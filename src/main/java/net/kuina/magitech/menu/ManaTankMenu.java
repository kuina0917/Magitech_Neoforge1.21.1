package net.kuina.magitech.menu;

import net.kuina.magitech.block.MagitechBlocks;
import net.kuina.magitech.block.custom.ManaTankBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

public class ManaTankMenu extends AbstractContainerMenu {

    private static final int TANK_SLOT_COUNT = 1;

    public final ManaTankBlockEntity blockEntity;
    private final ContainerLevelAccess access;
    private final ContainerData data;

    public ManaTankMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, playerInventory,
                getBlockEntity(playerInventory, extraData.readBlockPos()),
                new SimpleContainerData(2));
        ManaTankBlockEntity be = this.blockEntity;
        if (be != null) {
            for (int i = 0; i < be.getData().getCount(); i++) {
                this.data.set(i, be.getData().get(i));
            }
        }
    }

    public ManaTankMenu(int containerId, Inventory playerInventory, ManaTankBlockEntity blockEntity,
            ContainerData data) {
        super(MagitechMenus.MANA_TANK_MENU.get(), containerId);
        this.blockEntity = blockEntity;
        this.data = data;
        this.access = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        addSlot(new SlotItemHandler(blockEntity.getInventory(), 0, 80, 35));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }

        addDataSlots(data);
    }

    private static ManaTankBlockEntity getBlockEntity(Inventory playerInventory, BlockPos pos) {
        if (playerInventory.player.level().getBlockEntity(pos) instanceof ManaTankBlockEntity be) {
            return be;
        }
        throw new IllegalStateException("Mana Tank block entity not found at: " + pos);
    }

    public int getManaStored() { return data.get(0); }
    public int getMaxMana() { return data.get(1); }
    public int getManaScaled(int barHeight) {
        int stored = data.get(0);
        int max = data.get(1);
        return (max != 0) ? stored * barHeight / max : 0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack returned = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) return returned;

        ItemStack stack = slot.getItem();
        returned = stack.copy();

        int playerStart = TANK_SLOT_COUNT;
        int playerEnd = TANK_SLOT_COUNT + 36;

        if (index < TANK_SLOT_COUNT) {
            if (!moveItemStackTo(stack, playerStart, playerEnd, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!moveItemStackTo(stack, 0, TANK_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return returned;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, MagitechBlocks.MANA_TANK.get());
    }
}
