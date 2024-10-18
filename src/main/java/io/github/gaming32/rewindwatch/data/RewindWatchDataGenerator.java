package io.github.gaming32.rewindwatch.data;

import io.github.gaming32.rewindwatch.RewindWatch;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = RewindWatch.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class RewindWatchDataGenerator {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        final var generator = event.getGenerator();
        final var output = generator.getPackOutput();
        final var existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(event.includeClient(), new RewindWatchItemModelProvider(output, existingFileHelper));
        generator.addProvider(event.includeClient(), new RewindWatchLanguageProvider(output));
        generator.addProvider(event.includeClient(), new RewindWatchSoundDefinitionsProvider(output, existingFileHelper));
    }
}
