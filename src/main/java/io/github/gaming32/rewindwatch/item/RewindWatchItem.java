package io.github.gaming32.rewindwatch.item;

import com.mojang.logging.LogUtils;
import io.github.gaming32.rewindwatch.components.RecallData;
import io.github.gaming32.rewindwatch.components.RewindWatchDataComponents;
import io.github.gaming32.rewindwatch.components.ScalableCoordinate;
import io.github.gaming32.rewindwatch.entity.FakePlayer;
import io.github.gaming32.rewindwatch.entity.RewindWatchEntityTypes;
import io.github.gaming32.rewindwatch.registry.RewindWatchSoundEvents;
import io.github.gaming32.rewindwatch.state.EntityEffect;
import io.github.gaming32.rewindwatch.state.GlobalLocation;
import io.github.gaming32.rewindwatch.state.LivingFacingAngles;
import io.github.gaming32.rewindwatch.state.LockedPlayerState;
import io.github.gaming32.rewindwatch.timer.RecallCompleteCallback;
import io.github.gaming32.rewindwatch.timer.RecallSoundCallback;
import io.github.gaming32.rewindwatch.util.RWAttachments;
import io.github.gaming32.rewindwatch.util.RWUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import static net.minecraft.SharedConstants.TICKS_PER_MINUTE;
import static net.minecraft.SharedConstants.TICKS_PER_SECOND;

public class RewindWatchItem extends Item {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int SAVE_TIME = 30;
    private static final int RECALL_SOUND_TIME = 27;
    private static final int POST_RECALL_COOLDOWN = 20;
    public static final int MAX_TELEPORT_TIME = TICKS_PER_MINUTE / 2;

    public RewindWatchItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
        @NotNull Level level,
        @NotNull Player player,
        @NotNull InteractionHand usedHand
    ) {
        final var item = player.getItemInHand(usedHand);
        final var owner = item.get(RewindWatchDataComponents.OWNER);
        if (owner != null && !owner.equals(player.getUUID())) {
            return InteractionResultHolder.pass(item);
        }
        if (!level.isClientSide) {
            final var recall = item.remove(RewindWatchDataComponents.RECALL_DATA);
            final var serverPlayer = (ServerPlayer)player;
            if (recall == null) {
                savePlayer(serverPlayer, item);
            } else {
                recallPlayer(serverPlayer, recall);
                item.remove(RewindWatchDataComponents.SCALABLE_COORDINATE);
            }
        }
        return InteractionResultHolder.sidedSuccess(item, level.isClientSide);
    }

    private void savePlayer(ServerPlayer player, ItemStack item) {
        final var level = player.serverLevel();
        final var time = level.getGameTime();

        final var markerPlayer = RewindWatchEntityTypes.FAKE_PLAYER.get().create(level);
        if (markerPlayer == null) {
            LOGGER.error("Failed to create fake player for {}", player);
            return;
        }
        markerPlayer.copyInformationFrom(player);
        markerPlayer.setCurrentEffect(new EntityEffect.Dissolve(
            time, time + SAVE_TIME, EntityEffect.Dissolve.Type.TRANSPARENT_GRAYSCALE, true
        ));
        item.set(RewindWatchDataComponents.OWNER, player.getUUID());
        item.set(RewindWatchDataComponents.SCALABLE_COORDINATE, new ScalableCoordinate(
            player.position(), level.dimensionType().coordinateScale()
        ));
        item.set(RewindWatchDataComponents.RECALL_DATA, new RecallData(
            GlobalLocation.fromEntity(player), markerPlayer.getUUID()
        ));
        level.addFreshEntity(markerPlayer);

        player.playNotifySound(RewindWatchSoundEvents.ITEM_REWIND_WATCH_SAVE.get(), SoundSource.PLAYERS, 1f, 1f);
        player.getCooldowns().addCooldown(this, SAVE_TIME);
    }

    private void recallPlayer(ServerPlayer player, RecallData recall) {
        final var originalLevel = player.serverLevel();
        final var newLevel = originalLevel.getServer().getLevel(recall.recallLocation().dimension());
        if (newLevel == null) {
            LOGGER.warn("Target dimension {} no longer exists", recall.recallLocation().dimension());
            player.sendSystemMessage(
                Component.translatable("rewindwatch.dimension_gone").withStyle(ChatFormatting.RED), true
            );
            return;
        }

        final var time = originalLevel.getGameTime();
        final var duration = computeDuration(
            originalLevel.dimensionType().coordinateScale(), player.position(),
            newLevel.dimensionType().coordinateScale(), recall.recallLocation().position()
        );
        final var endTime = time + duration;

        final var standInPlayer = RewindWatchEntityTypes.FAKE_PLAYER.get().create(originalLevel);
        if (standInPlayer == null) {
            LOGGER.warn("Failed to create fake player for {}", player);
        } else {
            standInPlayer.copyInformationFrom(player);
            standInPlayer.setCurrentEffect(new EntityEffect.Dissolve(
                time, endTime, EntityEffect.Dissolve.Type.TRANSPARENT, false
            ));
            originalLevel.addFreshEntity(standInPlayer);
        }

        if (!recall.recallLocation().teleport(player)) {
            throw new IllegalStateException("Teleportation failed");
        }

        final var markerPlayer = newLevel.getEntity(recall.fakePlayer());
        if (!(markerPlayer instanceof FakePlayer fakePlayer)) {
            LOGGER.warn(
                "Unable to find marker entity {} for {} (found {} instead). Unable to recover some data.",
                recall.fakePlayer(), player, markerPlayer
            );
            RWAttachments.lockMovement(player);
            RWAttachments.setEntityEffect(player, new EntityEffect.Dissolve(
                time, endTime, EntityEffect.Dissolve.Type.TRANSPARENT, true
            ));
        } else {
            RWAttachments.lockMovement(player, new LockedPlayerState(
                LivingFacingAngles.from(fakePlayer),
                fakePlayer.getAnimationState(),
                RWUtils.unpackModelCustomization(fakePlayer.getModelCustomization()),
                fakePlayer.getCloak(),
                fakePlayer.getPoseData(),
                fakePlayer.getDeltaMovement(),
                fakePlayer.onGround()
            ));
            fakePlayer.reapplySomeData(player);
            markerPlayer.discard();
            RWAttachments.setEntityEffect(player, new EntityEffect.Dissolve(
                time, endTime, EntityEffect.Dissolve.Type.GRAYSCALE, true
            ));
        }

        final var queue = player.server.getWorldData().overworldData().getScheduledEvents();
        queue.schedule(
            "Recall Sound for " + player.getUUID(),
            endTime - RECALL_SOUND_TIME,
            new RecallSoundCallback(player.getUUID())
        );
        queue.schedule(
            "Recall Complete for " + player.getUUID(),
            endTime,
            new RecallCompleteCallback(player.getUUID())
        );

        player.getCooldowns().addCooldown(this, duration + POST_RECALL_COOLDOWN);
    }

    public static int computeDuration(
        double sourceScale, Vec3 sourcePos,
        double destScale, Vec3 destPos
    ) {
        final var distance = sourcePos.scale(sourceScale / destScale).distanceTo(destPos);
        final var time = Math.round(distance / 50 * TICKS_PER_SECOND + TICKS_PER_SECOND / 2.0);
        return Math.clamp(time, TICKS_PER_SECOND / 2, MAX_TELEPORT_TIME);
    }
}
