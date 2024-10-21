package io.github.gaming32.annreg.value;

import io.github.gaming32.annreg.AnnotationRegistration;
import io.github.gaming32.annreg.RegisterFor;
import net.jodah.typetools.TypeResolver;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;
import java.util.function.Supplier;

public class RegValueImpl<R, T extends R> implements RegValue<R, T> {
    private Function<ResourceKey<R>, T> initialValue;
    private final ResourceKey<Registry<R>> registry;
    DeferredHolder<R, T> holder = null;

    public RegValueImpl(Function<ResourceKey<R>, T> initialValue, ResourceKey<Registry<R>> registry) {
        this.initialValue = initialValue;
        this.registry = registry;
    }

    public RegValueImpl(Supplier<T> initialValue, ResourceKey<Registry<R>> registry) {
        this(key -> initialValue.get(), registry);
    }

    public RegValueImpl(Function<ResourceKey<R>, T> initialValue, Object inferenceLambda) {
        this(initialValue, findRegistry(inferenceLambda, false));
    }

    public RegValueImpl(Supplier<T> initialValue, Object inferenceLambda) {
        this(initialValue, findRegistry(inferenceLambda, false));
    }

    RegValueImpl(Function<ResourceKey<R>, T> initialValue) {
        this(initialValue, findRegistry(initialValue, true));
    }

    RegValueImpl(Supplier<T> initialValue) {
        this(initialValue, findRegistry(initialValue, true));
    }

    @SuppressWarnings("unchecked")
    private static <R> ResourceKey<Registry<R>> findRegistry(Object lambda, boolean validateSupplier) {
        final var ownerClass = getLambdaClass(lambda);
        final var registryKey = getRegistry(ownerClass);
        if (!BuiltInRegistries.REGISTRY.containsKey(registryKey.location())) {
            throw new IllegalArgumentException("Could not find builtin registry " + registryKey.location());
        }
        if (validateSupplier) {
            final var registryClass = AnnotationRegistration.REG_TYPES.get(registryKey);
            if (registryClass != null) {
                validateSupplier((Supplier<?>)lambda, registryClass, ownerClass);
            }
        }
        return (ResourceKey<Registry<R>>)registryKey;
    }

    public static void validateRegisterFor(Object lambda, ResourceKey<?> realTarget) {
        final var owner = getLambdaClass(lambda);
        final var target = getRegistry(owner);
        if (target != realTarget) {
            throw new IllegalArgumentException(
                "Registry entered was " + target.location() + ", but " + realTarget.location() + " was expected in " + owner
            );
        }
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
                "Cannot infer owner class on " + lambda + ". Please pass a lambda or anonymous class inline to RegValue#of."
            );
        }
        return ownerClass;
    }

    private static ResourceKey<?> getRegistry(Class<?> clazz) {
        final var annotation = clazz.getDeclaredAnnotation(RegisterFor.class);
        if (annotation == null) {
            throw new IllegalArgumentException("Missing @RegisterFor on " + clazz + ".");
        }
        final var registryName = ResourceLocation.tryParse(annotation.registry());
        if (registryName == null) {
            throw new IllegalArgumentException("Invalid registry key " + annotation.registry());
        }
        return ResourceKey.createRegistryKey(registryName);
    }

    private static void validateSupplier(Object supplier, Class<?> registryClass, Class<?> owner) {
        final var isSupplier = supplier instanceof Supplier<?>;
        final var argIndex = isSupplier ? 0 : 1;
        final var baseType = isSupplier ? Supplier.class : Function.class;
        final var lambdaType = ArrayUtils.get(TypeResolver.resolveRawArguments(baseType, supplier.getClass()), argIndex);
        if (lambdaType == null) return; // No type checking on raw types
        if (!registryClass.isAssignableFrom(lambdaType)) {
            throw new ClassCastException(
                lambdaType + " cannot be used as supplier return type for registry " + registryClass + " in " + owner
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

    public final T init(ResourceKey<R> key) {
        final var value = initialValue.apply(key);
        initialValue = null;
        holder = createHolder(key);
        return value;
    }

    protected DeferredHolder<R, T> createHolder(ResourceKey<R> key) {
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
        protected DeferredHolder<Block, B> createHolder(ResourceKey<Block> key) {
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
        protected DeferredHolder<Item, I> createHolder(ResourceKey<Item> key) {
            return DeferredItem.createItem(key);
        }
    }
}
