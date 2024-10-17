package io.github.gaming32.rewindwatch;

import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;

import java.util.Comparator;

public class RewindWatchTicketTypes {
    public static final TicketType<ChunkPos> FAKE_PLAYER = TicketType.create(
        "fake_player", Comparator.comparingLong(ChunkPos::toLong), 40
    );
}
