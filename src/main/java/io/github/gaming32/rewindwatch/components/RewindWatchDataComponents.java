package io.github.gaming32.rewindwatch.components;

import io.github.gaming32.annreg.RegisterFor;
import io.github.gaming32.annreg.value.RegValue;
import io.github.gaming32.rewindwatch.RewindWatch;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;

import java.util.UUID;
import java.util.function.Supplier;

@RegisterFor(modid = RewindWatch.MOD_ID, registry = "data_component_type")
public class RewindWatchDataComponents {
    public static final Supplier<DataComponentType<UUID>> OWNER = RegValue.ofDataComponent(builder -> builder
        .persistent(UUIDUtil.LENIENT_CODEC)
        .networkSynchronized(UUIDUtil.STREAM_CODEC)
    );
    public static final Supplier<DataComponentType<ScalableCoordinate>> SCALABLE_COORDINATE = RegValue.ofDataComponent(builder -> builder
        .persistent(ScalableCoordinate.CODEC)
        .networkSynchronized(ScalableCoordinate.STREAM_CODEC)
    );
    public static final Supplier<DataComponentType<RecallData>> RECALL_DATA = RegValue.ofDataComponent(builder -> builder
        .persistent(RecallData.CODEC)
    );
}
