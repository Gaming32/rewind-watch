package io.github.gaming32.rewindwatch.client.entity;

import io.github.gaming32.rewindwatch.entity.FakePlayer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;

public class FakePlayerModel extends PlayerModel<FakePlayer> {
    public FakePlayerModel(ModelPart root, boolean slim) {
        super(root, slim);
    }

    @Override
    public void prepareMobModel(@NotNull FakePlayer entity, float limbSwing, float limbSwingAmount, float partialTick) {
        final var animation = entity.getAnimationState();
        super.prepareMobModel(entity, animation.position(), animation.speed(), partialTick);
    }

    @Override
    public void setupAnim(@NotNull FakePlayer entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        final var animation = entity.getAnimationState();
        super.setupAnim(entity, animation.position(), animation.speed(), ageInTicks, netHeadYaw, headPitch);
    }
}
