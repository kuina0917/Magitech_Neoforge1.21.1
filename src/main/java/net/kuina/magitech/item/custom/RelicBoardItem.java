package net.kuina.magitech.item.custom;

import net.kuina.magitech.network.s2c.SyncRelicBoardPayload;
import net.kuina.magitech.relic.PlayerRelicBoard;
import net.kuina.magitech.relic.RelicBoard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

public class RelicBoardItem extends Item {

    public RelicBoardItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            RelicBoard board = PlayerRelicBoard.get(player);
            PacketDistributor.sendToPlayer(serverPlayer, new SyncRelicBoardPayload(board.saveToNBT()));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
