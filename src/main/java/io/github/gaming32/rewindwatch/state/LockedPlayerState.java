package io.github.gaming32.rewindwatch.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.gaming32.rewindwatch.registry.RewindWatchAttachmentTypes;
import io.github.gaming32.rewindwatch.util.RWCodecs;
import io.github.gaming32.rewindwatch.util.RWStreamCodecs;
import io.github.gaming32.rewindwatch.util.RWUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;

import java.util.EnumSet;
import java.util.Set;

public record LockedPlayerState(
    LivingFacingAngles facing,
    PlayerAnimationState animation,
    Set<PlayerModelPart> modelCustomization,
    Vec3 cloak,
    PoseData poseData
) {
    public static final LockedPlayerState NONE = new LockedPlayerState(
        LivingFacingAngles.ORIGIN,
        PlayerAnimationState.NONE,
        EnumSet.allOf(PlayerModelPart.class),
        Vec3.ZERO,
        PoseData.NONE
    );

    public static final Codec<LockedPlayerState> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            LivingFacingAngles.CODEC.fieldOf("facing").forGetter(LockedPlayerState::facing),
            PlayerAnimationState.CODEC
                .optionalFieldOf("animation", PlayerAnimationState.NONE)
                .forGetter(LockedPlayerState::animation),
            NeoForgeExtraCodecs.setOf(RWCodecs.PLAYER_MODEL_PART)
                .optionalFieldOf("model_customization", EnumSet.allOf(PlayerModelPart.class))
                .forGetter(LockedPlayerState::modelCustomization),
            Vec3.CODEC.optionalFieldOf("cloak", Vec3.ZERO).forGetter(LockedPlayerState::cloak),
            PoseData.CODEC.optionalFieldOf("pose_data", PoseData.NONE).forGetter(LockedPlayerState::poseData)
        ).apply(instance, LockedPlayerState::new)
    );
    public static final StreamCodec<ByteBuf, LockedPlayerState> STREAM_CODEC = StreamCodec.composite(
        LivingFacingAngles.STREAM_CODEC, LockedPlayerState::facing,
        PlayerAnimationState.STREAM_CODEC, LockedPlayerState::animation,
        RWStreamCodecs.PLAYER_MODEL_PART_SET, LockedPlayerState::modelCustomization,
        RWStreamCodecs.VEC3, LockedPlayerState::cloak,
        PoseData.STREAM_CODEC, LockedPlayerState::poseData,
        LockedPlayerState::new
    );

    public static LockedPlayerState from(ServerPlayer player) {
        return new LockedPlayerState(
            LivingFacingAngles.from(player),
            player.getData(RewindWatchAttachmentTypes.PLAYER_ANIMATION_STATE),
            RWUtils.unpackModelCustomization(player.getEntityData().get(Player.DATA_PLAYER_MODE_CUSTOMISATION)),
            new Vec3(player.xCloak, player.yCloak, player.zCloak),
            new PoseData(player.getFallFlyingTicks(), player.getSwimAmount(1f))
        );
    }
}
