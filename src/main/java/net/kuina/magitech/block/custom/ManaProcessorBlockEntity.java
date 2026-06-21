package net.kuina.magitech.block.custom;

import net.kuina.magitech.block.base.ManaContainerBlockEntity;
import net.kuina.magitech.block.MagitechBlockEntities;
import net.kuina.magitech.block.MagitechBlocks;
import net.kuina.magitech.energy.IManaStorage;
import net.kuina.magitech.menu.ManaProcessorMenu;
import net.kuina.magitech.recipe.ManaProcessorRecipe;
import net.kuina.magitech.recipe.ManaProcessorRecipeInput;
import net.kuina.magitech.recipe.ModRecipes;
import net.kuina.magitech.util.ManaHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ManaProcessorBlockEntity extends ManaContainerBlockEntity implements MenuProvider {

    /* ---------- 調整用の定数 ---------- */
    public static final long MANA_CAPACITY = 50_000L;
    public static final long MANA_PER_TICK = 20L;
    public static final int MAX_PROGRESS = 100;

    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 1;
    public static final int SLOT_CATALYST = 2;

    /* ---------- 在庫（アイテム） ---------- */
    private final ItemStackHandler inventory = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (slot == SLOT_INPUT || slot == SLOT_CATALYST) {
                currentRecipe = null;
            }
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return slot == SLOT_INPUT || slot == SLOT_CATALYST;
        }
    };

    /* ---------- マナ ---------- */

    /* ---------- 加工の進捗 ---------- */
    private int progress = 0;
    @Nullable
    private ManaProcessorRecipe currentRecipe = null;

    /* ---------- アップグレード（隣接ブロック） ---------- */
    private int effectiveMaxProgress = MAX_PROGRESS;
    private long effectiveManaPerTick = MANA_PER_TICK;
    private int upgradeCooldown = 0;

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> effectiveMaxProgress;
                case 2 -> (int) mana.getManaStored();
                case 3 -> (int) mana.getMaxMana();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                progress = value;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public ManaProcessorBlockEntity(BlockPos pos, BlockState state) {
        super(MagitechBlockEntities.MANA_PROCESSOR_BLOCK_ENTITY.get(), pos, state, MANA_CAPACITY);
        this.manaPort = createReceiveOnlyPort();
    }

    /* ---------- 外部からアクセスするための窓口 ---------- */

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public IManaStorage getManaPort() {
        return manaPort;
    }

    public ContainerData getData() {
        return data;
    }

    /* ---------- 毎tick処理（サーバー側） ---------- */

    public static void tick(ServerLevel level, BlockPos pos, BlockState state, ManaProcessorBlockEntity self) {
        // ① 隣接ブロック（タンクなど）からマナを自動補給
        ManaHelper.pullFromNeighbors(level, pos, self.manaPort, 200L);

        // ② アップグレードスキャン（20tick毎）
        if (--self.upgradeCooldown <= 0) {
            self.upgradeCooldown = 20;
            self.scanUpgrades(level, pos);
        }

        // ③ レシピ解決
        if (self.currentRecipe == null) {
            ItemStack input = self.inventory.getStackInSlot(SLOT_INPUT);
            if (!input.isEmpty()) {
                Optional<RecipeHolder<ManaProcessorRecipe>> holder = level.getRecipeManager()
                        .getRecipeFor(ModRecipes.MANA_PROCESSING_TYPE.get(),
                                new ManaProcessorRecipeInput(input), level);
                self.currentRecipe = holder.map(RecipeHolder::value).orElse(null);
            }
        }

        // ④ 加工処理
        boolean hasMana = self.mana.getManaStored() >= self.effectiveManaPerTick;
        boolean processing = self.canProcess() && hasMana;
        
        if (state.getValue(ManaProcessorBlock.LIT) != processing) {
            level.setBlock(pos, state.setValue(ManaProcessorBlock.LIT, processing), 3);
        }

        if (processing) {
            self.mana.extractMana(self.effectiveManaPerTick, false);
            self.progress++;
            if (self.progress >= self.effectiveMaxProgress) {
                self.craft();
                self.progress = 0;
            }
            self.setChanged();
        } else if (!self.canProcess() && self.progress != 0) {
            self.progress = 0;
            self.setChanged();
        }
    }

    private void scanUpgrades(ServerLevel level, BlockPos pos) {
        int speedPct = 0;
        long manaReduction = 0;
        for (Direction dir : Direction.values()) {
            Block neighbor = level.getBlockState(pos.relative(dir)).getBlock();
            if (neighbor == MagitechBlocks.MANA_TANK.get()) {
                speedPct += 25;
            } else if (neighbor == MagitechBlocks.MANA.get()) {
                manaReduction += 5;
            } else if (neighbor == MagitechBlocks.ACTIVE_MAGITECH_BLOCK.get()) {
                speedPct += 25;
            }
        }
        this.effectiveMaxProgress = Math.max(MAX_PROGRESS - speedPct, 1);
        this.effectiveManaPerTick = Math.max(MANA_PER_TICK - manaReduction, 1);
    }

    private boolean canProcess() {
        if (currentRecipe == null) return false;

        ItemStack input = inventory.getStackInSlot(SLOT_INPUT);
        if (input.isEmpty()) return false;

        ItemStack output = inventory.getStackInSlot(SLOT_OUTPUT);
        ItemStack result = currentRecipe.getResult();

        if (output.isEmpty()) return true;

        return ItemStack.isSameItemSameComponents(output, result)
                && output.getCount() + result.getCount() <= output.getMaxStackSize();
    }

    private void craft() {
        if (currentRecipe == null) return;

        ItemStack result = currentRecipe.getResult().copy();
        currentRecipe = null;

        inventory.extractItem(SLOT_INPUT, 1, false);

        ItemStack output = inventory.getStackInSlot(SLOT_OUTPUT);
        if (output.isEmpty()) {
            inventory.setStackInSlot(SLOT_OUTPUT, result);
        } else {
            output.grow(result.getCount());
        }
    }

    /* ---------- GUI（MenuProvider） ---------- */

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.magitech.mana_processor");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ManaProcessorMenu(containerId, playerInventory, this, data);
    }

    /* ---------- NBT 保存／読み込み ---------- */

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.putInt("Progress", progress);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) {
            inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        }
        progress = tag.getInt("Progress");
    }
}
