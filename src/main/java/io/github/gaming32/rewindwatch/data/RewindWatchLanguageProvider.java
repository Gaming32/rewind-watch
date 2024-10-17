package io.github.gaming32.rewindwatch.data;

import io.github.gaming32.rewindwatch.RewindWatch;
import io.github.gaming32.rewindwatch.item.RewindWatchItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class RewindWatchLanguageProvider extends LanguageProvider {
    public RewindWatchLanguageProvider(PackOutput output) {
        super(output, RewindWatch.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        addItem(RewindWatchItems.REWIND_WATCH, "Rewind Watch");
        add(subKey(RewindWatchItems.REWIND_WATCH, "used"), "Rewind Watch Used");
        add(subKey(RewindWatchItems.REWIND_WATCH, "recalled"), "Player Recalled");
        add("rewindwatch.dimension_gone", "The dimension you are recalling to no longer exists");
    }

    private static String subKey(ItemLike item, String subKey) {
        return item.asItem().getDescriptionId() + '.' + subKey;
    }
}
