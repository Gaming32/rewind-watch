package io.github.gaming32.rewindwatch.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.gaming32.rewindwatch.client.RewindWatchClient;
import io.github.gaming32.rewindwatch.entity.FakePlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class FakeCapeLayer extends RenderLayer<FakePlayer, FakePlayerModel> {
    public FakeCapeLayer(RenderLayerParent<FakePlayer, FakePlayerModel> renderer) {
        super(renderer);
    }

    @Override
    public void render(
        @NotNull PoseStack poseStack,
        @NotNull MultiBufferSource bufferSource,
        int packedLight,
        @NotNull FakePlayer fakePlayer,
        float limbSwing,
        float limbSwingAmount,
        float partialTick,
        float ageInTicks,
        float netHeadYaw,
        float headPitch
    ) {
        if (!fakePlayer.isInvisible() && fakePlayer.isModelPartShown(PlayerModelPart.CAPE)) {
            PlayerSkin playerskin = FakePlayerRenderer.getSkin(fakePlayer);
            if (playerskin.capeTexture() != null) {
                ItemStack itemstack = fakePlayer.getItemBySlot(EquipmentSlot.CHEST);
                if (!itemstack.is(Items.ELYTRA)) {
                    poseStack.pushPose();
                    poseStack.translate(0.0F, 0.0F, 0.125F);
                    final var cloak = fakePlayer.getCloak();
                    double d0 = cloak.x - fakePlayer.getX();
                    double d1 = cloak.y - fakePlayer.getY();
                    double d2 = cloak.z - fakePlayer.getZ();
                    float f = Mth.rotLerp(partialTick, fakePlayer.yBodyRotO, fakePlayer.yBodyRot);
                    double d3 = Mth.sin(f * (float) (Math.PI / 180.0));
                    double d4 = -Mth.cos(f * (float) (Math.PI / 180.0));
                    float f1 = (float)d1 * 10.0F;
                    f1 = Mth.clamp(f1, -6.0F, 32.0F);
                    float f2 = (float)(d0 * d3 + d2 * d4) * 100.0F;
                    f2 = Mth.clamp(f2, 0.0F, 150.0F);
                    float f3 = (float)(d0 * d4 - d2 * d3) * 100.0F;
                    f3 = Mth.clamp(f3, -20.0F, 20.0F);
                    if (f2 < 0.0F) {
                        f2 = 0.0F;
                    }

                    float f4 = 0f;
                    f1 += Mth.sin(Mth.lerp(partialTick, fakePlayer.walkDistO, fakePlayer.walkDist) * 6.0F) * 32.0F * f4;
                    if (fakePlayer.isCrouching()) {
                        f1 += 25.0F;
                    }

                    poseStack.mulPose(Axis.XP.rotationDegrees(6.0F + f2 / 2.0F + f1));
                    poseStack.mulPose(Axis.ZP.rotationDegrees(f3 / 2.0F));
                    poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - f3 / 2.0F));
                    VertexConsumer vertexconsumer = bufferSource.getBuffer(RewindWatchClient.getEffectRenderType(
                        fakePlayer.getCurrentEffect(), playerskin.capeTexture(), RenderType.entitySolid(playerskin.capeTexture())
                    ));
                    this.getParentModel().renderCloak(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
                    poseStack.popPose();
                }
            }
        }
    }
}
