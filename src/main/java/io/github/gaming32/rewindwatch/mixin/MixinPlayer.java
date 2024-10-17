package io.github.gaming32.rewindwatch.mixin;

import io.github.gaming32.rewindwatch.registry.RewindWatchAttachmentTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class MixinPlayer extends LivingEntity {
    protected MixinPlayer(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "aiStep", at = @At("HEAD"), cancellable = true)
    private void cancelMovement(CallbackInfo ci) {
        if (hasData(RewindWatchAttachmentTypes.LOCKED_PLAYER_STATE)) {
            serverAiStep();
            ci.cancel();
        }
    }
}
