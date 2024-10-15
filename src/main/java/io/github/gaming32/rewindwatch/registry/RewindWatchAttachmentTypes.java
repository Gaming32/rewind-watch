package io.github.gaming32.rewindwatch.registry;

import io.github.gaming32.rewindwatch.RewindWatch;
import io.github.gaming32.rewindwatch.state.EntityEffect;
import io.github.gaming32.rewindwatch.state.LockedPlayerState;
import io.github.gaming32.rewindwatch.state.PlayerAnimationState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class RewindWatchAttachmentTypes {
    private static final DeferredRegister<AttachmentType<?>> REGISTER =
        DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, RewindWatch.MOD_ID);

    public static final Supplier<AttachmentType<EntityEffect>> ENTITY_EFFECT = REGISTER.register(
        "entity_effect", () ->
            AttachmentType.<EntityEffect>builder(() -> EntityEffect.Simple.NONE)
                .serialize(EntityEffect.CODEC)
                .build()
    );
    public static final Supplier<AttachmentType<PlayerAnimationState>> PLAYER_ANIMATION_STATE = REGISTER.register(
        "player_animation_state", () -> AttachmentType.builder(() -> PlayerAnimationState.NONE).build()
    );
    public static final Supplier<AttachmentType<LockedPlayerState>> LOCKED_PLAYER_STATE = REGISTER.register(
        "locked_player_state", () ->
            AttachmentType.builder(() -> LockedPlayerState.NONE)
                .serialize(LockedPlayerState.CODEC)
                .build()
    );

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }
}
