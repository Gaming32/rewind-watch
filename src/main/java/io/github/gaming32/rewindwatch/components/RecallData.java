package io.github.gaming32.rewindwatch.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.gaming32.rewindwatch.state.GlobalLocation;
import net.minecraft.core.UUIDUtil;

import java.util.UUID;

public record RecallData(GlobalLocation recallLocation, UUID fakePlayer) {
    public static final Codec<RecallData> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            GlobalLocation.CODEC.fieldOf("recall_location").forGetter(RecallData::recallLocation),
            UUIDUtil.CODEC.fieldOf("fake_player").forGetter(RecallData::fakePlayer)
        ).apply(instance, RecallData::new)
    );
}
