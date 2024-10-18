package io.github.gaming32.rewindwatch.data;

import io.github.gaming32.rewindwatch.item.RewindWatchItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class RewindWatchRecipeProvider extends RecipeProvider {
    public RewindWatchRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, RewindWatchItems.REWIND_WATCH)
            .define('G', Items.GOLD_INGOT)
            .define('I', Items.IRON_INGOT)
            .define('/', Items.AMETHYST_SHARD)
            .define('*', Items.NETHER_STAR)
            .pattern("GIG")
            .pattern("/*/")
            .pattern("G/G")
            .unlockedBy("has_nether_star", has(Items.NETHER_STAR))
            .save(output);
    }
}
