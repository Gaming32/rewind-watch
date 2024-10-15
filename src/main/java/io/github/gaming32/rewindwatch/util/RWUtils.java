package io.github.gaming32.rewindwatch.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class RWUtils {
    public static final StreamCodec<ByteBuf, Vec3> VEC3_STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.DOUBLE, Vec3::x,
        ByteBufCodecs.DOUBLE, Vec3::y,
        ByteBufCodecs.DOUBLE, Vec3::z,
        Vec3::new
    );

    public static Vec3 getVec3(CompoundTag tag, String key) {
        final var list = tag.getList(key, Tag.TAG_DOUBLE);
        return new Vec3(list.getDouble(0), list.getDouble(1), list.getDouble(2));
    }

    public static void putVec3(CompoundTag tag, String key, Vec3 value) {
        final var list = new ListTag();
        list.add(DoubleTag.valueOf(value.x));
        list.add(DoubleTag.valueOf(value.y));
        list.add(DoubleTag.valueOf(value.z));
        tag.put(key, list);
    }

    public static void sendPackets(ServerPlayer player, List<CustomPacketPayload> packets) {
        switch (packets.size()) {
            case 0 -> {}
            case 1 -> player.connection.send(packets.getFirst());
            default -> player.connection.sendBundled(packets);
        }
    }
}
