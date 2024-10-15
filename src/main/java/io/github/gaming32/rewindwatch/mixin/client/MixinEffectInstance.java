package io.github.gaming32.rewindwatch.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.shaders.EffectProgram;
import com.mojang.blaze3d.shaders.Program;
import io.github.gaming32.rewindwatch.RewindWatch;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.io.IOException;

@Mixin(EffectInstance.class)
public class MixinEffectInstance {
    // EffectInstance.getOrCreate kinda sucks (doesn't support #moj_import), so for this mod's shaders, redirect it to
    // ShaderInstance.getOrCreate
    @WrapOperation(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/EffectInstance;getOrCreate(Lnet/minecraft/server/packs/resources/ResourceProvider;Lcom/mojang/blaze3d/shaders/Program$Type;Ljava/lang/String;)Lcom/mojang/blaze3d/shaders/EffectProgram;"
        )
    )
    private EffectProgram useCoreShaderLoader(
        ResourceProvider resourceProvider, Program.Type type, String name,
        Operation<EffectProgram> original
    ) throws IOException {
        if (name.startsWith(RewindWatch.MOD_ID + ':')) {
            final var program = ShaderInstance.getOrCreate(resourceProvider, type, name);
            return new EffectProgram(type, program.getId(), name);
        }
        return original.call(resourceProvider, type, name);
    }
}
