package io.github.gaming32.rewindwatch.util;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.phys.Vec2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RWCodecs {
    public static final Codec<PlayerModelPart> PLAYER_MODEL_PART =
        Codec.stringResolver(PlayerModelPart::getId, getModelPartLookup()::get);

    public static final Codec<Vec2> VEC2 = Codec.FLOAT.listOf().comapFlatMap(
        list -> Util.fixedSize(list, 2).map(l -> new Vec2(l.get(0), l.get(1))),
        vec -> List.of(vec.x, vec.y)
    );

    private static Map<String, PlayerModelPart> getModelPartLookup() {
        return Arrays.stream(PlayerModelPart.values())
            .collect(Collectors.toMap(PlayerModelPart::getId, Function.identity()));
    }

    public static <A> Codec<Set<A>> set(Codec<A> codec) {
        return codec.listOf().xmap(ImmutableSet::copyOf, ArrayList::new);
    }
}
