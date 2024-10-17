package io.github.gaming32.rewindwatch.components;

import io.github.gaming32.rewindwatch.RewindWatch;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.UUID;
import java.util.function.Supplier;

public class RewindWatchDataComponents {
    private static final DeferredRegister.DataComponents REGISTER =
        DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, RewindWatch.MOD_ID);

    public static final Supplier<DataComponentType<UUID>> OWNER = REGISTER.registerComponentType(
        "owner", builder -> builder
            .persistent(UUIDUtil.LENIENT_CODEC)
            .networkSynchronized(UUIDUtil.STREAM_CODEC)
    );
    public static final Supplier<DataComponentType<ScalableCoordinate>> SCALABLE_COORDINATE = REGISTER.registerComponentType(
        "scalable_coordinate", builder -> builder
            .persistent(ScalableCoordinate.CODEC)
            .networkSynchronized(ScalableCoordinate.STREAM_CODEC)
    );
    public static final Supplier<DataComponentType<RecallData>> RECALL_DATA = REGISTER.registerComponentType(
        "recall_data", builder -> builder.persistent(RecallData.CODEC)
    );

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }
}
