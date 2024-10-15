package io.github.gaming32.rewindwatch.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import io.github.gaming32.rewindwatch.entity.FakePlayer;
import io.github.gaming32.rewindwatch.registry.RewindWatchAttachmentTypes;
import io.github.gaming32.rewindwatch.state.LockedPlayerState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public class MixinHumanoidModel {
    @Inject(method = "prepareMobModel(Lnet/minecraft/world/entity/LivingEntity;FFF)V", at = @At("HEAD"))
    private void forceAnimationMobModel(
        LivingEntity entity, float limbSwingIgnored, float limbSwingAmountIgnored, float partialTick,
        CallbackInfo ci,
        @Local(argsOnly = true, ordinal = 0) LocalFloatRef limbSwing,
        @Local(argsOnly = true, ordinal = 0) LocalFloatRef limbSwingAmount
    ) {
        rw$updateSwing(entity, limbSwing, limbSwingAmount);
    }

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("HEAD"))
    private void forceAnimationSetupAnim(
        LivingEntity entity, float limbSwingIgnored, float limbSwingAmountIgnored, float ageInTicks, float netHeadYaw, float headPitch,
        CallbackInfo ci,
        @Local(argsOnly = true, ordinal = 0) LocalFloatRef limbSwing,
        @Local(argsOnly = true, ordinal = 0) LocalFloatRef limbSwingAmount
    ) {
        rw$updateSwing(entity, limbSwing, limbSwingAmount);
    }

    @Unique
    private static LockedPlayerState rw$updateSwing(LivingEntity living, LocalFloatRef limbSwing, LocalFloatRef limbSwingAmount) {
        if (living instanceof FakePlayer) {
            return null;
        }
        final var state = living.getExistingData(RewindWatchAttachmentTypes.LOCKED_PLAYER_STATE).orElse(null);
        if (state != null) {
            limbSwing.set(state.animationState().position());
            limbSwingAmount.set(state.animationState().speed());
        }
        return state;
    }
}
