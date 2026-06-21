package net.kuina.magitech.network.s2c;

import net.kuina.magitech.Magitech;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SyncRelicBoardPayload(CompoundTag boardTag) implements CustomPacketPayload {
    public static final Type<SyncRelicBoardPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(Magitech.MOD_ID, "sync_relic_board")
    );

    public static final StreamCodec<FriendlyByteBuf, SyncRelicBoardPayload> STREAM_CODEC = StreamCodec.of(
        (buf, payload) -> buf.writeNbt(payload.boardTag),
        buf -> new SyncRelicBoardPayload(buf.readNbt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
