package io.github.gaming32.rewindwatch.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.gaming32.rewindwatch.registry.RewindWatchAttachmentTypes;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public record LockedPlayerState(LivingFacingAngles facing, PlayerAnimationState animation) {
    public static final LockedPlayerState NONE = new LockedPlayerState(LivingFacingAngles.ORIGIN, PlayerAnimationState.NONE);
    public static final Codec<LockedPlayerState> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            LivingFacingAngles.CODEC.fieldOf("facing").forGetter(LockedPlayerState::facing),
            PlayerAnimationState.CODEC
                .optionalFieldOf("animation", PlayerAnimationState.NONE)
                .forGetter(LockedPlayerState::animation)
        ).apply(instance, LockedPlayerState::new)
    );
    public static final StreamCodec<ByteBuf, LockedPlayerState> STREAM_CODEC = StreamCodec.composite(
        LivingFacingAngles.STREAM_CODEC, LockedPlayerState::facing,
        PlayerAnimationState.STREAM_CODEC, LockedPlayerState::animation,
        LockedPlayerState::new
    );

    public static LockedPlayerState from(ServerPlayer player) {
        return new LockedPlayerState(
            LivingFacingAngles.from(player),
            player.getData(RewindWatchAttachmentTypes.PLAYER_ANIMATION_STATE)
        );
    }
}
