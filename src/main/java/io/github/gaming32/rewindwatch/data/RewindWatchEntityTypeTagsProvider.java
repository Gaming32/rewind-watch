package io.github.gaming32.rewindwatch.data;

import io.github.gaming32.rewindwatch.RewindWatch;
import io.github.gaming32.rewindwatch.entity.RewindWatchEntityTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class RewindWatchEntityTypeTagsProvider extends EntityTypeTagsProvider {
    public RewindWatchEntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, registries, RewindWatch.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider registries) {
        tag(Tags.EntityTypes.CAPTURING_NOT_SUPPORTED).add(RewindWatchEntityTypes.FAKE_PLAYER.get());
        tag(Tags.EntityTypes.TELEPORTING_NOT_SUPPORTED).add(RewindWatchEntityTypes.FAKE_PLAYER.get());
    }
}
