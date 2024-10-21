package io.github.gaming32.annreg;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public sealed interface RegValue<R, T extends R> extends Supplier<T>
    permits BlockValue, ComponentTypeValue, ItemValue, RegValueImpl {
    static <R, T extends R> RegValue<R, T> of(Supplier<T> value) {
        return new RegValueImpl<>(value);
    }

    @Override
    default T get() {
        return holder().get();
    }

    ResourceKey<Registry<R>> registryKey();

    @Nullable
    @SuppressWarnings("unchecked")
    default Registry<R> registry() {
        return (Registry<R>)BuiltInRegistries.REGISTRY.get(registryKey().registry());
    }

    DeferredHolder<R, T> holder();

    default ResourceKey<R> key() {
        return holder().getKey();
    }

    default ResourceLocation id() {
        return key().location();
    }
}
