package io.github.gaming32.rewindwatch.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public class RWStreamCodecs {
    public static final StreamCodec<ByteBuf, Set<PlayerModelPart>> PLAYER_MODEL_PART_SET =
        ByteBufCodecs.BYTE.map(RWUtils::unpackModelCustomization, RWUtils::packModelCustomization);

    public static final StreamCodec<ByteBuf, Vec3> VEC3 = StreamCodec.composite(
        ByteBufCodecs.DOUBLE, Vec3::x,
        ByteBufCodecs.DOUBLE, Vec3::y,
        ByteBufCodecs.DOUBLE, Vec3::z,
        Vec3::new
    );
}
