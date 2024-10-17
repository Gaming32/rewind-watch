package io.github.gaming32.rewindwatch.registry;

import io.github.gaming32.rewindwatch.RewindWatch;
import io.github.gaming32.rewindwatch.state.EntityEffect;
import io.github.gaming32.rewindwatch.state.LivingAnimationState;
import io.github.gaming32.rewindwatch.state.LockedPlayerState;
import io.github.gaming32.rewindwatch.util.RWUtils;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Set;
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
    public static final Supplier<AttachmentType<LivingAnimationState>> PLAYER_ANIMATION_STATE = REGISTER.register(
        "player_animation_state", () -> AttachmentType.builder(() -> LivingAnimationState.NONE).build()
    );
    public static final Supplier<AttachmentType<LockedPlayerState>> LOCKED_PLAYER_STATE = REGISTER.register(
        "locked_player_state", () ->
            AttachmentType.builder(() -> LockedPlayerState.NONE)
                .serialize(LockedPlayerState.CODEC)
                .build()
    );
    public static final Supplier<AttachmentType<Vec3>> CLIENT_VELOCITY = REGISTER.register(
        "client_velocity", () -> AttachmentType.builder(() -> Vec3.ZERO).build()
    );
    public static final Supplier<AttachmentType<Set<GlobalPos>>> OWNED_CHUNKS = REGISTER.register(
        "owned_chunks", () ->
            AttachmentType.<Set<GlobalPos>>builder(() -> Set.of())
                .serialize(NeoForgeExtraCodecs.setOf(GlobalPos.CODEC))
                .copyHandler(RWUtils.valueCopy())
                .copyOnDeath()
                .build()
    );

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }
}
