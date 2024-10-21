package io.github.gaming32.rewindwatch.trigger;

import io.github.gaming32.annreg.RegisterFor;
import io.github.gaming32.annreg.value.RegValue;
import io.github.gaming32.rewindwatch.RewindWatch;

import java.util.function.Supplier;

@RegisterFor(modid = RewindWatch.MOD_ID, registry = "trigger_type")
public class RewindWatchCriteriaTriggers {
    public static final Supplier<WatchRecallTrigger> WATCH_RECALL = RegValue.of(WatchRecallTrigger::new);
}
