package io.github.gaming32.annreg.value;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public sealed interface AttachmentTypeValue<T> extends RegValue<AttachmentType<?>, AttachmentType<T>>
    permits RegValueImpl.AttachmentTypeValueImpl {
    static <T> AttachmentTypeValue<T> of(Supplier<AttachmentType<T>> supplier) {
        RegValueImpl.validateRegisterFor(supplier, NeoForgeRegistries.Keys.ATTACHMENT_TYPES);
        return new RegValueImpl.AttachmentTypeValueImpl<>(supplier);
    }
}
