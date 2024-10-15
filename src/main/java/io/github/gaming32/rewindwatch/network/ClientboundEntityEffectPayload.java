package io.github.gaming32.rewindwatch.network;

import io.github.gaming32.rewindwatch.EntityEffect;
import io.github.gaming32.rewindwatch.ResourceLocations;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ClientboundEntityEffectPayload(int entity, EntityEffect effect) implements CustomPacketPayload {
    public static final Type<ClientboundEntityEffectPayload> TYPE = new Type<>(ResourceLocations.rewindWatch("entity_effect"));
    public static final StreamCodec<FriendlyByteBuf, ClientboundEntityEffectPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, ClientboundEntityEffectPayload::entity,
        EntityEffect.STREAM_CODEC, ClientboundEntityEffectPayload::effect,
        ClientboundEntityEffectPayload::new
    );

    @Override
    public @NotNull Type<ClientboundEntityEffectPayload> type() {
        return TYPE;
    }
}
