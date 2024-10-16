package io.github.gaming32.rewindwatch.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;

public record PoseData(int fallFlyTicks, float swimAmount) {
    public static final PoseData NONE = new PoseData(0, 0f);

    public static final Codec<PoseData> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Codec.INT.fieldOf("fall_fly_ticks").forGetter(PoseData::fallFlyTicks),
            Codec.FLOAT.fieldOf("swim_amount").forGetter(PoseData::swimAmount)
        ).apply(instance, PoseData::new)
    );
    public static final StreamCodec<ByteBuf, PoseData> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, PoseData::fallFlyTicks,
        ByteBufCodecs.FLOAT, PoseData::swimAmount,
        PoseData::new
    );

    public static PoseData fromEntity(LivingEntity entity) {
        return new PoseData(entity.fallFlyTicks, entity.swimAmount);
    }
}
