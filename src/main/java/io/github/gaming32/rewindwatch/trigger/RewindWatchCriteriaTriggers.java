package io.github.gaming32.rewindwatch.trigger;

import io.github.gaming32.rewindwatch.RewindWatch;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class RewindWatchCriteriaTriggers {
    private static final DeferredRegister<CriterionTrigger<?>> REGISTER =
        DeferredRegister.create(BuiltInRegistries.TRIGGER_TYPES, RewindWatch.MOD_ID);

    public static final Supplier<WatchRecallTrigger> WATCH_RECALL = REGISTER.register("watch_recall", WatchRecallTrigger::new);

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }
}
