package io.github.gaming32.rewindwatch.network;

import io.github.gaming32.rewindwatch.PlayerAnimationState;
import io.github.gaming32.rewindwatch.ResourceLocations;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record AnimationStatePayload(PlayerAnimationState state) implements CustomPacketPayload {
    public static final Type<AnimationStatePayload> TYPE = new Type<>(ResourceLocations.rewindWatch("animation_state"));
    public static final StreamCodec<ByteBuf, AnimationStatePayload> STREAM_CODEC =
        PlayerAnimationState.STREAM_CODEC.map(AnimationStatePayload::new, AnimationStatePayload::state);

    @Override
    public @NotNull Type<AnimationStatePayload> type() {
        return TYPE;
    }
}
