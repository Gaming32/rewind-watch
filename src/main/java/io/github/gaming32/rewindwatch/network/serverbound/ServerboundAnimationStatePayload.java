package io.github.gaming32.rewindwatch.network.serverbound;

import io.github.gaming32.rewindwatch.ResourceLocations;
import io.github.gaming32.rewindwatch.state.LivingAnimationState;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ServerboundAnimationStatePayload(LivingAnimationState state) implements CustomPacketPayload {
    public static final Type<ServerboundAnimationStatePayload> TYPE =
        new Type<>(ResourceLocations.rewindWatch("animation_state"));
    public static final StreamCodec<ByteBuf, ServerboundAnimationStatePayload> STREAM_CODEC =
        LivingAnimationState.STREAM_CODEC.map(ServerboundAnimationStatePayload::new, ServerboundAnimationStatePayload::state);

    @Override
    public @NotNull Type<ServerboundAnimationStatePayload> type() {
        return TYPE;
    }
}
