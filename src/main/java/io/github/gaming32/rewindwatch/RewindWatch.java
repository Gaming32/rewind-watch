package io.github.gaming32.rewindwatch;

import com.mojang.logging.LogUtils;
import io.github.gaming32.rewindwatch.item.RewindWatchItems;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.slf4j.Logger;

@Mod(RewindWatch.MOD_ID)
public class RewindWatch {
    public static final String MOD_ID = "rewindwatch";
    private static final Logger LOGGER = LogUtils.getLogger();

    public RewindWatch(IEventBus bus) {
        RewindWatchAttachmentTypes.register(bus);
        RewindWatchItems.register(bus);
        bus.register(this);

        LOGGER.info("Rewinding time...");
    }

    @SubscribeEvent
    public void buildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(RewindWatchItems.REWIND_WATCH);
        }
    }
}
