package io.github.gaming32.rewindwatch;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class RewindWatchAttachmentTypes {
    private static final DeferredRegister<AttachmentType<?>> REGISTER = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, RewindWatch.MOD_ID);

    public static final Supplier<AttachmentType<EntityEffect>> ENTITY_EFFECT = REGISTER.register(
        "entity_effect", () -> AttachmentType.<EntityEffect>builder(() -> EntityEffect.Simple.NONE).build()
    );

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }
}
