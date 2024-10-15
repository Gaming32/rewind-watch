package io.github.gaming32.rewindwatch.entity;

import com.mojang.logging.LogUtils;
import io.github.gaming32.rewindwatch.registry.RewindWatchAttachmentTypes;
import io.github.gaming32.rewindwatch.registry.RewindWatchEntityDataSerializers;
import io.github.gaming32.rewindwatch.state.EntityEffect;
import io.github.gaming32.rewindwatch.state.PlayerAnimationState;
import io.github.gaming32.rewindwatch.util.RWUtils;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

public class FakePlayer extends LivingEntity {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final EntityDataAccessor<Optional<UUID>> DATA_PLAYER_UUID =
        SynchedEntityData.defineId(FakePlayer.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Vec3> DATA_PLAYER_SPEED =
        SynchedEntityData.defineId(FakePlayer.class, RewindWatchEntityDataSerializers.VEC3.get());
    private static final EntityDataAccessor<PlayerAnimationState> DATA_ANIMATION_STATE =
        SynchedEntityData.defineId(FakePlayer.class, RewindWatchEntityDataSerializers.PLAYER_ANIMATION_STATE.get());
    private static final EntityDataAccessor<EntityEffect> DATA_CURRENT_EFFECT =
        SynchedEntityData.defineId(FakePlayer.class, RewindWatchEntityDataSerializers.ENTITY_EFFECT.get());

    public FakePlayer(EntityType<? extends FakePlayer> entityType, Level level) {
        super(entityType, level);
    }

    public void copyInformationFrom(LivingEntity entity) {
        setPos(entity.position());
        setPose(entity.getPose());
        setXRot(entity.getXRot());
        setYRot(entity.getYRot());
        setYBodyRot(entity.yBodyRot);
        setYHeadRot(entity.getYHeadRot());
        setSharedFlag(FLAG_FALL_FLYING, entity.isFallFlying());

        setPlayerUuid(Optional.of(entity.getUUID()));
        setPlayerSpeed(entity.getDeltaMovement());
        entity.getExistingData(RewindWatchAttachmentTypes.PLAYER_ANIMATION_STATE).ifPresent(this::setAnimationState);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_PLAYER_UUID, Optional.empty());
        builder.define(DATA_PLAYER_SPEED, Vec3.ZERO);
        builder.define(DATA_ANIMATION_STATE, PlayerAnimationState.NONE);
        builder.define(DATA_CURRENT_EFFECT, EntityEffect.Simple.GRAYSCALE);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("player_uuid")) {
            setPlayerUuid(Optional.of(compound.getUUID("player_uuid")));
        } else {
            setPlayerUuid(Optional.empty());
        }
        setPlayerSpeed(RWUtils.getVec3(compound, "player_speed"));
        setAnimationState(new PlayerAnimationState(
            compound.getFloat("animation_position"),
            compound.getFloat("animation_speed")
        ));
        EntityEffect.CODEC.parse(NbtOps.INSTANCE, compound.get("current_effect"))
            .resultOrPartial(Util.prefix("Failed to parse EntityEffect: ", LOGGER::warn))
            .ifPresent(this::setCurrentEffect);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        getPlayerUuid().ifPresent(uuid -> compound.putUUID("player_uuid", uuid));
        RWUtils.putVec3(compound, "player_speed", getPlayerSpeed());
        final var animation = getAnimationState();
        compound.putFloat("animation_position", animation.position());
        compound.putFloat("animation_speed", animation.speed());
        compound.put("current_effect", EntityEffect.CODEC.encodeStart(NbtOps.INSTANCE, getCurrentEffect()).getOrThrow());
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.GENERIC_KILL)) {
            remove(RemovalReason.KILLED);
            return true;
        }
        return false;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return !source.is(DamageTypes.GENERIC_KILL);
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public @NotNull Iterable<ItemStack> getArmorSlots() {
        return Collections.emptyList();
    }

    @Override
    public @NotNull ItemStack getItemBySlot(@NotNull EquipmentSlot slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(@NotNull EquipmentSlot slot, @NotNull ItemStack stack) {
    }

    @Override
    public @NotNull HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    public void tick() {
        if (
            getCurrentEffect() instanceof EntityEffect.Dissolve dissolve &&
            level().getGameTime() >= dissolve.endTick() &&
            !dissolve.in() &&
            dissolve.type().isTransparent()
        ) {
            discard();
        }
    }

    public Optional<UUID> getPlayerUuid() {
        return entityData.get(DATA_PLAYER_UUID);
    }

    public void setPlayerUuid(Optional<UUID> playerUuid) {
        entityData.set(DATA_PLAYER_UUID, playerUuid);
    }

    public Vec3 getPlayerSpeed() {
        return entityData.get(DATA_PLAYER_SPEED);
    }

    public void setPlayerSpeed(Vec3 speed) {
        entityData.set(DATA_PLAYER_SPEED, speed);
    }

    public PlayerAnimationState getAnimationState() {
        return entityData.get(DATA_ANIMATION_STATE);
    }

    public void setAnimationState(PlayerAnimationState animationSpeed) {
        entityData.set(DATA_ANIMATION_STATE, animationSpeed);
    }

    public EntityEffect getCurrentEffect() {
        return entityData.get(DATA_CURRENT_EFFECT);
    }

    public void setCurrentEffect(EntityEffect effect) {
        entityData.set(DATA_CURRENT_EFFECT, effect);
    }
}
