package io.github.gaming32.rewindwatch.mixin;

import io.github.gaming32.rewindwatch.registry.RewindWatchAttachmentTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class MixinServerGamePacketListenerImpl {
    @Shadow public ServerPlayer player;

    @Shadow private boolean clientIsFloating;

    @Inject(method = "tick", at = @At("HEAD"))
    private void notFlyingWhenLocked(CallbackInfo ci) {
        if (player.hasData(RewindWatchAttachmentTypes.LOCKED_PLAYER_STATE)) {
            clientIsFloating = false;
        }
    }
}
