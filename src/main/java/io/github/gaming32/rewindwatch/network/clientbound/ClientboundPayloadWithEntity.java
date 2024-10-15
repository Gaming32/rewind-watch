package io.github.gaming32.rewindwatch.network.clientbound;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public interface ClientboundPayloadWithEntity {
    Logger LOGGER = LogUtils.getLogger();

    int entity();

    @Nullable
    default Entity getEntity(IPayloadContext context) {
        final var entity = context.player().level().getEntity(entity());
        if (entity == null) {
            LOGGER.warn("Received unknown entity {} for {}", entity(), getClass().getSimpleName());
        }
        return entity;
    }
}
