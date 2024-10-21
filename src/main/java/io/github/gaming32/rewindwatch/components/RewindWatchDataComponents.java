package io.github.gaming32.rewindwatch.components;

import io.github.gaming32.annreg.RegisterFor;
import io.github.gaming32.annreg.value.ItemComponentTypeValue;
import io.github.gaming32.rewindwatch.RewindWatch;
import net.minecraft.core.UUIDUtil;

import java.util.UUID;

@RegisterFor(modid = RewindWatch.MOD_ID, registry = "data_component_type")
public class RewindWatchDataComponents {
    public static final ItemComponentTypeValue<UUID> OWNER = ItemComponentTypeValue.of(builder -> builder
        .persistent(UUIDUtil.LENIENT_CODEC)
        .networkSynchronized(UUIDUtil.STREAM_CODEC)
    );
    public static final ItemComponentTypeValue<ScalableCoordinate> SCALABLE_COORDINATE = ItemComponentTypeValue.of(builder -> builder
        .persistent(ScalableCoordinate.CODEC)
        .networkSynchronized(ScalableCoordinate.STREAM_CODEC)
    );
    public static final ItemComponentTypeValue<RecallData> RECALL_DATA = ItemComponentTypeValue.of(builder -> builder
        .persistent(RecallData.CODEC)
    );
}
