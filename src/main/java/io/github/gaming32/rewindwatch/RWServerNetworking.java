package io.github.gaming32.rewindwatch;

import io.github.gaming32.rewindwatch.network.serverbound.ServerboundAnimationStatePayload;
import io.github.gaming32.rewindwatch.registry.RewindWatchAttachmentTypes;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class RWServerNetworking {
    public static void handleAnimationState(ServerboundAnimationStatePayload payload, IPayloadContext context) {
        context.player().setData(RewindWatchAttachmentTypes.PLAYER_ANIMATION_STATE, payload.state());
    }
}
