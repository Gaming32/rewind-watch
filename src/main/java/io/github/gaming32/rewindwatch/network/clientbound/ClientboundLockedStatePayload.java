package io.github.gaming32.rewindwatch.network.clientbound;

import io.github.gaming32.rewindwatch.ResourceLocations;
import io.github.gaming32.rewindwatch.state.LockedPlayerState;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ClientboundLockedStatePayload(
    int entity, LockedPlayerState state
) implements CustomPacketPayload, ClientboundPayloadWithEntity {
    public static final Type<ClientboundLockedStatePayload> TYPE =
        new Type<>(ResourceLocations.rewindWatch("locked_state"));
    public static final StreamCodec<ByteBuf, ClientboundLockedStatePayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, ClientboundLockedStatePayload::entity,
        LockedPlayerState.STREAM_CODEC, ClientboundLockedStatePayload::state,
        ClientboundLockedStatePayload::new
    );

    @Override
    public @NotNull Type<ClientboundLockedStatePayload> type() {
        return TYPE;
    }
}
