package io.github.gaming32.rewindwatch;

import com.mojang.logging.LogUtils;
import io.github.gaming32.rewindwatch.client.RWClientNetworking;
import io.github.gaming32.rewindwatch.entity.RewindWatchEntityTypes;
import io.github.gaming32.rewindwatch.item.RewindWatchItems;
import io.github.gaming32.rewindwatch.network.ClientboundEntityEffectPayload;
import io.github.gaming32.rewindwatch.network.ClientboundLockMovementPayload;
import io.github.gaming32.rewindwatch.network.ServerboundAnimationStatePayload;
import io.github.gaming32.rewindwatch.registry.RewindWatchAttachmentTypes;
import io.github.gaming32.rewindwatch.registry.RewindWatchEntityDataSerializers;
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
        RewindWatchAttachmentTypes.register(bus);
        RewindWatchEntityDataSerializers.register(bus);
        RewindWatchItems.register(bus);
        RewindWatchEntityTypes.register(bus);

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
        final var registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playToClient(
            ClientboundEntityEffectPayload.TYPE,
            ClientboundEntityEffectPayload.STREAM_CODEC,
            RWClientNetworking::handleEntityEffect
        );
        registrar.playToClient(
            ClientboundLockMovementPayload.TYPE,
            ClientboundLockMovementPayload.STREAM_CODEC,
            (payload, context) -> context.player().setData(RewindWatchAttachmentTypes.MOVEMENT_LOCKED, payload.lock())
        );
        registrar.playToServer(
            ServerboundAnimationStatePayload.TYPE,
            ServerboundAnimationStatePayload.STREAM_CODEC,
            (payload, context) -> context.player().setData(RewindWatchAttachmentTypes.PLAYER_ANIMATION_STATE, payload.state())
        );
    }
}
