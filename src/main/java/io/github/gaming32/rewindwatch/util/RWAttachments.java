package io.github.gaming32.rewindwatch.util;

import io.github.gaming32.rewindwatch.entity.FakePlayer;
import io.github.gaming32.rewindwatch.network.clientbound.ClientboundClearLockedStatePayload;
import io.github.gaming32.rewindwatch.network.clientbound.ClientboundEntityEffectPayload;
import io.github.gaming32.rewindwatch.network.clientbound.ClientboundLockedStatePayload;
import io.github.gaming32.rewindwatch.registry.RewindWatchAttachmentTypes;
import io.github.gaming32.rewindwatch.state.EntityEffect;
import io.github.gaming32.rewindwatch.state.LockedPlayerState;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Objects;

public class RWAttachments {
    public static void setEntityEffect(LivingEntity entity, EntityEffect effect) {
        if (entity.level().isClientSide) {
            throw new IllegalStateException("Cannot use setEntityEffect on client");
        }
        if (entity instanceof FakePlayer fakePlayer) {
            fakePlayer.setCurrentEffect(effect);
            return;
        }
        final var old = effect != EntityEffect.Simple.NONE
            ? entity.setData(RewindWatchAttachmentTypes.ENTITY_EFFECT, effect)
            : entity.removeData(RewindWatchAttachmentTypes.ENTITY_EFFECT);
        if (!Objects.equals(effect, old)) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                entity, new ClientboundEntityEffectPayload(entity.getId(), effect)
            );
        }
    }

    public static EntityEffect getEntityEffect(Entity entity) {
        if (!(entity instanceof LivingEntity)) {
            return EntityEffect.Simple.NONE;
        }
        if (entity instanceof FakePlayer fakePlayer) {
            return fakePlayer.getCurrentEffect();
        }
        return entity
            .getExistingData(RewindWatchAttachmentTypes.ENTITY_EFFECT)
            .orElse(EntityEffect.Simple.NONE);
    }

    public static void lockMovement(ServerPlayer player) {
        lockMovement(player, LockedPlayerState.from(player));
    }

    public static void lockMovement(ServerPlayer player, LockedPlayerState state) {
        player.setData(RewindWatchAttachmentTypes.LOCKED_PLAYER_STATE, state);
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new ClientboundLockedStatePayload(player.getId(), state));
    }
    public static void unlockMovement(ServerPlayer player) {
        player.removeData(RewindWatchAttachmentTypes.LOCKED_PLAYER_STATE);
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new ClientboundClearLockedStatePayload(player.getId()));
    }
}
