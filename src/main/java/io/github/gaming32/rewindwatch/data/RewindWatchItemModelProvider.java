package io.github.gaming32.rewindwatch.data;

import io.github.gaming32.rewindwatch.RewindWatch;
import io.github.gaming32.rewindwatch.item.RewindWatchItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class RewindWatchItemModelProvider extends ItemModelProvider {
    public RewindWatchItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, RewindWatch.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(RewindWatchItems.REWIND_WATCH.get())
            .transforms()
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
                    .rotation(0f, -90f, 25f)
                    .translation(1.13f, 5f, 1.13f)
                    .scale(0.68f)
                .end()
            .end();
    }
}
