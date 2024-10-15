package io.github.gaming32.rewindwatch.network.clientbound;

import io.github.gaming32.rewindwatch.ResourceLocations;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ClientboundClearLockedStatePayload(int entity) implements CustomPacketPayload, ClientboundPayloadWithEntity {
    public static final Type<ClientboundClearLockedStatePayload> TYPE =
        new Type<>(ResourceLocations.rewindWatch("clear_locked_state"));
    public static final StreamCodec<ByteBuf, ClientboundClearLockedStatePayload> STREAM_CODEC =
        ByteBufCodecs.VAR_INT.map(ClientboundClearLockedStatePayload::new, ClientboundClearLockedStatePayload::entity);

    @Override
    public @NotNull Type<ClientboundClearLockedStatePayload> type() {
        return TYPE;
    }
}
