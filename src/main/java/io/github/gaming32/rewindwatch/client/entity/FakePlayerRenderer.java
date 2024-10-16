package io.github.gaming32.rewindwatch.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.gaming32.rewindwatch.client.RewindWatchClient;
import io.github.gaming32.rewindwatch.entity.FakePlayer;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class FakePlayerRenderer extends LivingEntityRenderer<FakePlayer, FakePlayerModel> {
    private final FakePlayerModel wideModel;
    private final FakePlayerModel slimModel;

    public FakePlayerRenderer(EntityRendererProvider.Context context) {
        super(context, new FakePlayerModel(context.bakeLayer(ModelLayers.PLAYER), false), 0f);
        wideModel = model;
        slimModel = new FakePlayerModel(context.bakeLayer(ModelLayers.PLAYER_SLIM), true);

        addLayer(new FakeCapeLayer(this));
    }

    @Override
    public void render(
        @NotNull FakePlayer entity,
        float entityYaw,
        float partialTick,
        @NotNull PoseStack poseStack,
        @NotNull MultiBufferSource bufferSource,
        int packedLight
    ) {
        updateModel(entity);
        RewindWatchClient.updateDissolveOpacity(entity, partialTick);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    protected @Nullable RenderType getRenderType(@NotNull FakePlayer entity, boolean bodyVisible, boolean translucent, boolean glowing) {
        final var texture = getTextureLocation(entity);
        return RewindWatchClient.getEffectRenderType(entity.getCurrentEffect(), texture, model.renderType(texture));
    }

    @Override
    public @NotNull Vec3 getRenderOffset(FakePlayer entity, float partialTicks) {
        return entity.isCrouching()
            ? new Vec3(0.0, (double)(entity.getScale() * -2f) / 16.0, 0.0)
            : super.getRenderOffset(entity, partialTicks);
    }

    private void updateModel(FakePlayer entity) {
        model = switch (getSkin(entity).model()) {
            case WIDE -> wideModel;
            case SLIM -> slimModel;
        };
        updateModelCustomization(model, entity::isModelPartShown);
        model.crouching = entity.isCrouching();
    }

    public static void updateModelCustomization(PlayerModel<?> model, Predicate<PlayerModelPart> checker) {
        model.hat.visible = checker.test(PlayerModelPart.HAT);
        model.jacket.visible = checker.test(PlayerModelPart.JACKET);
        model.leftPants.visible = checker.test(PlayerModelPart.LEFT_PANTS_LEG);
        model.rightPants.visible = checker.test(PlayerModelPart.RIGHT_PANTS_LEG);
        model.leftSleeve.visible = checker.test(PlayerModelPart.LEFT_SLEEVE);
        model.rightSleeve.visible = checker.test(PlayerModelPart.RIGHT_SLEEVE);
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(@NotNull FakePlayer entity) {
        return getSkin(entity).texture();
    }

    public static PlayerSkin getSkin(FakePlayer entity) {
        final var uuid = entity.getPlayerUuid().orElse(null);
        if (uuid == null) {
            return DefaultPlayerSkin.get(Util.NIL_UUID);
        }
        //noinspection DataFlowIssue
        var playerInfo = Minecraft.getInstance().getConnection().getPlayerInfo(uuid);
        if (playerInfo == null) {
            return DefaultPlayerSkin.get(uuid);
        }
        return playerInfo.getSkin();
    }

    @Override
    protected void scale(@NotNull FakePlayer livingEntity, PoseStack poseStack, float partialTickTime) {
        poseStack.scale(15 / 16f, 15 / 16f, 15 / 16f);
    }

    // From PlayerRenderer
    @Override
    protected void setupRotations(FakePlayer entity, @NotNull PoseStack poseStack, float bob, float yBodyRot, float partialTick, float scale) {
        float f = entity.getSwimAmount(partialTick);
        float f1 = entity.getViewXRot(partialTick);
        if (entity.isFallFlying()) {
            super.setupRotations(entity, poseStack, bob, yBodyRot, partialTick, scale);
            float f2 = (float)entity.getFallFlyingTicks() + partialTick;
            float f3 = Mth.clamp(f2 * f2 / 100.0F, 0.0F, 1.0F);
            if (!entity.isAutoSpinAttack()) {
                poseStack.mulPose(Axis.XP.rotationDegrees(f3 * (-90.0F - f1)));
            }

            Vec3 vec3 = entity.getViewVector(partialTick);
            Vec3 vec31 = entity.getPlayerSpeed();
            double d0 = vec31.horizontalDistanceSqr();
            double d1 = vec3.horizontalDistanceSqr();
            if (d0 > 0.0 && d1 > 0.0) {
                double d2 = (vec31.x * vec3.x + vec31.z * vec3.z) / Math.sqrt(d0 * d1);
                double d3 = vec31.x * vec3.z - vec31.z * vec3.x;
                poseStack.mulPose(Axis.YP.rotation((float)(Math.signum(d3) * Math.acos(d2))));
            }
        } else if (f > 0.0F) {
            super.setupRotations(entity, poseStack, bob, yBodyRot, partialTick, scale);
            float f4 = entity.isInWater() || entity.isInFluidType((fluidType, height) -> entity.canSwimInFluidType(fluidType)) ? -90.0F - entity.getXRot() : -90.0F;
            float f5 = Mth.lerp(f, 0.0F, f4);
            poseStack.mulPose(Axis.XP.rotationDegrees(f5));
            if (entity.isVisuallySwimming()) {
                poseStack.translate(0.0F, -1.0F, 0.3F);
            }
        } else {
            super.setupRotations(entity, poseStack, bob, yBodyRot, partialTick, scale);
        }
    }

    @Override
    protected float getBob(@NotNull FakePlayer livingBase, float partialTick) {
        return 0f;
    }

    @Override
    protected void renderNameTag(@NotNull FakePlayer entity, @NotNull Component displayName, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, float partialTick) {
    }
}
