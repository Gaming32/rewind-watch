package io.github.gaming32.rewindwatch.entity;

import io.github.gaming32.annreg.RegisterFor;
import io.github.gaming32.annreg.value.RegValues;
import io.github.gaming32.rewindwatch.RewindWatch;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;

import java.util.function.Supplier;

@RegisterFor(modid = RewindWatch.MOD_ID, registry = "entity_type")
public class RewindWatchEntityTypes {
    public static final Supplier<EntityType<FakePlayer>> FAKE_PLAYER = RegValues.ofEntityType(() ->
        EntityType.Builder.of(FakePlayer::new, MobCategory.MISC)
            .sized(0.6f, 1.8f)
            .eyeHeight(1.62f)
            .vehicleAttachment(Player.DEFAULT_VEHICLE_ATTACHMENT)
            .clientTrackingRange(32)
    );
}
