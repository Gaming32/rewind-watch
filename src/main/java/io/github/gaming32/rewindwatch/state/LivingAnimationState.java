package io.github.gaming32.rewindwatch.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;

public record LivingAnimationState(float position, float speed) {
    public static final LivingAnimationState NONE = new LivingAnimationState(0f, 0f);
    public static final Codec<LivingAnimationState> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.FLOAT.fieldOf("position").forGetter(LivingAnimationState::position),
            Codec.FLOAT.fieldOf("speed").forGetter(LivingAnimationState::speed)
        ).apply(instance, LivingAnimationState::new)
    );
    public static final StreamCodec<ByteBuf, LivingAnimationState> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.FLOAT, LivingAnimationState::position,
        ByteBufCodecs.FLOAT, LivingAnimationState::speed,
        LivingAnimationState::new
    );

    public static LivingAnimationState from(LivingEntity entity) {
        final var animation = entity.walkAnimation;
        return new LivingAnimationState(animation.position(), animation.speed());
    }

    public void apply(LivingEntity entity) {
        final var animation = entity.walkAnimation;
        animation.position = position;
        animation.setSpeed(animation.speedOld = speed);
    }
}
