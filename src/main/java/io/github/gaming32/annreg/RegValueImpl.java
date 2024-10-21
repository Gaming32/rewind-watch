package io.github.gaming32.annreg;

import net.jodah.typetools.TypeResolver;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Supplier;

sealed class RegValueImpl<R, T extends R> implements RegValue<R, T> {
    Supplier<T> initialValue;
    private final ResourceKey<Registry<R>> registry;
    DeferredHolder<R, T> holder = null;

    RegValueImpl(Supplier<T> initialValue, ResourceKey<Registry<R>> registry) {
        this.initialValue = initialValue;
        this.registry = registry;
    }

    @SuppressWarnings("unchecked")
    RegValueImpl(Supplier<T> initialValue) {
        this(initialValue, (ResourceKey<Registry<R>>)findRegistry(initialValue));
    }

    private static ResourceKey<?> findRegistry(Supplier<?> supplier) {
        final var ownerClass = getLambdaClass(supplier);
        final var annotation = getRegisterFor(ownerClass);
        final var registryClass = annotation.registry();
        validateSupplier(supplier, registryClass);
        final var registry = AnnotationRegistration.REG_TYPES.get(registryClass);
        if (registry == null) {
            throw new IllegalArgumentException("No registry for " + registryClass + " is known.");
        }
        return registry;
    }

    static void validateRegisterFor(Object lambda) {
        getRegisterFor(getLambdaClass(lambda));
    }

    private static Class<?> getLambdaClass(Object lambda) {
        final var className = lambda.getClass().getName();
        final var ownerClassName = StringUtils.substringBeforeLast(className, "$$Lambda");
        Class<?> ownerClass = null;
        if (ownerClassName.equals(className)) {
            ownerClass = lambda.getClass().getDeclaringClass();
        } else {
            try {
                ownerClass = Class.forName(ownerClassName);
            } catch (ClassNotFoundException ignored) {
            }
        }
        if (ownerClass == null) {
            throw new IllegalArgumentException(
                "Cannot infer owner class. Please pass a lambda or anonymous class inline to RegValue#of."
            );
        }
        return ownerClass;
    }

    private static RegisterFor getRegisterFor(Class<?> clazz) {
        final var annotation = clazz.getDeclaredAnnotation(RegisterFor.class);
        if (annotation == null) {
            throw new IllegalArgumentException("Missing @RegisterFor on " + clazz + ".");
        }
        return annotation;
    }

    private static void validateSupplier(Supplier<?> supplier, Class<?> registryClass) {
        final var lambdaType = TypeResolver.resolveRawArgument(Supplier.class, supplier.getClass());
        if (!registryClass.isAssignableFrom(lambdaType)) {
            throw new ClassCastException(
                lambdaType + " cannot be used as supplier return type for registry " + registryClass
            );
        }
    }

    @Override
    public ResourceKey<Registry<R>> registryKey() {
        return registry;
    }

    @Override
    public DeferredHolder<R, T> holder() {
        final var result = holder;
        if (registry == null) {
            throw new IllegalStateException("Resource key not yet known");
        }
        return result;
    }

    final void init(ResourceKey<R> key) {
        initialValue = null;
        holder = createHolder(key);
    }

    DeferredHolder<R, T> createHolder(ResourceKey<R> key) {
        return DeferredHolder.create(key);
    }

    static final class BlockValueImpl<B extends Block> extends RegValueImpl<Block, B> implements BlockValue<B> {
        BlockValueImpl(Supplier<B> initialValue) {
            super(initialValue, Registries.BLOCK);
        }

        @Override
        public DeferredBlock<B> holder() {
            return (DeferredBlock<B>)super.holder();
        }

        @Override
        DeferredHolder<Block, B> createHolder(ResourceKey<Block> key) {
            return DeferredBlock.createBlock(key);
        }
    }

    static final class ItemValueImpl<I extends Item> extends RegValueImpl<Item, I> implements ItemValue<I> {
        ItemValueImpl(Supplier<I> initialValue) {
            super(initialValue, Registries.ITEM);
        }

        @Override
        public DeferredItem<I> holder() {
            return (DeferredItem<I>)super.holder();
        }

        @Override
        DeferredHolder<Item, I> createHolder(ResourceKey<Item> key) {
            return DeferredItem.createItem(key);
        }
    }

    static final class ComponentTypeValueImpl<D>
        extends RegValueImpl<DataComponentType<?>, DataComponentType<D>>
        implements ComponentTypeValue<D> {
        ComponentTypeValueImpl(Supplier<DataComponentType<D>> initialValue) {
            super(initialValue, Registries.DATA_COMPONENT_TYPE);
        }
    }
}
