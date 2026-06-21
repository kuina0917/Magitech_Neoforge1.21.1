package net.kuina.magitech.network.c2s;

import net.kuina.magitech.Magitech;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PlaceRelicPayload(int inventorySlot, int posX, int posY, int rotation) implements CustomPacketPayload {
    public static final Type<PlaceRelicPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(Magitech.MOD_ID, "place_relic")
    );

    public static final StreamCodec<FriendlyByteBuf, PlaceRelicPayload> STREAM_CODEC = StreamCodec.of(
        (buf, payload) -> {
            buf.writeVarInt(payload.inventorySlot);
            buf.writeVarInt(payload.posX);
            buf.writeVarInt(payload.posY);
            buf.writeVarInt(payload.rotation);
        },
        buf -> new PlaceRelicPayload(
            buf.readVarInt(),
            buf.readVarInt(),
            buf.readVarInt(),
            buf.readVarInt()
        )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
