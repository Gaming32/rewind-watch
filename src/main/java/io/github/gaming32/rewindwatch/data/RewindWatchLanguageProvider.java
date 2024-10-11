package io.github.gaming32.rewindwatch.data;

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
    }
}
