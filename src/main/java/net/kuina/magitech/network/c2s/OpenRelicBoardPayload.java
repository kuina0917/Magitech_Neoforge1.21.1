package net.kuina.magitech.network.c2s;

import net.kuina.magitech.Magitech;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class OpenRelicBoardPayload implements CustomPacketPayload {
    public static final Type<OpenRelicBoardPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(Magitech.MOD_ID, "open_relic_board")
    );

    public static final OpenRelicBoardPayload INSTANCE = new OpenRelicBoardPayload();

    public static final StreamCodec<FriendlyByteBuf, OpenRelicBoardPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private OpenRelicBoardPayload() {}

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
