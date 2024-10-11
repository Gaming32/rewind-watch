package io.github.gaming32.rewindwatch.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.gaming32.rewindwatch.EntityEffect;
import io.github.gaming32.rewindwatch.RewindWatchAttachmentTypes;
import io.github.gaming32.rewindwatch.client.render.RewindWatchRenderTypes;
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
        @SuppressWarnings("LocalMayBeArgsOnly") @Local ResourceLocation texture
    ) {
        return switch (entity.getData(RewindWatchAttachmentTypes.ENTITY_EFFECT)) {
            case EntityEffect.Simple.NONE -> original;
            case EntityEffect.Simple.GRAYSCALE -> RewindWatchRenderTypes.entityTranslucentGrayscale(texture);
            case EntityEffect.Dissolve dissolve -> switch (dissolve.type()) {
                case TRANSPARENT -> RewindWatchRenderTypes.entityTranslucentDissolve(texture);
                case GRAYSCALE, TRANSPARENT_GRAYSCALE -> RewindWatchRenderTypes.entityTranslucentDissolveGrayscale(texture);
            };
        };
    }
}
