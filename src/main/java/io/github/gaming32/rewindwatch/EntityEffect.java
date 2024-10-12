package io.github.gaming32.rewindwatch;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public sealed interface EntityEffect {
    BiMap<ResourceLocation, EntityEffectType> TYPES = ImmutableBiMap.of(
        ResourceLocations.rewindWatch("simple"), Simple.TYPE,
        ResourceLocations.rewindWatch("dissolve"), Dissolve.TYPE
    );
    Codec<EntityEffect> CODEC =
        ResourceLocation.CODEC
            .xmap(TYPES::get, TYPES.inverse()::get)
            .dispatch(EntityEffect::effectType, EntityEffectType::codec);
    StreamCodec<FriendlyByteBuf, EntityEffect> STREAM_CODEC =
        StreamCodec.of(FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::readResourceLocation)
            .map(TYPES::get, TYPES.inverse()::get)
            .dispatch(EntityEffect::effectType, EntityEffectType::streamCodec);

    EntityEffectType effectType();

    enum Simple implements EntityEffect, StringRepresentable {
        NONE, GRAYSCALE;

        public static final MapCodec<Simple> CODEC = StringRepresentable.fromEnum(Simple::values).fieldOf("effect");
        public static final StreamCodec<FriendlyByteBuf, Simple> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(Simple.class);
        public static final EntityEffectType TYPE = new EntityEffectType(CODEC, STREAM_CODEC);

        private final String serializedName;

        Simple() {
            serializedName = name().toLowerCase(Locale.ROOT);
        }

        @Override
        public EntityEffectType effectType() {
            return TYPE;
        }

        @NotNull
        @Override
        public String getSerializedName() {
            return serializedName;
        }
    }

    record Dissolve(long startTick, long endTick, Type type, boolean in) implements EntityEffect {
        public enum Type implements StringRepresentable {
            TRANSPARENT(true),
            GRAYSCALE(false),
            TRANSPARENT_GRAYSCALE(true);

            public static final Codec<Type> CODEC = StringRepresentable.fromEnum(Type::values);
            public static final StreamCodec<FriendlyByteBuf, Type> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(Type.class);

            private final String serializedName;
            private final boolean transparent;

            Type(boolean transparent) {
                serializedName = name().toLowerCase(Locale.ROOT);
                this.transparent = transparent;
            }

            @NotNull
            @Override
            public String getSerializedName() {
                return serializedName;
            }

            public boolean isTransparent() {
                return transparent;
            }
        }

        public static final MapCodec<Dissolve> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                Codec.LONG.fieldOf("start_tick").forGetter(Dissolve::startTick),
                Codec.LONG.fieldOf("end_tick").forGetter(Dissolve::endTick),
                Type.CODEC.fieldOf("dissolve_type").forGetter(Dissolve::type),
                Codec.BOOL.fieldOf("in").forGetter(Dissolve::in)
            ).apply(instance, Dissolve::new)
        );
        public static final StreamCodec<FriendlyByteBuf, Dissolve> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG, Dissolve::startTick,
            ByteBufCodecs.VAR_LONG, Dissolve::endTick,
            Type.STREAM_CODEC, Dissolve::type,
            ByteBufCodecs.BOOL, Dissolve::in,
            Dissolve::new
        );
        public static final EntityEffectType TYPE = new EntityEffectType(CODEC, STREAM_CODEC);

        @Override
        public EntityEffectType effectType() {
            return TYPE;
        }
    }

    record EntityEffectType(
        MapCodec<? extends EntityEffect> codec,
        StreamCodec<? super FriendlyByteBuf, ? extends EntityEffect> streamCodec
    ) {
    }
}
