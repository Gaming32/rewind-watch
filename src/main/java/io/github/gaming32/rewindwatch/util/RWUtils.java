package io.github.gaming32.rewindwatch.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class RWUtils {
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

    public static Set<PlayerModelPart> unpackModelCustomization(byte customization) {
        return unpackModelCustomization(customization & 0xff);
    }

    public static Set<PlayerModelPart> unpackModelCustomization(int customization) {
        final var result = EnumSet.noneOf(PlayerModelPart.class);
        for (final var part : PlayerModelPart.values()) {
            if ((customization & part.getMask()) != 0) {
                result.add(part);
            }
        }
        return result;
    }

    public static int packModelCustomization(Set<PlayerModelPart> parts) {
        var result = 0;
        for (final var part : parts) {
            result |= part.getMask();
        }
        return result;
    }
}
