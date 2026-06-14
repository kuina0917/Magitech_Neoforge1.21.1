package net.kuina.magitech.menu;

import net.kuina.magitech.block.custom.ManaExtractorCoreBlockEntity;
import net.kuina.magitech.block.magitechblocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ManaExtractorMenu extends AbstractContainerMenu {
    private final ManaExtractorCoreBlockEntity blockEntity;
    private final ContainerLevelAccess levelAccess;

    public ManaExtractorMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        
        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            java.util.List<BlockPos> found = blockEntity.getFoundAddresses();
            java.util.List<Boolean> states = new java.util.ArrayList<>();
            for (BlockPos p : found) {
                states.add(blockEntity.isTargetEnabled(p));
            }
            
            net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(serverPlayer, 
                new net.kuina.magitech.network.SyncManaTargetsPayload(found, states));
        }
    }

    private final Player player;
    public ManaExtractorMenu(int containerId, Inventory inv, BlockEntity entity) {
        super(magitechmenus.MANA_EXTRACTOR_MENU.get(), containerId);
        this.blockEntity = (ManaExtractorCoreBlockEntity) entity;
        this.levelAccess = ContainerLevelAccess.create(entity.getLevel(), entity.getBlockPos());
        this.player = inv.player;
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        // ボタンIDをスキャンで見つかったアドレスのインデックスとして扱う
        java.util.List<BlockPos> found = blockEntity.getFoundAddresses();
        if (buttonId >= 0 && buttonId < found.size()) {
            blockEntity.toggleTarget(found.get(buttonId));
            return true;
        }
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.levelAccess, player, net.kuina.magitech.block.magitechblocks.MANA_EXTRACTOR_CORE.get());
    }

    public ManaExtractorCoreBlockEntity getBlockEntity() {
        return blockEntity;
    }
}
