package io.github.gaming32.rewindwatch.client;

import io.github.gaming32.rewindwatch.network.clientbound.ClientboundClearLockedStatePayload;
import io.github.gaming32.rewindwatch.network.clientbound.ClientboundEntityEffectPayload;
import io.github.gaming32.rewindwatch.network.clientbound.ClientboundLockedStatePayload;
import io.github.gaming32.rewindwatch.registry.RewindWatchAttachmentTypes;
import io.github.gaming32.rewindwatch.state.EntityEffect;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class RWClientNetworking {
    public static void handleEntityEffect(ClientboundEntityEffectPayload payload, IPayloadContext context) {
        final var entity = payload.getEntity(context);
        if (entity == null) return;
        if (payload.effect() != EntityEffect.Simple.NONE) {
            entity.setData(RewindWatchAttachmentTypes.ENTITY_EFFECT, payload.effect());
        } else {
            entity.removeData(RewindWatchAttachmentTypes.ENTITY_EFFECT);
        }

        final var minecraft = Minecraft.getInstance();
        if (entity == minecraft.cameraEntity && !minecraft.gameRenderer.getMainCamera().isDetached()) {
            minecraft.gameRenderer.checkEntityPostEffect(entity);
        }
    }

    public static void handleLockedState(ClientboundLockedStatePayload payload, IPayloadContext context) {
        final var entity = payload.getEntity(context);
        if (entity != null) {
            entity.setData(RewindWatchAttachmentTypes.LOCKED_PLAYER_STATE, payload.state());
        }
    }

    public static void handleClearLockedState(ClientboundClearLockedStatePayload payload, IPayloadContext context) {
        final var entity = payload.getEntity(context);
        if (entity != null) {
            entity.removeData(RewindWatchAttachmentTypes.LOCKED_PLAYER_STATE);
        }
    }
}
