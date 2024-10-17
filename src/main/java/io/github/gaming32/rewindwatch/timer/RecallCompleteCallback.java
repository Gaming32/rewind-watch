package io.github.gaming32.rewindwatch.timer;

import io.github.gaming32.rewindwatch.ResourceLocations;
import io.github.gaming32.rewindwatch.util.RWAttachments;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.timers.TimerCallback;
import net.minecraft.world.level.timers.TimerQueue;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record RecallCompleteCallback(UUID player) implements TimerCallback<MinecraftServer> {
    @Override
    public void handle(MinecraftServer obj, @NotNull TimerQueue<MinecraftServer> manager, long gameTime) {
        final var player = obj.getPlayerList().getPlayer(this.player);
        if (player == null) return;
        RWAttachments.unlockMovement(player);
    }

    public static class Serializer extends TimerCallback.Serializer<MinecraftServer, RecallCompleteCallback> {
        public Serializer() {
            super(ResourceLocations.rewindWatch("recall_sound"), RecallCompleteCallback.class);
        }

        @Override
        public void serialize(CompoundTag tag, RecallCompleteCallback callback) {
            tag.putUUID("player", callback.player);
        }

        @Override
        public @NotNull RecallCompleteCallback deserialize(CompoundTag tag) {
            return new RecallCompleteCallback(tag.getUUID("player"));
        }
    }
}
