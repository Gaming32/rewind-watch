package io.github.gaming32.annreg.value;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public sealed interface ItemComponentTypeValue<D> extends RegValue<DataComponentType<?>, DataComponentType<D>>
    permits RegValueImpl.ItemComponentTypeValueImpl {
    private static <D> ItemComponentTypeValue<D> ofImpl(Supplier<DataComponentType<D>> supplier) {
        return new RegValueImpl.ItemComponentTypeValueImpl<>(supplier);
    }

    static <D> ItemComponentTypeValue<D> ofCustom(Supplier<DataComponentType<D>> supplier) {
        RegValueImpl.validateRegisterFor(supplier, Registries.DATA_COMPONENT_TYPE);
        return ofImpl(supplier);
    }

    static <D> ItemComponentTypeValue<D> of(UnaryOperator<DataComponentType.Builder<D>> builder) {
        RegValueImpl.validateRegisterFor(builder, Registries.DATA_COMPONENT_TYPE);
        return ofImpl(() -> builder.apply(DataComponentType.builder()).build());
    }
}
