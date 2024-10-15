package io.github.gaming32.rewindwatch.commands;

import io.github.gaming32.rewindwatch.RewindWatch;
import net.minecraft.SharedConstants;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = RewindWatch.MOD_ID)
public class RewindWatchCommands {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            TestEffectCommand.register(event.getDispatcher());
            TestFakePlayerCommand.register(event.getDispatcher());
            TestMovementLockCommand.register(event.getDispatcher());
        }
    }
}
