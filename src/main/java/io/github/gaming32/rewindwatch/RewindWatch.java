package io.github.gaming32.rewindwatch;

import com.mojang.logging.LogUtils;
import io.github.gaming32.rewindwatch.client.RWClientNetworking;
import io.github.gaming32.rewindwatch.entity.RewindWatchEntityTypes;
import io.github.gaming32.rewindwatch.item.RewindWatchItems;
import io.github.gaming32.rewindwatch.network.clientbound.ClientboundClearLockedStatePayload;
import io.github.gaming32.rewindwatch.network.clientbound.ClientboundEntityEffectPayload;
import io.github.gaming32.rewindwatch.network.clientbound.ClientboundLockedStatePayload;
import io.github.gaming32.rewindwatch.network.serverbound.ServerboundClientStatePayload;
import io.github.gaming32.rewindwatch.registry.RewindWatchEntityDataSerializers;
import io.github.gaming32.rewindwatch.registry.RewindWatchSoundEvents;
import io.github.gaming32.rewindwatch.trigger.RewindWatchCriteriaTriggers;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.slf4j.Logger;

@Mod(RewindWatch.MOD_ID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class RewindWatch {
    public static final String MOD_ID = "rewindwatch";
    public static final String PROTOCOL_VERSION = "1";
    private static final Logger LOGGER = LogUtils.getLogger();

    public RewindWatch(IEventBus bus) {
        RewindWatchCriteriaTriggers.register(bus);
        RewindWatchEntityDataSerializers.register(bus);
        RewindWatchEntityTypes.register(bus);
        RewindWatchSoundEvents.register(bus);

        LOGGER.info("Rewinding time...");
    }

    @SubscribeEvent
    public static void buildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(RewindWatchItems.REWIND_WATCH);
        }
    }

    @SubscribeEvent
    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        event.registrar(PROTOCOL_VERSION)
            .playToClient(
                ClientboundEntityEffectPayload.TYPE,
                ClientboundEntityEffectPayload.STREAM_CODEC,
                RWClientNetworking::handleEntityEffect
            )
            .playToClient(
                ClientboundLockedStatePayload.TYPE,
                ClientboundLockedStatePayload.STREAM_CODEC,
                RWClientNetworking::handleLockedState
            )
            .playToClient(
                ClientboundClearLockedStatePayload.TYPE,
                ClientboundClearLockedStatePayload.STREAM_CODEC,
                RWClientNetworking::handleClearLockedState
            )
            .playToServer(
                ServerboundClientStatePayload.TYPE,
                ServerboundClientStatePayload.STREAM_CODEC,
                RWServerNetworking::handleAnimationState
            );
    }
}
