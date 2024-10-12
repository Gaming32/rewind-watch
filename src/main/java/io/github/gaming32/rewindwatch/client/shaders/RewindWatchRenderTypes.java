package io.github.gaming32.rewindwatch.client.shaders;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class RewindWatchRenderTypes {
    public static final Function<ResourceLocation, RenderType> ENTITY_TRANSLUCENT_GRAYSCALE = entityTranslucent(
        "entity_translucent_grayscale", RewindWatchRenderStateShards.RENDERTYPE_ENTITY_TRANSLUCENT_GRAYSCALE
    );
    public static final Function<ResourceLocation, RenderType> ENTITY_TRANSLUCENT_DISSOLVE = entityTranslucent(
        "entity_translucent_dissolve", RewindWatchRenderStateShards.RENDERTYPE_ENTITY_TRANSLUCENT_DISSOLVE
    );
    public static final Function<ResourceLocation, RenderType> ENTITY_TRANSLUCENT_DISSOLVE_GRAYSCALE = entityTranslucent(
        "entity_translucent_dissolve_grayscale", RewindWatchRenderStateShards.RENDERTYPE_ENTITY_TRANSLUCENT_DISSOLVE_GRAYSCALE
    );

    public static RenderType entityTranslucentGrayscale(ResourceLocation texture) {
        return ENTITY_TRANSLUCENT_GRAYSCALE.apply(texture);
    }

    public static RenderType entityTranslucentDissolve(ResourceLocation texture) {
        return ENTITY_TRANSLUCENT_DISSOLVE.apply(texture);
    }

    public static RenderType entityTranslucentDissolveGrayscale(ResourceLocation texture) {
        return ENTITY_TRANSLUCENT_DISSOLVE_GRAYSCALE.apply(texture);
    }

    private static Function<ResourceLocation, RenderType> entityTranslucent(
        String name, RenderStateShard.ShaderStateShard shader
    ) {
        return Util.memoize(texture -> {
            final var state = RenderType.CompositeState.builder()
                .setShaderState(shader)
                .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setCullState(RenderStateShard.NO_CULL)
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .createCompositeState(true);
            return RenderType.create(
                "rewindwatch_" + name,
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                RenderType.TRANSIENT_BUFFER_SIZE,
                true, true, state
            );
        });
    }
}
