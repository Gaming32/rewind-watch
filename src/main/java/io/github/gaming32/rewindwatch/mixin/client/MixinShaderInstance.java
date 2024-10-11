package io.github.gaming32.rewindwatch.mixin.client;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.github.gaming32.rewindwatch.client.render.RewindWatchRenderState;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShaderInstance.class)
public abstract class MixinShaderInstance {
    @Shadow @javax.annotation.Nullable public abstract Uniform getUniform(String name);

    @Unique
    private @Nullable Uniform rw$dissolveOpacity;

    @Inject(
        method = "<init>(Lnet/minecraft/server/packs/resources/ResourceProvider;Lnet/minecraft/resources/ResourceLocation;Lcom/mojang/blaze3d/vertex/VertexFormat;)V",
        at = @At("TAIL")
    )
    private void initOpacityUniform(ResourceProvider p_173336_, ResourceLocation shaderLocation, VertexFormat p_173338_, CallbackInfo ci) {
        rw$dissolveOpacity = getUniform("DissolveOpacity");
    }

    @Inject(
        method = "setDefaultUniforms",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/systems/RenderSystem;setupShaderLights(Lnet/minecraft/client/renderer/ShaderInstance;)V"
        )
    )
    private void setOpacityUniform(VertexFormat.Mode mode, Matrix4f projectionMatrix, Matrix4f frustrumMatrix, Window window, CallbackInfo ci) {
        if (rw$dissolveOpacity != null) {
            rw$dissolveOpacity.set(RewindWatchRenderState.getDissolveOpacity());
        }
    }
}
