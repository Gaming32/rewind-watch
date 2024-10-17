package io.github.gaming32.rewindwatch.timer;

import io.github.gaming32.rewindwatch.ResourceLocations;
import io.github.gaming32.rewindwatch.registry.RewindWatchSoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.timers.TimerCallback;
import net.minecraft.world.level.timers.TimerQueue;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record RecallSoundCallback(UUID player) implements TimerCallback<MinecraftServer> {
    @Override
    public void handle(MinecraftServer obj, @NotNull TimerQueue<MinecraftServer> manager, long gameTime) {
        final var player = obj.getPlayerList().getPlayer(this.player);
        if (player == null) return;
        player.playNotifySound(RewindWatchSoundEvents.ITEM_REWIND_WATCH_RECALLED.get(), SoundSource.PLAYERS, 1f, 1f);
    }

    public static class Serializer extends TimerCallback.Serializer<MinecraftServer, RecallSoundCallback> {
        public Serializer() {
            super(ResourceLocations.rewindWatch("recall_sound"), RecallSoundCallback.class);
        }

        @Override
        public void serialize(CompoundTag tag, RecallSoundCallback callback) {
            tag.putUUID("player", callback.player);
        }

        @Override
        public @NotNull RecallSoundCallback deserialize(CompoundTag tag) {
            return new RecallSoundCallback(tag.getUUID("player"));
        }
    }
}
