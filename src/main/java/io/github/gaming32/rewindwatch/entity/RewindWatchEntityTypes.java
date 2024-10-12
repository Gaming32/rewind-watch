package io.github.gaming32.rewindwatch.entity;

import io.github.gaming32.rewindwatch.RewindWatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class RewindWatchEntityTypes {
    private static final DeferredRegister<EntityType<?>> REGISTER =
        DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, RewindWatch.MOD_ID);

    public static final Supplier<EntityType<FakePlayerEntity>> FAKE_PLAYER = REGISTER.register(
        "fake_player", () ->
            EntityType.Builder.of(FakePlayerEntity::new, MobCategory.MISC)
                .sized(0.6f, 1.8f)
                .eyeHeight(1.62f)
                .vehicleAttachment(Player.DEFAULT_VEHICLE_ATTACHMENT)
                .clientTrackingRange(32)
                .build("rewindwatch:fake_player")
    );

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }
}
