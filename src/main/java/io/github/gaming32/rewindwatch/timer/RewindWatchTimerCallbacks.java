package io.github.gaming32.rewindwatch.timer;

import io.github.gaming32.rewindwatch.RewindWatch;
import net.minecraft.world.level.timers.TimerCallbacks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;

@EventBusSubscriber(modid = RewindWatch.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class RewindWatchTimerCallbacks {
    @SubscribeEvent
    public static void queueRegister(FMLConstructModEvent event) {
        event.enqueueWork(RewindWatchTimerCallbacks::register);
    }

    private static void register() {
        TimerCallbacks.SERVER_CALLBACKS
            .register(new RecallSoundCallback.Serializer())
            .register(new RecallCompleteCallback.Serializer());
    }
}
