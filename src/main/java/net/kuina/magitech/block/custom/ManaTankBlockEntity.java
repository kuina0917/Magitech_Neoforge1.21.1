package net.kuina.magitech.block.custom;

import net.kuina.magitech.block.base.ManaContainerBlockEntity;
import net.kuina.magitech.block.MagitechBlockEntities;
import net.kuina.magitech.capability.ManaCapabilities;
import net.kuina.magitech.energy.IEtherEnergyReceiver;
import net.kuina.magitech.energy.IManaStorage;
import net.kuina.magitech.energy.ManaTransfer;
import net.kuina.magitech.energy.custom.EtherEnergyStorage;
import net.kuina.magitech.menu.ManaTankMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class ManaTankBlockEntity extends ManaContainerBlockEntity implements IEtherEnergyReceiver, MenuProvider {

    public static final long CAPACITY = 100_000L;
    private static final long AUTO_CHARGE_PER_TICK = 200L;

    private final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.getCapability(ManaCapabilities.MANA_ITEM) != null;
        }
    };

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> (int) mana.getManaStored();
                case 1 -> (int) mana.getMaxMana();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public ManaTankBlockEntity(BlockPos pos, BlockState state) {
        super(MagitechBlockEntities.MANA_TANK_BLOCK_ENTITY.get(), pos, state, CAPACITY);
    }

    public IManaStorage getManaStorage() {
        return manaPort;
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public ContainerData getData() {
        return data;
    }

    @Override
    public EtherEnergyStorage getEtherStorage() {
        return mana;
    }

    // ── MenuProvider ──

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.magitech.mana_tank");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ManaTankMenu(containerId, playerInventory, this, data);
    }

    // ── Tick ──

    public static void tick(Level level, BlockPos pos, BlockState state, ManaTankBlockEntity self) {
        if (level.isClientSide) return;

        ItemStack stack = self.inventory.getStackInSlot(0);
        if (!stack.isEmpty()) {
            IManaStorage portable = stack.getCapability(ManaCapabilities.MANA_ITEM);
            if (portable != null) {
                ManaTransfer.move(self.manaPort, portable, AUTO_CHARGE_PER_TICK);
            }
        }
    }

    // ── NBT ──

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) {
            inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        }
    }
}
