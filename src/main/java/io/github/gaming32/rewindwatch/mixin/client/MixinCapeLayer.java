package io.github.gaming32.rewindwatch.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.gaming32.rewindwatch.client.RewindWatchClient;
import io.github.gaming32.rewindwatch.util.RWAttachments;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(CapeLayer.class)
public class MixinCapeLayer {
    @ModifyArg(
        method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/player/AbstractClientPlayer;FFFFFF)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
        )
    )
    private RenderType useEffectRenderType(
        RenderType renderType,
        @Local(argsOnly = true) AbstractClientPlayer livingEntity
    ) {
        return RewindWatchClient.getEffectRenderType(
            RWAttachments.getEntityEffect(livingEntity),
            livingEntity.getSkin().capeTexture(),
            renderType
        );
    }
}
