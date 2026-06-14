package net.kuina.magitech.block.custom;

import net.kuina.magitech.block.base.ManaContainerBlockEntity;
import net.kuina.magitech.block.magitechblockentities;
import net.kuina.magitech.energy.IManaStorage;
import net.kuina.magitech.item.magitechitems;
import net.kuina.magitech.menu.ManaProcessorMenu;
import net.kuina.magitech.util.ManaHelper;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

/**
 * マナ加工機のブロックエンティティ。
 *
 * <p>鉄インゴット＋貯蔵マナを消費して淡輝マナインゴットを生産する。</p>
 *
 * <ul>
 *   <li>入力スロット(0)：鉄インゴットのみ受け付ける</li>
 *   <li>出力スロット(1)：淡輝マナインゴットを出す（プレイヤーは入れられない）</li>
 *   <li>マナ：内部バッファに貯め、隣接タンクからの自動吸引／携帯タンクからの手動補充で補給</li>
 * </ul>
 */
public class ManaProcessorBlockEntity extends ManaContainerBlockEntity implements MenuProvider {

    /* ---------- 調整用の定数 ---------- */
    public static final long MANA_CAPACITY = 50_000L; // マナバッファ容量
    public static final long MANA_PER_TICK = 20L;     // 加工中に毎tick消費するマナ
    public static final int MAX_PROGRESS = 100;       // 1 回の加工にかかる tick 数
    private static final long PULL_PER_SIDE = 200L;   // 隣接から毎tick引き込む最大量/面

    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 1;

    /* ---------- 在庫（アイテム） ---------- */
    private final ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return slot == SLOT_INPUT && stack.is(Items.IRON_INGOT);
        }
    };

    /* ---------- マナ ---------- */

    /* ---------- 加工の進捗 ---------- */
    private int progress = 0;

    /** GUI へ progress / mana を同期するためのデータ窓口。 */
    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> MAX_PROGRESS;
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
        super(magitechblockentities.MANA_PROCESSOR_BLOCK_ENTITY.get(), pos, state, MANA_CAPACITY);
        this.manaPort = createReceiveOnlyPort();
    }

    /* ---------- 外部からアクセスするための窓口 ---------- */

    public ItemStackHandler getInventory() {
        return inventory;
    }

    /** Capability／隣接補給で使う受け取り窓口。 */
    public IManaStorage getManaPort() {
        return manaPort;
    }

    public ContainerData getData() {
        return data;
    }

    /* ---------- 毎tick処理（サーバー側） ---------- */

    public static void tick(ServerLevel level, BlockPos pos, BlockState state, ManaProcessorBlockEntity self) {
        // ① 隣接ブロック（タンクなど）からマナを自動補給
        ManaHelper.pullFromNeighbors(level, pos, self.manaPort, PULL_PER_SIDE);

        // ② 加工処理
        boolean hasMana = self.mana.getManaStored() >= MANA_PER_TICK;
        if (self.canProcess() && hasMana) {
            self.mana.extractMana(MANA_PER_TICK, false);
            self.progress++;
            if (self.progress >= MAX_PROGRESS) {
                self.craft();
                self.progress = 0;
            }
            self.setChanged();
        } else if (!self.canProcess() && self.progress != 0) {
            // 材料が足りなくなったらだけ進捗リセット（マナ不足は継続可能）
            self.progress = 0;
            self.setChanged();
        }
    }

    /** 加工できる状態か（入力に鉄、出力に空き）。 */
    private boolean canProcess() {
        ItemStack input = inventory.getStackInSlot(SLOT_INPUT);
        if (!input.is(Items.IRON_INGOT) || input.isEmpty()) {
            return false;
        }
        ItemStack output = inventory.getStackInSlot(SLOT_OUTPUT);
        if (output.isEmpty()) {
            return true;
        }
        // 同じアイテムで、まだ積める余地があるか
        return output.is(magitechitems.LOW_MANA_INGOT.get())
                && output.getCount() < output.getMaxStackSize();
    }

    /** 1 回分の加工を実行（鉄を 1 消費し、淡輝マナインゴットを 1 出力）。 */
    private void craft() {
        inventory.extractItem(SLOT_INPUT, 1, false);
        ItemStack output = inventory.getStackInSlot(SLOT_OUTPUT);
        if (output.isEmpty()) {
            inventory.setStackInSlot(SLOT_OUTPUT, new ItemStack(magitechitems.LOW_MANA_INGOT.get(), 1));
        } else {
            output.grow(1);
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
