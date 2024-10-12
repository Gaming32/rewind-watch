package io.github.gaming32.rewindwatch.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.gaming32.rewindwatch.client.RewindWatchClient;
import io.github.gaming32.rewindwatch.registry.RewindWatchAttachmentTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntityRenderer.class)
public class MixinLivingEntityRenderer {
    @ModifyReturnValue(method = "getRenderType", at = @At("RETURN"))
    private RenderType useEffectRenderType(
        RenderType original,
        @Local(argsOnly = true) LivingEntity entity,
        @Local ResourceLocation texture
    ) {
        return RewindWatchClient.getEffectRenderType(entity.getData(RewindWatchAttachmentTypes.ENTITY_EFFECT), texture, original);
    }
}
