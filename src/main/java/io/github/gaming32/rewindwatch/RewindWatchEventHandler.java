package io.github.gaming32.rewindwatch;

import io.github.gaming32.rewindwatch.network.ClientboundEntityEffectPayload;
import io.github.gaming32.rewindwatch.util.RWAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = RewindWatch.MOD_ID)
public class RewindWatchEventHandler {
    @SubscribeEvent
    public static void startTracking(PlayerEvent.StartTracking event) {
        syncEntityEffect(event.getTarget(), event.getEntity());
    }

    @SubscribeEvent
    public static void loggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        syncEntityEffect(event.getEntity(), event.getEntity());
    }

    private static void syncEntityEffect(Entity effectHolder, Player player) {
        final var effect = RWAttachments.getEntityEffect(effectHolder);
        if (effect != EntityEffect.Simple.NONE) {
            ((ServerPlayer)player).connection.send(
                new ClientboundEntityEffectPayload(effectHolder.getId(), effect)
            );
        }
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
}
