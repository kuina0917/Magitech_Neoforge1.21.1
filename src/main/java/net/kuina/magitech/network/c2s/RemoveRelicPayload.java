package net.kuina.magitech.network.c2s;

import net.kuina.magitech.Magitech;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RemoveRelicPayload(int posX, int posY) implements CustomPacketPayload {
    public static final Type<RemoveRelicPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(Magitech.MOD_ID, "remove_relic")
    );

    public static final StreamCodec<FriendlyByteBuf, RemoveRelicPayload> STREAM_CODEC = StreamCodec.of(
        (buf, payload) -> {
            buf.writeVarInt(payload.posX);
            buf.writeVarInt(payload.posY);
        },
        buf -> new RemoveRelicPayload(
            buf.readVarInt(),
            buf.readVarInt()
        )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
