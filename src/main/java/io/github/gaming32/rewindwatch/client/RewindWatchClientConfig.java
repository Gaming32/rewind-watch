package io.github.gaming32.rewindwatch.client;

import net.neoforged.neoforge.common.ModConfigSpec;

public class RewindWatchClientConfig {
    public static final RewindWatchClientConfig CONFIG;
    public static final ModConfigSpec SPEC;

    static {
        final var pair = new ModConfigSpec.Builder().configure(RewindWatchClientConfig::new);
        CONFIG = pair.getLeft();
        SPEC = pair.getRight();
    }

    public final ModConfigSpec.ConfigValue<PostEffectMode> postEffects;

    private RewindWatchClientConfig(ModConfigSpec.Builder builder) {
        builder
            .translation("options.accessibility.title")
            .push("accessibility");
        postEffects = builder.defineEnum("post_effects", PostEffectMode.ON);
        builder.pop();
    }
}
