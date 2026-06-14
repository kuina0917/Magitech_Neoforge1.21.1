package net.kuina.magitech.network;

import net.kuina.magitech.magitech;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * サーバーからクライアントへ、スキャンで見つかったマナブロックのリストを送るパケット。
 */
public record SyncManaTargetsPayload(List<BlockPos> foundPositions, List<Boolean> enabledStates) implements CustomPacketPayload {
    public static final Type<SyncManaTargetsPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(magitech.MOD_ID, "sync_mana_targets"));

    public static final StreamCodec<FriendlyByteBuf, SyncManaTargetsPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeCollection(payload.foundPositions, (b, pos) -> b.writeBlockPos(pos));
                buf.writeCollection(payload.enabledStates, (b, state) -> b.writeBoolean(state));
            },
            buf -> {
                List<BlockPos> pos = buf.readCollection(ArrayList::new, b -> b.readBlockPos());
                List<Boolean> states = buf.readCollection(ArrayList::new, b -> b.readBoolean());
                return new SyncManaTargetsPayload(pos, states);
            }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
