package io.github.gaming32.rewindwatch.state;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.gaming32.rewindwatch.util.RWCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Set;

public record GlobalLocation(ResourceKey<Level> dimension, Vec3 position, Vec2 rotation) {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final GlobalLocation DEFAULT = new GlobalLocation(
        Level.OVERWORLD, new Vec3(8.5, 64.0, 8.5), Vec2.ZERO
    );

    public static final Codec<GlobalLocation> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(GlobalLocation::dimension),
            Vec3.CODEC.fieldOf("position").forGetter(GlobalLocation::position),
            RWCodecs.VEC2.fieldOf("rotation").forGetter(GlobalLocation::rotation)
        ).apply(instance, GlobalLocation::new)
    );

    public static GlobalLocation defaultForLevel(@Nullable Level level) {
        if (level == null) {
            return DEFAULT;
        }
        return new GlobalLocation(
            level.dimension(),
            Vec3.atBottomCenterOf(level.getSharedSpawnPos()),
            new Vec2(0f, level.getSharedSpawnAngle())
        );
    }

    public static GlobalLocation fromEntity(Entity entity) {
        return new GlobalLocation(entity.level().dimension(), entity.position(), entity.getRotationVector());
    }

    public boolean teleport(Entity entity) {
        if (!(entity.level() instanceof ServerLevel entityLevel)) {
            throw new IllegalStateException("Cannot teleport() entity on client");
        }
        final var targetLevel = entityLevel.getServer().getLevel(dimension);
        if (targetLevel == null) {
            LOGGER.warn("Failed to teleport {} to unknown dimension {}", entity, dimension);
            return false;
        }
        return entity.teleportTo(targetLevel, position.x, position.y, position.z, Set.of(), rotation.y, rotation.x);
    }
}
