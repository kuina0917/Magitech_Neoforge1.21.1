package net.kuina.magitech.network;

import net.kuina.magitech.client.screen.RelicBoardScreen;
import net.kuina.magitech.component.MagitechDataComponents;
import net.kuina.magitech.item.custom.RelicItem;
import net.kuina.magitech.network.c2s.OpenRelicBoardPayload;
import net.kuina.magitech.network.c2s.PlaceRelicPayload;
import net.kuina.magitech.network.c2s.RemoveRelicPayload;
import net.kuina.magitech.network.s2c.SyncRelicBoardPayload;
import net.kuina.magitech.relic.*;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class RelicBoardPayloadHandler {

    public static void handleSyncBoard(final SyncRelicBoardPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            RelicBoard board = RelicBoard.loadFromNBT(payload.boardTag());
            Minecraft.getInstance().setScreen(new RelicBoardScreen(board));
        });
    }

    public static void handlePlaceRelic(final PlaceRelicPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (!(player instanceof ServerPlayer serverPlayer)) return;

            RelicBoard board = PlayerRelicBoard.get(player);
            if (board == null) return;

            ItemStack stack = player.getInventory().getItem(payload.inventorySlot());
            if (stack.isEmpty()) return;

            RelicData data = stack.get(MagitechDataComponents.RELIC_DATA.get());
            if (data == null) return;

            PlacedRelic placed = new PlacedRelic(data, payload.posX(), payload.posY(), payload.rotation());
            if (!board.place(placed)) return;

            stack.shrink(1);
            PlayerRelicBoard.markDirty(player);
            RelicEffectsApplicator.apply(player, board.getTotalEffects());
            sendSync(serverPlayer, board);
        });
    }

    public static void handleRemoveRelic(final RemoveRelicPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (!(player instanceof ServerPlayer serverPlayer)) return;

            RelicBoard board = PlayerRelicBoard.get(player);
            if (board == null) return;

            PlacedRelic relic = board.getRelicAt(payload.posX(), payload.posY());
            if (relic == null) return;

            if (!board.remove(payload.posX(), payload.posY())) return;

            ItemStack stack = RelicItem.create(relic.data());
            if (!player.getInventory().add(stack)) {
                player.drop(stack, false);
            }

            PlayerRelicBoard.markDirty(player);
            RelicEffectsApplicator.apply(player, board.getTotalEffects());
            sendSync(serverPlayer, board);
        });
    }

    public static void handleOpenBoard(final OpenRelicBoardPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (!(player instanceof ServerPlayer serverPlayer)) return;

            RelicBoard board = PlayerRelicBoard.get(player);
            if (board == null) return;

            PacketDistributor.sendToPlayer(serverPlayer, new SyncRelicBoardPayload(board.saveToNBT()));
        });
    }

    private static void sendSync(ServerPlayer player, RelicBoard board) {
        PacketDistributor.sendToPlayer(player, new SyncRelicBoardPayload(board.saveToNBT()));
    }
}
