package io.github.gaming32.rewindwatch.registry;

import io.github.gaming32.annreg.RegisterFor;
import io.github.gaming32.annreg.value.RegValue;
import io.github.gaming32.rewindwatch.RewindWatch;
import io.github.gaming32.rewindwatch.state.EntityEffect;
import io.github.gaming32.rewindwatch.state.LivingAnimationState;
import io.github.gaming32.rewindwatch.state.PoseData;
import io.github.gaming32.rewindwatch.util.RWStreamCodecs;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

@RegisterFor(modid = RewindWatch.MOD_ID, registry = "neoforge:entity_data_serializers")
public class RewindWatchEntityDataSerializers {
    public static final Supplier<EntityDataSerializer<EntityEffect>> ENTITY_EFFECT = RegValue.of(() ->
        EntityDataSerializer.forValueType(EntityEffect.STREAM_CODEC)
    );
    public static final Supplier<EntityDataSerializer<Vec3>> VEC3 = RegValue.of(() ->
        EntityDataSerializer.forValueType(RWStreamCodecs.VEC3)
    );
    public static final Supplier<EntityDataSerializer<LivingAnimationState>> ANIMATION_STATE = RegValue.of(() ->
        EntityDataSerializer.forValueType(LivingAnimationState.STREAM_CODEC)
    );
    public static final Supplier<EntityDataSerializer<PoseData>> POSE_DATA = RegValue.of(() ->
        EntityDataSerializer.forValueType(PoseData.STREAM_CODEC)
    );
}
