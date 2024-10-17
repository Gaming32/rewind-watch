package io.github.gaming32.rewindwatch.data;

import io.github.gaming32.rewindwatch.RewindWatch;
import io.github.gaming32.rewindwatch.item.RewindWatchItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class RewindWatchItemModelProvider extends ItemModelProvider {
    public RewindWatchItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, RewindWatch.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(RewindWatchItems.REWIND_WATCH.get());
    }
}
