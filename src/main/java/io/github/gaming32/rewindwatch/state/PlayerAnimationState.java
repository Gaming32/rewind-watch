package io.github.gaming32.rewindwatch.state;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record PlayerAnimationState(float position, float speed) {
    public static final PlayerAnimationState NONE = new PlayerAnimationState(0f, 0f);
    public static final StreamCodec<ByteBuf, PlayerAnimationState> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.FLOAT, PlayerAnimationState::position,
        ByteBufCodecs.FLOAT, PlayerAnimationState::speed,
        PlayerAnimationState::new
    );
}
