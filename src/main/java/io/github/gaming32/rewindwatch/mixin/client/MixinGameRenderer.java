package io.github.gaming32.rewindwatch.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import io.github.gaming32.rewindwatch.client.RewindWatchClient;
import io.github.gaming32.rewindwatch.client.shaders.RewindWatchRenderState;
import io.github.gaming32.rewindwatch.util.RWAttachments;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.client.EntitySpectatorShaderManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {
    @Shadow public abstract void loadEffect(ResourceLocation resourceLocation);

    @Shadow @Final Minecraft minecraft;

    @Shadow @Nullable PostChain postEffect;

    @WrapWithCondition(
        method = "checkEntityPostEffect",
        at = @At(
            value = "INVOKE",
            target = "Lnet/neoforged/neoforge/client/ClientHooks;loadEntityShader(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/client/renderer/GameRenderer;)V"
        )
    )
    private boolean useCustomShaderIfNotOverridden(Entity entity, GameRenderer renderer) {
        if (entity == null || EntitySpectatorShaderManager.get(entity.getType()) != null) {
            return true;
        }
        final var shader = RewindWatchClient.getEffectPostShader(RWAttachments.getEntityEffect(entity));
        if (shader != null) {
            loadEffect(shader);
            return false;
        }
        return true;
    }

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/PostChain;process(F)V"
        )
    )
    private void injectDissolveOpacity(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo ci) {
        assert postEffect != null;
        final var entity = minecraft.cameraEntity;
        if (entity == null) return;
        RewindWatchClient.updateDissolveOpacity(entity, deltaTracker.getGameTimeDeltaTicks());
        postEffect.setUniform("DissolveOpacity", RewindWatchRenderState.getDissolveOpacity());
    }
}
