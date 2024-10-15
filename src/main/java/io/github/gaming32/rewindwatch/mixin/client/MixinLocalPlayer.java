package io.github.gaming32.rewindwatch.mixin.client;

import com.mojang.authlib.GameProfile;
import io.github.gaming32.rewindwatch.registry.RewindWatchAttachmentTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class MixinLocalPlayer extends AbstractClientPlayer {
    public MixinLocalPlayer(ClientLevel clientLevel, GameProfile gameProfile) {
        super(clientLevel, gameProfile);
    }

    @Inject(method = "aiStep", at = @At("HEAD"), cancellable = true)
    private void cancelMovement(CallbackInfo ci) {
        if (getData(RewindWatchAttachmentTypes.MOVEMENT_LOCKED)) {
            ci.cancel();
        }
    }
}
