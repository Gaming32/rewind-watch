package io.github.gaming32.annreg.value;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public final class RegValues {
    private RegValues() {
    }

    public static <D> RegValue<DataComponentType<?>, DataComponentType<D>> ofDataComponent(
        UnaryOperator<DataComponentType.Builder<D>> builder
    ) {
        return new RegValueImpl<>(() -> builder.apply(DataComponentType.builder()).build(), builder);
    }

    public static RegValue<SoundEvent, SoundEvent> ofVariableRangeSoundEvent() {
        return new RegValueImpl<>(key -> SoundEvent.createVariableRangeEvent(key.location()), Registries.SOUND_EVENT);
    }

    public static RegValue<SoundEvent, SoundEvent> ofFixedRangeSoundEvent(float range) {
        return new RegValueImpl<>(key -> SoundEvent.createFixedRangeEvent(key.location(), range), Registries.SOUND_EVENT);
    }

    public static <E extends Entity> RegValue<EntityType<?>, EntityType<E>> ofEntityType(
        Supplier<EntityType.Builder<E>> builder
    ) {
        return new RegValueImpl<>(key -> builder.get().build(key.location().toString()), Registries.ENTITY_TYPE);
    }
}
