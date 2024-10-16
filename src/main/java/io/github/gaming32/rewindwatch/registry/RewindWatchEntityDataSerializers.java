package io.github.gaming32.rewindwatch.registry;

import io.github.gaming32.rewindwatch.RewindWatch;
import io.github.gaming32.rewindwatch.state.EntityEffect;
import io.github.gaming32.rewindwatch.state.LivingAnimationState;
import io.github.gaming32.rewindwatch.state.PoseData;
import io.github.gaming32.rewindwatch.util.RWStreamCodecs;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class RewindWatchEntityDataSerializers {
    private static final DeferredRegister<EntityDataSerializer<?>> REGISTER =
        DeferredRegister.create(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, RewindWatch.MOD_ID);

    public static final Supplier<EntityDataSerializer<EntityEffect>> ENTITY_EFFECT = REGISTER.register(
        "entity_effect", () -> EntityDataSerializer.forValueType(EntityEffect.STREAM_CODEC)
    );
    public static final Supplier<EntityDataSerializer<Vec3>> VEC3 = REGISTER.register(
        "vec3", () -> EntityDataSerializer.forValueType(RWStreamCodecs.VEC3)
    );
    public static final Supplier<EntityDataSerializer<LivingAnimationState>> ANIMATION_STATE = REGISTER.register(
        "animation_state", () -> EntityDataSerializer.forValueType(LivingAnimationState.STREAM_CODEC)
    );
    public static final Supplier<EntityDataSerializer<PoseData>> POSE_DATA = REGISTER.register(
        "pose_data", () -> EntityDataSerializer.forValueType(PoseData.STREAM_CODEC)
    );

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }
}
