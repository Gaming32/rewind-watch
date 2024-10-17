package io.github.gaming32.rewindwatch.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.gaming32.rewindwatch.registry.RewindWatchAttachmentTypes;
import io.github.gaming32.rewindwatch.util.RWAttachments;
import io.github.gaming32.rewindwatch.util.RWCodecs;
import io.github.gaming32.rewindwatch.util.RWStreamCodecs;
import io.github.gaming32.rewindwatch.util.RWUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.EnumSet;
import java.util.Set;

public record LockedPlayerState(
    LivingFacingAngles facing,
    LivingAnimationState animation,
    Set<PlayerModelPart> modelCustomization,
    Vec3 cloak,
    PoseData poseData,
    Vec3 deltaMovement,
    boolean onGround
) {
    public static final LockedPlayerState NONE = new LockedPlayerState(
        LivingFacingAngles.ORIGIN,
        LivingAnimationState.NONE,
        EnumSet.allOf(PlayerModelPart.class),
        Vec3.ZERO,
        PoseData.NONE,
        Vec3.ZERO,
        true
    );

    public static final Codec<LockedPlayerState> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            LivingFacingAngles.CODEC.fieldOf("facing").forGetter(LockedPlayerState::facing),
            LivingAnimationState.CODEC
                .optionalFieldOf("animation", LivingAnimationState.NONE)
                .forGetter(LockedPlayerState::animation),
            NeoForgeExtraCodecs.setOf(RWCodecs.PLAYER_MODEL_PART)
                .optionalFieldOf("model_customization", EnumSet.allOf(PlayerModelPart.class))
                .forGetter(LockedPlayerState::modelCustomization),
            Vec3.CODEC.optionalFieldOf("cloak", Vec3.ZERO).forGetter(LockedPlayerState::cloak),
            PoseData.CODEC.optionalFieldOf("pose_data", PoseData.NONE).forGetter(LockedPlayerState::poseData),
            Vec3.CODEC.optionalFieldOf("delta_movement", Vec3.ZERO).forGetter(LockedPlayerState::deltaMovement),
            Codec.BOOL.fieldOf("on_ground").forGetter(LockedPlayerState::onGround)
        ).apply(instance, LockedPlayerState::new)
    );
    public static final StreamCodec<ByteBuf, LockedPlayerState> STREAM_CODEC = NeoForgeStreamCodecs.composite(
        LivingFacingAngles.STREAM_CODEC, LockedPlayerState::facing,
        LivingAnimationState.STREAM_CODEC, LockedPlayerState::animation,
        RWStreamCodecs.PLAYER_MODEL_PART_SET, LockedPlayerState::modelCustomization,
        RWStreamCodecs.VEC3, LockedPlayerState::cloak,
        PoseData.STREAM_CODEC, LockedPlayerState::poseData,
        RWStreamCodecs.VEC3, LockedPlayerState::deltaMovement,
        ByteBufCodecs.BOOL, LockedPlayerState::onGround,
        LockedPlayerState::new
    );

    public static LockedPlayerState from(ServerPlayer player) {
        return new LockedPlayerState(
            LivingFacingAngles.from(player),
            player.getData(RewindWatchAttachmentTypes.PLAYER_ANIMATION_STATE),
            RWUtils.unpackModelCustomization(player.getEntityData().get(Player.DATA_PLAYER_MODE_CUSTOMISATION)),
            new Vec3(player.xCloak, player.yCloak, player.zCloak),
            new PoseData(player.getFallFlyingTicks(), player.getSwimAmount(1f)),
            RWAttachments.getVelocity(player),
            player.onGround()
        );
    }

    public void apply(Entity entity) {
        facing.apply(entity);
        entity.setDeltaMovement(deltaMovement);
        entity.setOnGround(onGround);
        if (entity instanceof LivingEntity living) {
            animation.apply(living);
            poseData.apply(living);
        }
        if (entity instanceof Player player) {
            player.xCloak = player.xCloakO = cloak.x;
            player.yCloak = player.yCloakO = cloak.y;
            player.zCloak = player.zCloakO = cloak.z;
        }
    }
}
