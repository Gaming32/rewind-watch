package io.github.gaming32.annreg;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.mojang.logging.LogUtils;
import net.minecraft.Optionull;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.exception.UncheckedReflectiveOperationException;
import org.objectweb.asm.Type;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Mod(AnnotationRegistration.MOD_ID)
public class AnnotationRegistration {
    public static final String MOD_ID = "annreg";
    private static final Logger LOGGER = LogUtils.getLogger();

    static final Map<Class<?>, ResourceKey<?>> REG_TYPES = findRegistryTypes();

    private final Multimap<ResourceKey<?>, ModFileScanData.AnnotationData> annotations;

    public AnnotationRegistration() {
        final var targetType = Type.getType(RegisterFor.class);
        annotations = ModList.get()
            .getAllScanData()
            .stream()
            .map(ModFileScanData::getAnnotations)
            .flatMap(Collection::stream)
            .filter(x -> x.annotationType().equals(targetType))
            .collect(Multimaps.toMultimap(
                AnnotationRegistration::findRegistryType,
                Function.identity(),
                ArrayListMultimap::create
            ));

        for (final var data : annotations.removeAll(null)) {
            LOGGER.error(
                "Could not find registry for class {}, requested by {}",
                Optionull.map(data.annotationData().get("registry"), x -> ((Type)x).getClassName()),
                getOwnerDescription(data)
            );
        }

        final var iter = annotations.values().iterator();
        while (iter.hasNext()) {
            final var annotation = iter.next();
            final var modId = (String)annotation.annotationData().get("modid");
            final var container = ModList.get().getModContainerById(modId).orElse(null);
            if (container == null) {
                LOGGER.error(
                    "Could not find mod ID {} for registration, requested by {}",
                    modId, getOwnerDescription(annotation)
                );
                iter.remove();
                continue;
            }
            final var bus = container.getEventBus();
            if (bus == null) continue;
            bus.addListener(RegisterEvent.class, event -> register(modId, event));
        }
    }

    private void register(String modId, RegisterEvent event) {
        final var registry =  event.getRegistry();
        for (final var annotation : annotations.get(event.getRegistryKey())) {
            final var requestedModId = (String)annotation.annotationData().get("modid");
            if (!modId.equals(requestedModId)) continue;
            registerForClass(
                registry,
                Objects.requireNonNullElse((String)annotation.annotationData().get("namespace"), requestedModId),
                getRequiredClass(annotation.clazz())
            );
        }
    }

    private void registerForClass(Registry<?> registry, String namespace, Class<?> clazz) {
        if (!ResourceLocation.isValidNamespace(namespace)) {
            LOGGER.error("Invalid namespace for registration in {}: {}", clazz, namespace);
            return;
        }
        final var idTemplate = ResourceLocation.fromNamespaceAndPath(namespace, "");
        for (final var field : clazz.getDeclaredFields()) {
            if (!RegValue.class.isAssignableFrom(field.getType())) continue;
            registerForField(registry, idTemplate, field);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void registerForField(Registry<T> registry, ResourceLocation idTemplate, Field field) {
        if (!Modifier.isStatic(field.getModifiers())) {
            LOGGER.error("Cannot register to non-static field {}", field);
            return;
        }
        final RegValueImpl<T, ?> value;
        try {
            field.setAccessible(true);
            value = (RegValueImpl<T, ?>)field.get(null);
        } catch (IllegalAccessException e) {
            LOGGER.error("Failed to access {}", field, e);
            return;
        }
        final var key = ResourceKey.create(
            registry.key(), idTemplate.withPath(field.getName().toLowerCase(Locale.ROOT))
        );
        Registry.register(registry, key, value.initialValue.get());
        value.init(key);
    }

    private static Map<Class<?>, ResourceKey<?>> findRegistryTypes() {
        final var fields = Registries.class.getDeclaredFields();
        final var result = HashMap.<Class<?>, ResourceKey<?>>newHashMap(fields.length);
        for (final var field : fields) {
            if (field.getType() != ResourceKey.class || !Modifier.isPublic(field.getModifiers())) continue;
            if (!(field.getGenericType() instanceof ParameterizedType generic)) continue;
            if (!(ArrayUtils.get(generic.getActualTypeArguments(), 0) instanceof ParameterizedType inner1)) continue;
            if (inner1.getRawType() != Registry.class) continue;
            var inner2 = ArrayUtils.get(inner1.getActualTypeArguments(), 0);
            if (inner2 instanceof ParameterizedType parameterized) {
                inner2 = parameterized.getRawType();
            }
            if (inner2 instanceof Class<?> clazz) {
                try {
                    result.put(clazz, (ResourceKey<?>)field.get(null));
                } catch (IllegalAccessException e) {
                    throw new UncheckedReflectiveOperationException(e);
                }
            }
        }
        return result;
    }

    private static ResourceKey<?> findRegistryType(ModFileScanData.AnnotationData annotationData) {
        final var registryType = (Type)annotationData.annotationData().get("registry");
        try {
            final var clazz = Class.forName(
                registryType.getClassName(),
                false,
                AnnotationRegistration.class.getClassLoader()
            );
            return REG_TYPES.get(clazz);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static String getOwnerDescription(ModFileScanData.AnnotationData annotation) {
        var result = annotation.clazz().getClassName();
        if (annotation.memberName().equals(result)) {
            return result;
        }
        return result + '#' + annotation.memberName();
    }

    private static Class<?> getRequiredClass(Type clazz) {
        try {
            return Class.forName(clazz.getClassName());
        } catch (ReflectiveOperationException e) {
            throw new UncheckedReflectiveOperationException(e);
        }
    }
}
