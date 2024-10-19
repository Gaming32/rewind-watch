package io.github.gaming32.rewindwatch.trigger;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.gaming32.rewindwatch.util.RWUtils;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class WatchRecallTrigger extends SimpleCriterionTrigger<WatchRecallTrigger.TriggerInstance> {
    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(
        ServerPlayer player, ItemStack watch,
        ServerLevel startLevel, Vec3 startPos,
        ServerLevel recallLevel, Vec3 recallPos,
        int recallDuration
    ) {
        trigger(player, instance -> instance.matches(watch, startLevel, startPos, recallLevel, recallPos, recallDuration));
    }

    public static Builder builder() {
        return new Builder();
    }

    public record TriggerInstance(
        Optional<ContextAwarePredicate> player,
        Optional<ItemPredicate> watch,
        Optional<LocationPredicate> startLocation,
        Optional<LocationPredicate> recallLocation,
        MinMaxBounds.Ints recallDuration
    ) implements SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                ItemPredicate.CODEC.optionalFieldOf("watch").forGetter(TriggerInstance::watch),
                LocationPredicate.CODEC.optionalFieldOf("start_location").forGetter(TriggerInstance::startLocation),
                LocationPredicate.CODEC.optionalFieldOf("recall_location").forGetter(TriggerInstance::recallLocation),
                MinMaxBounds.Ints.CODEC
                    .optionalFieldOf("recall_duration", MinMaxBounds.Ints.ANY)
                    .forGetter(TriggerInstance::recallDuration)
            ).apply(instance, TriggerInstance::new)
        );

        public boolean matches(
            ItemStack watch,
            ServerLevel startLevel, Vec3 startPos,
            ServerLevel recallLevel, Vec3 recallPos,
            int recallDuration
        ) {
            if (this.watch.isPresent() && !this.watch.get().test(watch)) {
                return false;
            }
            if (this.startLocation.isPresent() && !RWUtils.matches(this.startLocation.get(), startLevel, startPos)) {
                return false;
            }
            if (this.recallLocation.isPresent() && !RWUtils.matches(this.recallLocation.get(), recallLevel, recallPos)) {
                return false;
            }
            if (!this.recallDuration.matches(recallDuration)) {
                return false;
            }
            return true;
        }
    }

    public static class Builder {
        private Optional<ContextAwarePredicate> player = Optional.empty();
        private Optional<ItemPredicate> watch = Optional.empty();
        private Optional<LocationPredicate> startLocation = Optional.empty();
        private Optional<LocationPredicate> recallLocation = Optional.empty();
        private MinMaxBounds.Ints recallDuration = MinMaxBounds.Ints.ANY;

        private Builder() {
        }

        public Builder player(ContextAwarePredicate player) {
            this.player = Optional.of(player);
            return this;
        }

        public Builder player(EntityPredicate player) {
            return player(EntityPredicate.wrap(player));
        }

        public Builder watch(ItemPredicate watch) {
            this.watch = Optional.of(watch);
            return this;
        }

        public Builder startLocation(LocationPredicate startLocation) {
            this.startLocation = Optional.of(startLocation);
            return this;
        }

        public Builder recallLocation(LocationPredicate recallLocation) {
            this.recallLocation = Optional.of(recallLocation);
            return this;
        }

        public Builder recallDuration(MinMaxBounds.Ints recallDuration) {
            this.recallDuration = recallDuration;
            return this;
        }

        public Criterion<TriggerInstance> build() {
            return RewindWatchCriteriaTriggers.WATCH_RECALL.get().createCriterion(
                new TriggerInstance(player, watch, startLocation, recallLocation, recallDuration)
            );
        }
    }
}
