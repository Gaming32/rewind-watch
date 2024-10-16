package io.github.gaming32.rewindwatch.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.gaming32.rewindwatch.client.RewindWatchClient;
import io.github.gaming32.rewindwatch.client.entity.FakePlayerRenderer;
import io.github.gaming32.rewindwatch.registry.RewindWatchAttachmentTypes;
import io.github.gaming32.rewindwatch.util.RWAttachments;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class MixinLivingEntityRenderer {
    @Shadow protected EntityModel<? extends LivingEntity> model;

    @ModifyReturnValue(method = "getRenderType", at = @At("RETURN"))
    private RenderType useEffectRenderType(
        RenderType original,
        @Local(argsOnly = true) LivingEntity entity,
        @Local ResourceLocation texture
    ) {
        return RewindWatchClient.getEffectRenderType(RWAttachments.getEntityEffect(entity), texture, original);
    }

    @Inject(method = "getBob", at = @At("HEAD"), cancellable = true)
    private void noBobIfLocked(LivingEntity livingBase, float partialTick, CallbackInfoReturnable<Float> cir) {
        if (livingBase.hasData(RewindWatchAttachmentTypes.LOCKED_PLAYER_STATE)) {
            cir.setReturnValue(0f);
        }
    }

    @WrapMethod(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V")
    private void fakePlayerRotation(
        LivingEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight,
        Operation<Void> original
    ) {
        if (!(entity instanceof Player player)) {
            original.call(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
            return;
        }
        final var state = entity.getExistingData(RewindWatchAttachmentTypes.LOCKED_PLAYER_STATE).orElse(null);
        if (state == null) {
            original.call(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
            return;
        }
        final var facing = state.facing();
        final var cloak = state.cloak();

        if (model instanceof PlayerModel<?> playerModel) {
            FakePlayerRenderer.updateModelCustomization(playerModel, state.modelCustomization()::contains);
        }

        final var yRot = entity.getYRot();
        final var xRot = entity.getXRot();
        final var yBodyRot = entity.yBodyRot;
        final var yHeadRot = entity.yHeadRot;
        final var xCloak = player.xCloak;
        final var yCloak = player.yCloak;
        final var zCloak = player.zCloak;
        try {
            entity.setYRot(entity.yRotO = facing.y());
            entity.setXRot(entity.xRotO = facing.x());
            entity.yBodyRot = entity.yBodyRotO = facing.bodyY();
            entity.yHeadRot = entity.yHeadRotO = facing.headY();
            player.xCloak = player.xCloakO = cloak.x;
            player.yCloak = player.yCloakO = cloak.y;
            player.zCloak = player.zCloakO = cloak.z;
            original.call(entity, facing.y(), partialTicks, poseStack, buffer, packedLight);
        } finally {
            entity.setYRot(yRot);
            entity.setXRot(xRot);
            entity.yBodyRot = yBodyRot;
            entity.yHeadRot = yHeadRot;
            player.xCloak = xCloak;
            player.yCloak = yCloak;
            player.zCloak = zCloak;
        }
    }
}
