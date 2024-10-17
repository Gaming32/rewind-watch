package io.github.gaming32.rewindwatch.item;

import com.mojang.logging.LogUtils;
import io.github.gaming32.rewindwatch.entity.FakePlayer;
import io.github.gaming32.rewindwatch.entity.RewindWatchEntityTypes;
import io.github.gaming32.rewindwatch.registry.RewindWatchAttachmentTypes;
import io.github.gaming32.rewindwatch.registry.RewindWatchSoundEvents;
import io.github.gaming32.rewindwatch.state.EntityEffect;
import io.github.gaming32.rewindwatch.state.GlobalLocation;
import io.github.gaming32.rewindwatch.state.LivingFacingAngles;
import io.github.gaming32.rewindwatch.state.LockedPlayerState;
import io.github.gaming32.rewindwatch.state.StoredPositionRecovery;
import io.github.gaming32.rewindwatch.timer.RecallCompleteCallback;
import io.github.gaming32.rewindwatch.timer.RecallSoundCallback;
import io.github.gaming32.rewindwatch.util.RWAttachments;
import io.github.gaming32.rewindwatch.util.RWUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
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
        if (!level.isClientSide) {
            final var recovery = player.removeData(RewindWatchAttachmentTypes.STORED_POSITION_RECOVERY);
            final var serverPlayer = (ServerPlayer)player;
            if (recovery == null) {
                savePlayer(serverPlayer);
            } else {
                recallPlayer(serverPlayer, recovery);
            }
        }
        return InteractionResultHolder.sidedSuccess(item, level.isClientSide);
    }

    private void savePlayer(ServerPlayer player) {
        final var level = player.serverLevel();
        final var time = level.getGameTime();

        final var entity = RewindWatchEntityTypes.FAKE_PLAYER.get().create(level);
        if (entity == null) {
            LOGGER.error("Failed to create fake player for {}", player);
            return;
        }
        entity.copyInformationFrom(player);
        entity.setCurrentEffect(new EntityEffect.Dissolve(
            time, time + SAVE_TIME, EntityEffect.Dissolve.Type.TRANSPARENT_GRAYSCALE, true
        ));
        player.setData(RewindWatchAttachmentTypes.STORED_POSITION_RECOVERY, new StoredPositionRecovery(
            GlobalLocation.fromEntity(player), entity.getUUID()
        ));
        level.addFreshEntity(entity);

        player.playNotifySound(RewindWatchSoundEvents.ITEM_REWIND_WATCH_SAVE.get(), SoundSource.PLAYERS, 1f, 1f);
        player.getCooldowns().addCooldown(this, SAVE_TIME);
    }

    private void recallPlayer(ServerPlayer player, StoredPositionRecovery recovery) {
        final var originalLevel = player.serverLevel();
        final var newLevel = originalLevel.getServer().getLevel(recovery.location().dimension());
        if (newLevel == null) {
            LOGGER.warn("Target dimension {} no longer exists", recovery.location().dimension());
            player.sendSystemMessage(
                Component.translatable("rewindwatch.dimension_gone").withStyle(ChatFormatting.RED), true
            );
            return;
        }

        final var time = originalLevel.getGameTime();
        final var duration = computeDuration(originalLevel, player.position(), newLevel, recovery.location().position());
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

        if (!recovery.location().teleport(player)) {
            throw new IllegalStateException("Teleportation failed");
        }

        final var markerPlayer = newLevel.getEntity(recovery.fakePlayer());
        if (!(markerPlayer instanceof FakePlayer fakePlayer)) {
            LOGGER.warn(
                "Unable to find marker entity {} for {} (found {} instead). Unable to recover some data.",
                recovery.fakePlayer(), player, markerPlayer
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

    private static int computeDuration(ServerLevel sourceLevel, Vec3 sourcePos, ServerLevel destLevel, Vec3 destPos) {
        final var distance = sourcePos.distanceTo(destPos) *
                             DimensionType.getTeleportationScale(sourceLevel.dimensionType(), destLevel.dimensionType());
        final var time = Math.round(distance / 50 * TICKS_PER_SECOND + TICKS_PER_SECOND / 2.0);
        return Math.clamp(time, TICKS_PER_SECOND / 2, TICKS_PER_MINUTE / 2);
    }
}
