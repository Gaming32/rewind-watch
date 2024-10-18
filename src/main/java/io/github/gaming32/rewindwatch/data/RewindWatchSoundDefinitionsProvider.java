package io.github.gaming32.rewindwatch.data;

import io.github.gaming32.rewindwatch.RWTranslationKeys;
import io.github.gaming32.rewindwatch.ResourceLocations;
import io.github.gaming32.rewindwatch.RewindWatch;
import io.github.gaming32.rewindwatch.registry.RewindWatchSoundEvents;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

public class RewindWatchSoundDefinitionsProvider extends SoundDefinitionsProvider {
    protected RewindWatchSoundDefinitionsProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, RewindWatch.MOD_ID, existingFileHelper);
    }

    @Override
    public void registerSounds() {
        add(RewindWatchSoundEvents.ITEM_REWIND_WATCH_SAVE, definition()
            .subtitle(RWTranslationKeys.REWIND_WATCH_USED)
            .with(sound(ResourceLocations.minecraft("block/amethyst/step1")))
        );
        add(RewindWatchSoundEvents.ITEM_REWIND_WATCH_RECALLED, definition()
            .subtitle(RWTranslationKeys.REWIND_WATCH_RECALLED)
            .with(sound(ResourceLocations.rewindWatch("item/rewind_watch/recalled")))
        );
    }
}
