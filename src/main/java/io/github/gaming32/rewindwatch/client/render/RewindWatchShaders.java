package io.github.gaming32.rewindwatch.client.render;

import io.github.gaming32.rewindwatch.ResourceLocations;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import io.github.gaming32.rewindwatch.RewindWatch;
import net.minecraft.client.renderer.ShaderInstance;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;
import java.util.Objects;

@EventBusSubscriber(modid = RewindWatch.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class RewindWatchShaders {
    private static ShaderInstance rendertypeEntityTranslucentGrayscale;
    private static ShaderInstance rendertypeEntityTranslucentDissolve;
    private static ShaderInstance rendertypeEntityTranslucentDissolveGrayscale;

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(
            new ShaderInstance(
                event.getResourceProvider(),
                ResourceLocations.rewindWatch("rendertype_entity_translucent_grayscale"),
                DefaultVertexFormat.NEW_ENTITY
            ),
            shader -> rendertypeEntityTranslucentGrayscale = shader
        );
        event.registerShader(
            new ShaderInstance(
                event.getResourceProvider(),
                ResourceLocations.rewindWatch("rendertype_entity_translucent_dissolve"),
                DefaultVertexFormat.NEW_ENTITY
            ),
            shader -> rendertypeEntityTranslucentDissolve = shader
        );
        event.registerShader(
            new ShaderInstance(
                event.getResourceProvider(),
                ResourceLocations.rewindWatch("rendertype_entity_translucent_dissolve_grayscale"),
                DefaultVertexFormat.NEW_ENTITY
            ),
            shader -> rendertypeEntityTranslucentDissolveGrayscale = shader
        );
    }

    public static ShaderInstance getRendertypeEntityTranslucentGrayscale() {
        return Objects.requireNonNull(rendertypeEntityTranslucentGrayscale);
    }

    public static ShaderInstance getRendertypeEntityTranslucentDissolve() {
        return Objects.requireNonNull(rendertypeEntityTranslucentDissolve);
    }

    public static ShaderInstance getRendertypeEntityTranslucentDissolveGrayscale() {
        return Objects.requireNonNull(rendertypeEntityTranslucentDissolveGrayscale);
    }
}
