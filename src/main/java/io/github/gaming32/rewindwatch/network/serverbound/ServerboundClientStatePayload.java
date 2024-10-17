package io.github.gaming32.rewindwatch.network.serverbound;

import io.github.gaming32.rewindwatch.ResourceLocations;
import io.github.gaming32.rewindwatch.state.LivingAnimationState;
import io.github.gaming32.rewindwatch.util.RWStreamCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record ServerboundClientStatePayload(LivingAnimationState state, Vec3 velocity) implements CustomPacketPayload {
    public static final Type<ServerboundClientStatePayload> TYPE =
        new Type<>(ResourceLocations.rewindWatch("client_state"));
    public static final StreamCodec<ByteBuf, ServerboundClientStatePayload> STREAM_CODEC = StreamCodec.composite(
        LivingAnimationState.STREAM_CODEC, ServerboundClientStatePayload::state,
        RWStreamCodecs.VEC3, ServerboundClientStatePayload::velocity,
        ServerboundClientStatePayload::new
    );

    @Override
    public @NotNull Type<ServerboundClientStatePayload> type() {
        return TYPE;
    }
}
