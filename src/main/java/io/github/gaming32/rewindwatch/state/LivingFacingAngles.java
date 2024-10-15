package io.github.gaming32.rewindwatch.state;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;

public record LivingFacingAngles(float x, float y, float bodyY, float headY) {
    public static final LivingFacingAngles ORIGIN = new LivingFacingAngles(0f, 0f, 0f, 0f);
    public static final StreamCodec<ByteBuf, LivingFacingAngles> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.FLOAT, LivingFacingAngles::x,
        ByteBufCodecs.FLOAT, LivingFacingAngles::y,
        ByteBufCodecs.FLOAT, LivingFacingAngles::bodyY,
        ByteBufCodecs.FLOAT, LivingFacingAngles::headY,
        LivingFacingAngles::new
    );

    public static LivingFacingAngles from(LivingEntity entity) {
        return new LivingFacingAngles(
            entity.getXRot(),
            entity.getYRot(),
            entity.yBodyRot,
            entity.getYHeadRot()
        );
    }
}
