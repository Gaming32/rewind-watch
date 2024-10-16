package io.github.gaming32.rewindwatch.util;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import net.minecraft.world.entity.player.PlayerModelPart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RWCodecs {
    public static final Codec<PlayerModelPart> PLAYER_MODEL_PART =
        Codec.stringResolver(PlayerModelPart::getId, getModelPartLookup()::get);

    private static Map<String, PlayerModelPart> getModelPartLookup() {
        return Arrays.stream(PlayerModelPart.values())
            .collect(Collectors.toMap(PlayerModelPart::getId, Function.identity()));
    }

    public static <A> Codec<Set<A>> set(Codec<A> codec) {
        return codec.listOf().xmap(ImmutableSet::copyOf, ArrayList::new);
    }
}
