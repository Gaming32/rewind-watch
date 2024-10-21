package io.github.gaming32.rewindwatch.registry;

import io.github.gaming32.annreg.RegisterFor;
import io.github.gaming32.annreg.value.RegValue;
import io.github.gaming32.rewindwatch.RewindWatch;
import io.github.gaming32.rewindwatch.state.EntityEffect;
import io.github.gaming32.rewindwatch.state.LivingAnimationState;
import io.github.gaming32.rewindwatch.state.LockedPlayerState;
import io.github.gaming32.rewindwatch.util.RWUtils;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;

import java.util.Set;
import java.util.function.Supplier;

@RegisterFor(modid = RewindWatch.MOD_ID, registry = "neoforge:attachment_types")
public class RewindWatchAttachmentTypes {
    public static final Supplier<AttachmentType<EntityEffect>> ENTITY_EFFECT = RegValue.of(() ->
        AttachmentType.<EntityEffect>builder(() -> EntityEffect.Simple.NONE)
            .serialize(EntityEffect.CODEC)
            .build()
    );
    public static final Supplier<AttachmentType<LivingAnimationState>> PLAYER_ANIMATION_STATE = RegValue.of(() ->
        AttachmentType.builder(() -> LivingAnimationState.NONE).build()
    );
    public static final Supplier<AttachmentType<LockedPlayerState>> LOCKED_PLAYER_STATE = RegValue.of(() ->
        AttachmentType.builder(() -> LockedPlayerState.NONE)
            .serialize(LockedPlayerState.CODEC)
            .build()
    );
    public static final Supplier<AttachmentType<Vec3>> CLIENT_VELOCITY = RegValue.of(() ->
        AttachmentType.builder(() -> Vec3.ZERO).build()
    );
    public static final Supplier<AttachmentType<Set<GlobalPos>>> OWNED_CHUNKS = RegValue.of(() ->
        AttachmentType.<Set<GlobalPos>>builder(() -> Set.of())
            .serialize(NeoForgeExtraCodecs.setOf(GlobalPos.CODEC))
            .copyHandler(RWUtils.valueCopy())
            .copyOnDeath()
            .build()
    );
}
