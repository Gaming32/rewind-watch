package io.github.gaming32.rewindwatch;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(RewindWatch.MOD_ID)
public class RewindWatch {
    public static final String MOD_ID = "rewindwatch";
    private static final Logger LOGGER = LogUtils.getLogger();

    public RewindWatch(IEventBus bus) {
        RewindWatchAttachmentTypes.register(bus);

        LOGGER.info("Rewinding time...");
    }
}
