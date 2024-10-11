package io.github.gaming32.rewindwatch.client.commands;

import io.github.gaming32.rewindwatch.RewindWatch;
import net.minecraft.SharedConstants;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

@EventBusSubscriber(modid = RewindWatch.MOD_ID, value = Dist.CLIENT)
public class RewindWatchClientCommands {
    @SubscribeEvent
    public static void registerClientCommands(RegisterClientCommandsEvent event) {
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            TestEffectCommand.register(event.getDispatcher());
        }
    }
}
