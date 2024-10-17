package io.github.gaming32.rewindwatch.registry;

import io.github.gaming32.rewindwatch.ResourceLocations;
import io.github.gaming32.rewindwatch.RewindWatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class RewindWatchSoundEvents {
    private static final DeferredRegister<SoundEvent> REGISTER = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, RewindWatch.MOD_ID);

    public static final Supplier<SoundEvent> ITEM_REWIND_WATCH_SAVE = register("item.rewind_watch.save");
    public static final Supplier<SoundEvent> ITEM_REWIND_WATCH_RECALLED = register("item.rewind_watch.recalled");

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }

    private static Supplier<SoundEvent> register(String name) {
        return REGISTER.register(name, () -> SoundEvent.createVariableRangeEvent(ResourceLocations.rewindWatch(name)));
    }
}
