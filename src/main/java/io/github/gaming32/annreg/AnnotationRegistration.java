package io.github.gaming32.annreg;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import io.github.gaming32.annreg.value.RegValue;
import io.github.gaming32.annreg.value.RegValueImpl;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.ModLoadingIssue;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.exception.UncheckedReflectiveOperationException;
import org.jetbrains.annotations.ApiStatus;
import org.objectweb.asm.Type;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@ApiStatus.Internal
@Mod(AnnotationRegistration.MOD_ID)
public class AnnotationRegistration {
    public static final String MOD_ID = "annreg";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Map<ResourceKey<?>, Class<?>> REG_TYPES = findRegistryTypes();

    private final Multimap<ResourceKey<?>, ModFileScanData.AnnotationData> annotations;

    public AnnotationRegistration() {
        annotations = ArrayListMultimap.create();
        final var targetType = Type.getType(RegisterFor.class);
        for (final var scanData : ModList.get().getAllScanData()) {
            for (final var annotation : scanData.getAnnotations()) {
                if (!annotation.annotationType().equals(targetType)) continue;
                ResourceLocation registry;
                try {
                    registry = ResourceLocation.parse((String)annotation.annotationData().get("registry"));
                } catch (ResourceLocationException e) {
                    issue(
                        ModLoadingIssue.warning(
                            "annreg.error.invalid_registry",
                            annotation.annotationData().get("registry"),
                            annotation.clazz().getClassName()
                        )
                            .withAffectedModFile(scanData.getIModInfoData().getFirst().getFile())
                            .withCause(e)
                    );
                    continue;
                }
                annotations.put(ResourceKey.createRegistryKey(registry), annotation);
            }
        }

        for (final var entry : annotations.asMap().entrySet()) {
            LOGGER.info("Found {} classes for registry {}", entry.getValue().size(), entry.getKey().location());
        }

        final var registered = new HashSet<>();
        final var iter = annotations.values().iterator();
        while (iter.hasNext()) {
            final var annotation = iter.next();
            final var modId = (String)annotation.annotationData().get("modid");
            if (!registered.add(modId)) continue;
            final var container = ModList.get().getModContainerById(modId).orElse(null);
            if (container == null) {
                issue(ModLoadingIssue.warning(
                    "annreg.error.missing_mod",
                    modId, annotation.clazz().getClassName()
                ));
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
            if (!modId.equals(annotation.annotationData().get("modid"))) continue;
            registerForClass(
                registry,
                Objects.requireNonNullElse((String)annotation.annotationData().get("namespace"), modId),
                getRequiredClass(annotation.clazz())
            );
        }
    }

    private void registerForClass(Registry<?> registry, String namespace, Class<?> clazz) {
        final ResourceLocation idTemplate;
        try {
            idTemplate = ResourceLocation.fromNamespaceAndPath(namespace, "");
        } catch (ResourceLocationException e) {
            issue(ModLoadingIssue.warning(
                "annreg.error.invalid_namespace",
                namespace, clazz.getName()
            ).withCause(e));
            return;
        }
        for (final var field : clazz.getDeclaredFields()) {
            if (!RegValue.class.isAssignableFrom(field.getType())) continue;
            registerForField(registry, idTemplate, field);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void registerForField(Registry<T> registry, ResourceLocation idTemplate, Field field) {
        if (!Modifier.isStatic(field.getModifiers())) {
            issue(ModLoadingIssue.warning(
                "annreg.error.nonstatic_field", field
            ));
            return;
        }
        final RegValueImpl<T, ?> value;
        try {
            field.setAccessible(true);
            value = (RegValueImpl<T, ?>)field.get(null);
        } catch (IllegalAccessException e) {
            issue(ModLoadingIssue.warning(
                "annreg.error.access_failure", field
            ).withCause(e));
            return;
        }
        final var key = ResourceKey.create(
            registry.key(), idTemplate.withPath(field.getName().toLowerCase(Locale.ROOT))
        );
        Registry.register(registry, key, value.init(key));
    }

    private static Map<ResourceKey<?>, Class<?>> findRegistryTypes() {
        final var fields = ArrayUtils.addAll(
            Registries.class.getDeclaredFields(),
            NeoForgeRegistries.Keys.class.getDeclaredFields()
        );
        final var result = HashMap.<ResourceKey<?>, Class<?>>newHashMap(fields.length);
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
                    result.put((ResourceKey<?>)field.get(null), clazz);
                } catch (IllegalAccessException e) {
                    throw new UncheckedReflectiveOperationException(e);
                }
            }
        }
        return result;
    }

    private static Class<?> getRequiredClass(Type clazz) {
        try {
            return Class.forName(clazz.getClassName());
        } catch (ReflectiveOperationException e) {
            throw new UncheckedReflectiveOperationException(e);
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    private static void issue(ModLoadingIssue issue) {
        ModLoader.addLoadingIssue(issue);
    }
}
