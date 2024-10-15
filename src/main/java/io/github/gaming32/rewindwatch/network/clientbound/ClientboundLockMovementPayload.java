package io.github.gaming32.rewindwatch.network.clientbound;

import io.github.gaming32.rewindwatch.ResourceLocations;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ClientboundLockMovementPayload(boolean lock) implements CustomPacketPayload {
    public static final Type<ClientboundLockMovementPayload> TYPE =
        new Type<>(ResourceLocations.rewindWatch("lock_movement"));
    public static final StreamCodec<ByteBuf, ClientboundLockMovementPayload> STREAM_CODEC =
        ByteBufCodecs.BOOL.map(ClientboundLockMovementPayload::new, ClientboundLockMovementPayload::lock);

    @Override
    public @NotNull Type<ClientboundLockMovementPayload> type() {
        return TYPE;
    }
}
