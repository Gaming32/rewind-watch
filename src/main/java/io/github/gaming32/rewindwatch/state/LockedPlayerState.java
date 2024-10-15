package io.github.gaming32.rewindwatch.state;

import io.github.gaming32.rewindwatch.registry.RewindWatchAttachmentTypes;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public record LockedPlayerState(LivingFacingAngles facing, PlayerAnimationState animationState) {
    public static final LockedPlayerState NONE = new LockedPlayerState(LivingFacingAngles.ORIGIN, PlayerAnimationState.NONE);
    public static final StreamCodec<ByteBuf, LockedPlayerState> STREAM_CODEC = StreamCodec.composite(
        LivingFacingAngles.STREAM_CODEC, LockedPlayerState::facing,
        PlayerAnimationState.STREAM_CODEC, LockedPlayerState::animationState,
        LockedPlayerState::new
    );

    public static LockedPlayerState from(ServerPlayer player) {
        return new LockedPlayerState(
            LivingFacingAngles.from(player),
            player.getData(RewindWatchAttachmentTypes.PLAYER_ANIMATION_STATE)
        );
    }
}
