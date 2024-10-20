package io.github.gaming32.rewindwatch.data;

import io.github.gaming32.rewindwatch.RWTranslationKeys;
import io.github.gaming32.rewindwatch.RewindWatch;
import io.github.gaming32.rewindwatch.item.RewindWatchItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class RewindWatchLanguageProvider extends LanguageProvider {
    public RewindWatchLanguageProvider(PackOutput output) {
        super(output, RewindWatch.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        addItem(RewindWatchItems.REWIND_WATCH, "Rewind Watch");
        add(RWTranslationKeys.REWIND_WATCH_USED, "Rewind Watch Used");
        add(RWTranslationKeys.REWIND_WATCH_RECALLED, "Player Recalled");
        add(RWTranslationKeys.REWIND_WATCH_VISIBLE, "The watch reads %s");
        add(RWTranslationKeys.REWIND_WATCH_HIDDEN, "The watch is impossible to make out");

        add("rewindwatch.dimension_gone", "The dimension you are recalling to no longer exists");

        add(RewindWatchAdvancementProvider.RECALL_TITLE, "That Felt Strange");
        add(RewindWatchAdvancementProvider.RECALL_DESC, "Recall yourself to a previous location using a Rewind Watch");
        add(RewindWatchAdvancementProvider.RECALL_FAR_AWAY_TITLE, "I Can't Move!");
        add(RewindWatchAdvancementProvider.RECALL_FAR_AWAY_DESC, "Use a Rewind Watch to recall yourself from 11:59");
    }
}
