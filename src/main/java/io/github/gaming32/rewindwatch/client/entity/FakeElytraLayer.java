package io.github.gaming32.rewindwatch.client.entity;

import io.github.gaming32.rewindwatch.entity.FakePlayer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FakeElytraLayer extends ElytraLayer<FakePlayer, FakePlayerModel> {
    public FakeElytraLayer(RenderLayerParent<FakePlayer, FakePlayerModel> renderer, EntityModelSet modelSet) {
        super(renderer, modelSet);
    }

    @Override
    public boolean shouldRender(@NotNull ItemStack stack, FakePlayer entity) {
        return entity.getHasElytra();
    }

    @Override
    public @NotNull ResourceLocation getElytraTexture(@NotNull ItemStack stack, @NotNull FakePlayer entity) {
        final var skin = FakePlayerRenderer.getSkin(entity);
        if (skin.elytraTexture() != null) {
            return skin.elytraTexture();
        }
        if (skin.capeTexture() != null && entity.isModelPartShown(PlayerModelPart.CAPE)) {
            return skin.capeTexture();
        }
        return super.getElytraTexture(stack, entity);
    }
}
