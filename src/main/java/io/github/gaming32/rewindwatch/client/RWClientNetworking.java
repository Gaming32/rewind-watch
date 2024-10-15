package io.github.gaming32.rewindwatch.client;

import com.mojang.logging.LogUtils;
import io.github.gaming32.rewindwatch.EntityEffect;
import io.github.gaming32.rewindwatch.network.ClientboundEntityEffectPayload;
import io.github.gaming32.rewindwatch.registry.RewindWatchAttachmentTypes;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.slf4j.Logger;

public class RWClientNetworking {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void handleEntityEffect(ClientboundEntityEffectPayload payload, IPayloadContext context) {
        final var entity = context.player().level().getEntity(payload.entity());
        if (entity == null) {
            LOGGER.warn("Received entity effect for unknown entity {}", payload.entity());
            return;
        }
        if (payload.effect() != EntityEffect.Simple.NONE) {
            entity.setData(RewindWatchAttachmentTypes.ENTITY_EFFECT, payload.effect());
        } else {
            entity.removeData(RewindWatchAttachmentTypes.ENTITY_EFFECT);
        }

        final var minecraft = Minecraft.getInstance();
        if (entity == minecraft.cameraEntity) {
            minecraft.gameRenderer.checkEntityPostEffect(entity);
        }
    }
}
