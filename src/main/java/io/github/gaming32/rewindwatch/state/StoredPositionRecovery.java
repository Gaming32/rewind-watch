package io.github.gaming32.rewindwatch.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record StoredPositionRecovery(GlobalLocation location, UUID fakePlayer) {
    public static final StoredPositionRecovery DEFAULT = new StoredPositionRecovery(GlobalLocation.DEFAULT, Util.NIL_UUID);

    public static final Codec<StoredPositionRecovery> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            GlobalLocation.CODEC.fieldOf("location").forGetter(StoredPositionRecovery::location),
            UUIDUtil.CODEC.fieldOf("fake_player").forGetter(StoredPositionRecovery::fakePlayer)
        ).apply(instance, StoredPositionRecovery::new)
    );

    public static StoredPositionRecovery defaultForLevel(@Nullable Level level) {
        if (level == null) {
            return DEFAULT;
        }
        return new StoredPositionRecovery(GlobalLocation.defaultForLevel(level), Util.NIL_UUID);
    }
}
