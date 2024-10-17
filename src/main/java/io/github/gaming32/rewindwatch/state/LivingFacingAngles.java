package io.github.gaming32.rewindwatch.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public record LivingFacingAngles(float x, float y, float bodyY, float headY) {
    public static final LivingFacingAngles ORIGIN = new LivingFacingAngles(0f, 0f, 0f, 0f);
    public static final Codec<LivingFacingAngles> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.FLOAT.fieldOf("x").forGetter(LivingFacingAngles::x),
            Codec.FLOAT.fieldOf("y").forGetter(LivingFacingAngles::y),
            Codec.FLOAT.fieldOf("body_y").forGetter(LivingFacingAngles::bodyY),
            Codec.FLOAT.fieldOf("head_y").forGetter(LivingFacingAngles::headY)
        ).apply(instance, LivingFacingAngles::new)
    );
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

    public void apply(Entity entity) {
        entity.setXRot(entity.xRotO = x);
        entity.setYRot(entity.yRotO = y);
        if (entity instanceof LivingEntity living) {
            living.yBodyRot = living.yBodyRotO = bodyY;
            living.yHeadRot = living.yHeadRotO = headY;
        }
    }
}
