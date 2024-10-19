package io.github.gaming32.rewindwatch.data;

import io.github.gaming32.rewindwatch.ResourceLocations;
import io.github.gaming32.rewindwatch.RewindWatch;
import io.github.gaming32.rewindwatch.item.RewindWatchItem;
import io.github.gaming32.rewindwatch.item.RewindWatchItems;
import io.github.gaming32.rewindwatch.trigger.WatchRecallTrigger;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class RewindWatchAdvancementProvider extends AdvancementProvider {
    private static final String TRANSLATION_PREFIX = "advancements." + RewindWatch.MOD_ID + ".";

    private static final String RECALL = "recall";
    public static final String RECALL_TITLE = TRANSLATION_PREFIX + RECALL + ".title";
    public static final String RECALL_DESC = TRANSLATION_PREFIX + RECALL + ".description";

    private static final String RECALL_FAR_AWAY = "recall_far_away";
    public static final String RECALL_FAR_AWAY_TITLE = TRANSLATION_PREFIX + RECALL_FAR_AWAY + ".title";
    public static final String RECALL_FAR_AWAY_DESC = TRANSLATION_PREFIX + RECALL_FAR_AWAY + ".description";

    public RewindWatchAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper) {
        super(output, registries, existingFileHelper, List.of(new RWAdvancementGenerator()));
    }

    private static class RWAdvancementGenerator implements AdvancementGenerator {
        @Override
        public void generate(
            HolderLookup.@NotNull Provider registries,
            @NotNull Consumer<AdvancementHolder> saver,
            @NotNull ExistingFileHelper existingFileHelper
        ) {
            final var recall = new Advancement.Builder()
                .parent(AdvancementSubProvider.createPlaceholder("minecraft:nether/summon_wither"))
                .display(
                    RewindWatchItems.REWIND_WATCH,
                    Component.translatable(RECALL_TITLE),
                    Component.translatable(RECALL_DESC),
                    null, // Background
                    AdvancementType.TASK,
                    true, // Toast
                    true, // Chat
                    false // Hidden
                )
                .addCriterion("recalled", WatchRecallTrigger.builder().build())
                .save(saver, ResourceLocations.rewindWatch(RECALL), existingFileHelper);
            new Advancement.Builder()
                .parent(recall)
                .display(
                    RewindWatchItems.REWIND_WATCH,
                    Component.translatable(RECALL_FAR_AWAY_TITLE),
                    Component.translatable(RECALL_FAR_AWAY_DESC),
                    null, // Background
                    AdvancementType.CHALLENGE,
                    true, // Toast
                    true, // Chat
                    false // Hidden
                )
                .rewards(AdvancementRewards.Builder.experience(120))
                .addCriterion("recalled", WatchRecallTrigger.builder()
                    .recallDuration(MinMaxBounds.Ints.atLeast(RewindWatchItem.MAX_TELEPORT_TIME))
                    .build()
                )
                .save(saver, ResourceLocations.rewindWatch(RECALL_FAR_AWAY), existingFileHelper);
        }
    }
}
