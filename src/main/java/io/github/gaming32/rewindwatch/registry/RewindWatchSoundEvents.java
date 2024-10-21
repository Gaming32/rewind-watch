package io.github.gaming32.rewindwatch.registry;

import io.github.gaming32.annreg.RegisterFor;
import io.github.gaming32.annreg.RegistryId;
import io.github.gaming32.annreg.value.RegValues;
import io.github.gaming32.rewindwatch.RewindWatch;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

@RegisterFor(modid = RewindWatch.MOD_ID, registry = "sound_event")
public class RewindWatchSoundEvents {
    @RegistryId("item.rewind_watch.save")
    public static final Supplier<SoundEvent> ITEM_REWIND_WATCH_SAVE = RegValues.ofVariableRangeSoundEvent();
    @RegistryId("item.rewind_watch.recalled")
    public static final Supplier<SoundEvent> ITEM_REWIND_WATCH_RECALLED = RegValues.ofVariableRangeSoundEvent();
}
