package io.github.gaming32.rewindwatch.mixin.client;

import com.mojang.authlib.GameProfile;
import io.github.gaming32.rewindwatch.registry.RewindWatchAttachmentTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class MixinLocalPlayer extends AbstractClientPlayer {
    @Override
    @Shadow public abstract void serverAiStep();

    public MixinLocalPlayer(ClientLevel clientLevel, GameProfile gameProfile) {
        super(clientLevel, gameProfile);
    }

    @Inject(method = "aiStep", at = @At("HEAD"), cancellable = true)
    private void cancelMovement(CallbackInfo ci) {
        if (hasData(RewindWatchAttachmentTypes.LOCKED_PLAYER_STATE)) {
            serverAiStep();
            ci.cancel();
        }
    }
}
