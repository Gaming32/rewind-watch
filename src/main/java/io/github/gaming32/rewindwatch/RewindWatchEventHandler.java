package io.github.gaming32.rewindwatch;

import io.github.gaming32.rewindwatch.components.RewindWatchDataComponents;
import io.github.gaming32.rewindwatch.item.RewindWatchItem;
import io.github.gaming32.rewindwatch.network.clientbound.ClientboundEntityEffectPayload;
import io.github.gaming32.rewindwatch.network.clientbound.ClientboundLockedStatePayload;
import io.github.gaming32.rewindwatch.registry.RewindWatchAttachmentTypes;
import io.github.gaming32.rewindwatch.state.EntityEffect;
import io.github.gaming32.rewindwatch.util.RWAttachments;
import io.github.gaming32.rewindwatch.util.RWUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@EventBusSubscriber(modid = RewindWatch.MOD_ID)
public class RewindWatchEventHandler {
    @SubscribeEvent
    public static void startTracking(PlayerEvent.StartTracking event) {
        syncAttachments(event.getTarget(), event.getEntity());
    }

    @SubscribeEvent
    public static void loggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        syncAttachments(event.getEntity(), event.getEntity());
        refreshOwnedChunks((ServerPlayer)event.getEntity());
    }

    private static void syncAttachments(Entity newEntity, Player player) {
        final var packets = new ArrayList<CustomPacketPayload>();

        final var effect = RWAttachments.getEntityEffect(newEntity);
        if (effect != EntityEffect.Simple.NONE) {
            packets.add(new ClientboundEntityEffectPayload(newEntity.getId(), effect));
        }

        newEntity.getExistingData(RewindWatchAttachmentTypes.LOCKED_PLAYER_STATE)
            .map(state -> new ClientboundLockedStatePayload(newEntity.getId(), state))
            .ifPresent(packets::add);

        RWUtils.sendPackets((ServerPlayer)player, packets);
    }

    @SubscribeEvent
    public static void entityTick(EntityTickEvent.Pre event) {
        if (!(event.getEntity() instanceof LivingEntity entity) || entity.level().isClientSide) return;
        final var effect = RWAttachments.getEntityEffect(entity);
        if (
            effect instanceof EntityEffect.Dissolve dissolve &&
            entity.level().getGameTime() >= dissolve.endTick()
        ) {
            if (dissolve.in()) {
                switch (dissolve.type()) {
                    case TRANSPARENT, GRAYSCALE -> RWAttachments.setEntityEffect(entity, EntityEffect.Simple.NONE);
                    case TRANSPARENT_GRAYSCALE -> RWAttachments.setEntityEffect(entity, EntityEffect.Simple.GRAYSCALE);
                }
            } else if (dissolve.type() == EntityEffect.Dissolve.Type.GRAYSCALE) {
                RWAttachments.setEntityEffect(entity, EntityEffect.Simple.GRAYSCALE);
            }
        }
    }

    // We need the player as context, which isn't passed to appendHoverText
    @SubscribeEvent
    public static void appendWatchTooltip(ItemTooltipEvent event) {
        final var player = event.getEntity();
        final var item = event.getItemStack();
        final var result = event.getToolTip();
        if (player == null || event.getFlags().isCreative()) return;

        final var owner = item.get(RewindWatchDataComponents.OWNER);
        final var coordinate = item.get(RewindWatchDataComponents.SCALABLE_COORDINATE);
        if (owner == null) {
            result.add(
                Component.translatable(RWTranslationKeys.REWIND_WATCH_VISIBLE, "00:00")
                    .withStyle(ChatFormatting.DARK_GREEN)
            );
        } else if (!owner.equals(player.getUUID())) {
            result.add(Component.translatable(RWTranslationKeys.REWIND_WATCH_HIDDEN).withStyle(ChatFormatting.RED));
        } else if (coordinate != null) {
            final var duration = RewindWatchItem.computeDuration(
                player.level().dimensionType().coordinateScale(), player.position(),
                coordinate.scale(), coordinate.pos()
            );
            final var minutesInHalfDay = 60 * 12;
            final var scaledDuration = (double)duration / (RewindWatchItem.MAX_TELEPORT_TIME + 1) * minutesInHalfDay;
            result.add(Component.translatable(
                RWTranslationKeys.REWIND_WATCH_VISIBLE,
                RWUtils.minutesToHoursMinutes((long)scaledDuration)
            ).withStyle(ChatFormatting.DARK_GREEN));
        }
    }

    @SubscribeEvent
    public static void playerTick(PlayerTickEvent.Pre event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.tickCount == 0) return;
        final var refreshInterval = RewindWatchTicketTypes.FAKE_PLAYER.timeout();
        if (player.tickCount % refreshInterval != refreshInterval - 1) return;
        refreshOwnedChunks(player);
    }

    @SubscribeEvent
    public static void playerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        refreshOwnedChunks(player);
    }

    private static void refreshOwnedChunks(ServerPlayer player) {
        final var originalSet = player.getExistingData(RewindWatchAttachmentTypes.OWNED_CHUNKS).orElse(Set.of());
        Set<GlobalPos> newSet = null;
        for (final var pos : originalSet) {
            final var level = player.server.getLevel(pos.dimension());
            if (level == null) {
                if (newSet == null) {
                    newSet = new HashSet<>(originalSet);
                }
                newSet.remove(pos);
                continue;
            }
            final var chunk = new ChunkPos(pos.pos());
            level.getChunkSource().addRegionTicket(RewindWatchTicketTypes.FAKE_PLAYER, chunk, 0, chunk);
        }
        if (newSet != null) {
            player.setData(RewindWatchAttachmentTypes.OWNED_CHUNKS, newSet);
        }
    }
}
