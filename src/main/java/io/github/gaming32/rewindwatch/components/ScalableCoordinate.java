package io.github.gaming32.rewindwatch.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.gaming32.rewindwatch.util.RWStreamCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

public record ScalableCoordinate(Vec3 pos, double scale) {
    public static final Codec<ScalableCoordinate> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Vec3.CODEC.fieldOf("pos").forGetter(ScalableCoordinate::pos),
            Codec.DOUBLE.fieldOf("scale").forGetter(ScalableCoordinate::scale)
        ).apply(instance, ScalableCoordinate::new)
    );
    public static final StreamCodec<ByteBuf, ScalableCoordinate> STREAM_CODEC = StreamCodec.composite(
        RWStreamCodecs.VEC3, ScalableCoordinate::pos,
        ByteBufCodecs.DOUBLE, ScalableCoordinate::scale,
        ScalableCoordinate::new
    );
}
