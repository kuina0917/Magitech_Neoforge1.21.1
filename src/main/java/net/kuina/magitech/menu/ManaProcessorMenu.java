package net.kuina.magitech.menu;

import net.kuina.magitech.block.custom.ManaProcessorBlockEntity;
import net.kuina.magitech.block.MagitechBlocks;
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

/**
 * マナ加工機の GUI（サーバー・クライアント共通のロジック）。
 *
 * <p>スロット構成：0=入力、1=出力、2=触媒、3〜38=プレイヤーの持ち物。
 * progress と mana は {@link ContainerData} 経由でクライアントへ同期される。</p>
 */
public class ManaProcessorMenu extends AbstractContainerMenu {

    private static final int MACHINE_SLOT_COUNT = 3;

    public final ManaProcessorBlockEntity blockEntity;
    private final ContainerLevelAccess access;
    private final ContainerData data;

    /** クライアント側：サーバーから送られた BlockPos を読んで BE を取得する。 */
    public ManaProcessorMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, playerInventory,
                getBlockEntity(playerInventory, extraData.readBlockPos()),
                new SimpleContainerData(4));
        // クライアント側：BEのContainerDataでSimpleContainerDataを初期化（初回同期待ち回避）
        ManaProcessorBlockEntity be = this.blockEntity;
        if (be != null) {
            ContainerData src = be.getData();
            for (int i = 0; i < src.getCount(); i++) {
                this.data.set(i, src.get(i));
            }
        }
    }

    /** サーバー側：実際の BE と ContainerData を受け取る。 */
    public ManaProcessorMenu(int containerId, Inventory playerInventory, ManaProcessorBlockEntity blockEntity,
            ContainerData data) {
        super(MagitechMenus.MANA_PROCESSOR_MENU.get(), containerId);
        this.blockEntity = blockEntity;
        this.data = data;
        this.access = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        // 機械のスロット
        // 入力スロット
        addSlot(new SlotItemHandler(blockEntity.getInventory(), ManaProcessorBlockEntity.SLOT_INPUT, 56, 17));
        // カタリストスロット（追加分）
        addSlot(new SlotItemHandler(blockEntity.getInventory(), ManaProcessorBlockEntity.SLOT_CATALYST, 56, 53));
        // 出力スロット
        addSlot(new SlotItemHandler(blockEntity.getInventory(), ManaProcessorBlockEntity.SLOT_OUTPUT, 116, 35));

        // プレイヤーの持ち物（3 段）
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        // ホットバー
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }

        addDataSlots(data);
    }

    private static ManaProcessorBlockEntity getBlockEntity(Inventory playerInventory, BlockPos pos) {
        if (playerInventory.player.level().getBlockEntity(pos) instanceof ManaProcessorBlockEntity be) {
            return be;
        }
        throw new IllegalStateException("マナ加工機のブロックエンティティが見つかりません: " + pos);
    }

    /* ---------- GUI 描画用：進捗・マナの割合 ---------- */

    /** 進捗を 0〜arrowWidth の長さに換算（進捗矢印の描画用）。 */
    public int getProgressScaled(int arrowWidth) {
        int prog = data.get(0);
        int max = data.get(1);
        return (max != 0 && prog != 0) ? prog * arrowWidth / max : 0;
    }

    /** マナ量を 0〜barHeight の長さに換算（マナバーの描画用）。 */
    public int getManaScaled(int barHeight) {
        long stored = getManaStored();
        long max = getMaxMana();
        return (max != 0) ? (int) (stored * barHeight / max) : 0;
    }

    public long getManaStored() {
        return (long) data.get(2);
    }

    public long getMaxMana() {
        return (long) data.get(3);
    }

    /* ---------- Shift クリックでの移動 ---------- */

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack returned = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return returned;
        }
        ItemStack stack = slot.getItem();
        returned = stack.copy();

        int playerStart = MACHINE_SLOT_COUNT;
        int playerEnd = MACHINE_SLOT_COUNT + 36;

        if (index < MACHINE_SLOT_COUNT) {
            // 機械 → プレイヤー
            if (!moveItemStackTo(stack, playerStart, playerEnd, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            // プレイヤー → 機械（入力スロットまたはカタリストスロットへ）
            if (!moveItemStackTo(stack, 0, 1, false) && !moveItemStackTo(stack, 1, 2, false)) {
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
        return stillValid(access, player, MagitechBlocks.MANA_PROCESSOR.get());
    }
}
