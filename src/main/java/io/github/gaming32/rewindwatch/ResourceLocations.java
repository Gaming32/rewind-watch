package io.github.gaming32.rewindwatch;

import net.minecraft.resources.ResourceLocation;

public class ResourceLocations {
    private static final ResourceLocation REWIND_WATCH_TEMPLATE = ResourceLocation.fromNamespaceAndPath(RewindWatch.MOD_ID, "");

    public static ResourceLocation minecraft(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }

    public static ResourceLocation rewindWatch(String path) {
        return REWIND_WATCH_TEMPLATE.withPath(path);
    }
}
