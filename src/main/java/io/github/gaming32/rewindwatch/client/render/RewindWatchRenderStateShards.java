package io.github.gaming32.rewindwatch.client.render;

import net.minecraft.client.renderer.RenderStateShard;

public class RewindWatchRenderStateShards {
    public static final RenderStateShard.ShaderStateShard RENDERTYPE_ENTITY_TRANSLUCENT_GRAYSCALE =
        new RenderStateShard.ShaderStateShard(RewindWatchShaders::getRendertypeEntityTranslucentGrayscale);
    public static final RenderStateShard.ShaderStateShard RENDERTYPE_ENTITY_TRANSLUCENT_DISSOLVE =
        new RenderStateShard.ShaderStateShard(RewindWatchShaders::getRendertypeEntityTranslucentDissolve);
    public static final RenderStateShard.ShaderStateShard RENDERTYPE_ENTITY_TRANSLUCENT_DISSOLVE_GRAYSCALE =
        new RenderStateShard.ShaderStateShard(RewindWatchShaders::getRendertypeEntityTranslucentDissolveGrayscale);
}
