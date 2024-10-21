package io.github.gaming32.annreg;

import net.minecraft.core.component.DataComponentType;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public sealed interface ComponentTypeValue<D> extends RegValue<DataComponentType<?>, DataComponentType<D>>
    permits RegValueImpl.ComponentTypeValueImpl {
    static <D> ComponentTypeValue<D> ofCustom(Supplier<DataComponentType<D>> supplier) {
        return new RegValueImpl.ComponentTypeValueImpl<>(supplier);
    }

    static <D> ComponentTypeValue<D> of(UnaryOperator<DataComponentType.Builder<D>> builder) {
        RegValueImpl.validateRegisterFor(builder);
        return ofCustom(() -> builder.apply(DataComponentType.builder()).build());
    }
}
