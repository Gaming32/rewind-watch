package io.github.gaming32.rewindwatch.entity;

import com.mojang.logging.LogUtils;
import io.github.gaming32.rewindwatch.registry.RewindWatchAttachmentTypes;
import io.github.gaming32.rewindwatch.registry.RewindWatchEntityDataSerializers;
import io.github.gaming32.rewindwatch.state.EntityEffect;
import io.github.gaming32.rewindwatch.state.LivingAnimationState;
import io.github.gaming32.rewindwatch.state.PoseData;
import io.github.gaming32.rewindwatch.util.RWUtils;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

public class FakePlayer extends LivingEntity implements IEntityWithComplexSpawn {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final byte DEFAULT_PLAYER_MODEL_CUSTOMIZATION = (byte)((1 << PlayerModelPart.values().length) - 1);

    private static final EntityDataAccessor<Optional<UUID>> DATA_PLAYER_UUID =
        SynchedEntityData.defineId(FakePlayer.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<LivingAnimationState> DATA_ANIMATION_STATE =
        SynchedEntityData.defineId(FakePlayer.class, RewindWatchEntityDataSerializers.ANIMATION_STATE.get());
    private static final EntityDataAccessor<EntityEffect> DATA_CURRENT_EFFECT =
        SynchedEntityData.defineId(FakePlayer.class, RewindWatchEntityDataSerializers.ENTITY_EFFECT.get());
    private static final EntityDataAccessor<Byte> DATA_MODEL_CUSTOMIZATION =
        SynchedEntityData.defineId(FakePlayer.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Vec3> DATA_CLOAK =
        SynchedEntityData.defineId(FakePlayer.class, RewindWatchEntityDataSerializers.VEC3.get());
    private static final EntityDataAccessor<PoseData> DATA_POSE_DATA =
        SynchedEntityData.defineId(FakePlayer.class, RewindWatchEntityDataSerializers.POSE_DATA.get());
    private static final EntityDataAccessor<Boolean> DATA_HAS_ELYTRA =
        SynchedEntityData.defineId(FakePlayer.class, EntityDataSerializers.BOOLEAN);

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
        setOnGround(entity.onGround());

        setPlayerUuid(Optional.of(entity.getUUID()));
        setDeltaMovement(entity.getDeltaMovement());
        entity.getExistingData(RewindWatchAttachmentTypes.PLAYER_ANIMATION_STATE).ifPresent(this::setAnimationState);
        if (entity instanceof Player player) {
            setModelCustomization(player.getEntityData().get(Player.DATA_PLAYER_MODE_CUSTOMISATION));
            setCloak(new Vec3(player.xCloak, player.yCloak, player.zCloak));
        }
        setPoseData(PoseData.from(entity));
        setHasElytra(entity.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA));
    }

    public void reapplySomeData(Player player) {
        player.setPose(getPose());
        if (isFallFlying()) {
            player.startFallFlying();
        } else {
            player.stopFallFlying();
        }
        player.setOnGround(onGround());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_PLAYER_UUID, Optional.empty());
        builder.define(DATA_ANIMATION_STATE, LivingAnimationState.NONE);
        builder.define(DATA_CURRENT_EFFECT, EntityEffect.Simple.GRAYSCALE);
        builder.define(DATA_MODEL_CUSTOMIZATION, DEFAULT_PLAYER_MODEL_CUSTOMIZATION);
        builder.define(DATA_CLOAK, Vec3.ZERO);
        builder.define(DATA_POSE_DATA, PoseData.NONE);
        builder.define(DATA_HAS_ELYTRA, false);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setPose(Pose.BY_ID.apply(compound.getByte("pose")));
        setYBodyRot(compound.getFloat("body_rot"));
        setYHeadRot(compound.getFloat("head_rot"));

        if (compound.hasUUID("player_uuid")) {
            setPlayerUuid(Optional.of(compound.getUUID("player_uuid")));
        } else {
            setPlayerUuid(Optional.empty());
        }
        LivingAnimationState.CODEC.parse(NbtOps.INSTANCE, compound.get("animation"))
            .resultOrPartial(Util.prefix("Failed to parse LivingAnimationState: ", LOGGER::error))
            .ifPresent(this::setAnimationState);
        EntityEffect.CODEC.parse(NbtOps.INSTANCE, compound.get("current_effect"))
            .resultOrPartial(Util.prefix("Failed to parse EntityEffect: ", LOGGER::error))
            .ifPresent(this::setCurrentEffect);
        setModelCustomization(compound.getByte("model_customization"));
        setCloak(RWUtils.getVec3(compound, "cloak"));
        PoseData.CODEC.parse(NbtOps.INSTANCE, compound.get("pose_data"))
            .resultOrPartial(Util.prefix("Failed to parse PoseData: ", LOGGER::error))
            .ifPresent(this::setPoseData);
        setHasElytra(compound.getBoolean("has_elytra"));
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putByte("pose", (byte)getPose().id());
        compound.putFloat("body_rot", yBodyRot);
        compound.putFloat("head_rot", yHeadRot);

        getPlayerUuid().ifPresent(uuid -> compound.putUUID("player_uuid", uuid));
        compound.put("animation", LivingAnimationState.CODEC.encodeStart(NbtOps.INSTANCE, getAnimationState()).getOrThrow());
        compound.put("current_effect", EntityEffect.CODEC.encodeStart(NbtOps.INSTANCE, getCurrentEffect()).getOrThrow());
        compound.putByte("model_customization", getModelCustomization());
        RWUtils.putVec3(compound, "cloak", getCloak());
        compound.put("pose_data", PoseData.CODEC.encodeStart(NbtOps.INSTANCE, getPoseData()).getOrThrow());
        compound.putBoolean("has_elytra", getHasElytra());
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        buffer.writeFloat(yBodyRot);
        buffer.writeFloat(yHeadRot);
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf additionalData) {
        yBodyRot = yBodyRotO = additionalData.readFloat();
        yHeadRot = yHeadRotO = additionalData.readFloat();
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
        updateFluidHeightAndDoFluidPushing();
    }

    @Override
    @SuppressWarnings("deprecation") // The deprecation indicates that we shouldn't call it, not we shouldn't override it
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public int getFallFlyingTicks() {
        return getPoseData().fallFlyTicks();
    }

    @Override
    public float getSwimAmount(float partialTicks) {
        return getPoseData().swimAmount();
    }

    public Optional<UUID> getPlayerUuid() {
        return entityData.get(DATA_PLAYER_UUID);
    }

    public void setPlayerUuid(Optional<UUID> playerUuid) {
        entityData.set(DATA_PLAYER_UUID, playerUuid);
    }

    public LivingAnimationState getAnimationState() {
        return entityData.get(DATA_ANIMATION_STATE);
    }

    public void setAnimationState(LivingAnimationState animationSpeed) {
        entityData.set(DATA_ANIMATION_STATE, animationSpeed);
    }

    public EntityEffect getCurrentEffect() {
        return entityData.get(DATA_CURRENT_EFFECT);
    }

    public void setCurrentEffect(EntityEffect effect) {
        entityData.set(DATA_CURRENT_EFFECT, effect);
    }

    public byte getModelCustomization() {
        return entityData.get(DATA_MODEL_CUSTOMIZATION);
    }

    public void setModelCustomization(byte customization) {
        entityData.set(DATA_MODEL_CUSTOMIZATION, customization);
    }

    public boolean isModelPartShown(PlayerModelPart part) {
        return (getModelCustomization() & part.getMask()) != 0;
    }

    public Vec3 getCloak() {
        return entityData.get(DATA_CLOAK);
    }

    public void setCloak(Vec3 cloak) {
        entityData.set(DATA_CLOAK, cloak);
    }

    public PoseData getPoseData() {
        return entityData.get(DATA_POSE_DATA);
    }

    public void setPoseData(PoseData poseData) {
        entityData.set(DATA_POSE_DATA, poseData);
    }

    public boolean getHasElytra() {
        return entityData.get(DATA_HAS_ELYTRA);
    }

    public void setHasElytra(boolean hasElytra) {
        entityData.set(DATA_HAS_ELYTRA, hasElytra);
    }
}
