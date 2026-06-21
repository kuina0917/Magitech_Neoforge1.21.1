package net.kuina.magitech.item.custom;

import net.kuina.magitech.component.MagitechDataComponents;
import net.kuina.magitech.energy.IManaStorage;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * 携帯マナタンク。
 * プレイヤーが持ち歩けるマナの貯蔵アイテム。
 *
 * <p>マナ量は {@link MagitechDataComponents#MANA} コンポーネントに保存する。
 * 内部の {@link ManaStorage} を Capability として公開することで、
 * 装置やタンクから {@link net.kuina.magitech.energy.ManaTransfer} 経由でマナを注入できる。</p>
 * - テクスチャは流用プレースホルダです（タンク=active_magitech_block、携帯=mana_crystal）。専用テクスチャを用意したら models/block/mana_tank.json・models/item/portable_mana_tank.json のパスを差し替えてください。
 *   - 今回**見送った「装置間の搬出」**は、ManaTankBlock に ticker の余地を残してあります。隣接ブロックの MANA_BLOCK Capability を取得して ManaTransfer.move で push する形で次段階に追加できます。
 *   - 今後マナを扱うブロック/アイテムを足すときは、ManaCapabilities.onRegisterCapabilities に登録を1行加えるだけで同じ仕組みに繋がります。
 *
 *   再ビルドで反映されます。マナ生成機本体に着手する際は、出力スロットをこの IManaStorage で公開すれば、そのままタンク・携帯・搬出に繋がります。
 *
 */

public class PortableManaTankItem extends Item {

    /** 携帯タンクの最大容量。 */
    public static final long CAPACITY = 50_000L;

    public PortableManaTankItem(Properties properties) {
        super(properties);
    }

    /** 指定スタックに対するマナ貯蔵窓口を返す（Capability 登録から呼ばれる）。 */
    public static IManaStorage createStorage(ItemStack stack) {
        return new ManaStorage(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents,
            TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        long stored = stack.getOrDefault(MagitechDataComponents.MANA.get(), 0L);
        tooltipComponents.add(Component.translatable("tooltip.magitech.mana_stored", stored, CAPACITY)
                .withStyle(ChatFormatting.AQUA));
    }

    /**
     * ItemStack のコンポーネントを読み書きするマナ貯蔵。
     * 値はアイテムに直接保存されるので、別途 NBT 管理は不要。
     */
    public static class ManaStorage implements IManaStorage {

        private final ItemStack stack;

        public ManaStorage(ItemStack stack) {
            this.stack = stack;
        }

        private long get() {
            return stack.getOrDefault(MagitechDataComponents.MANA.get(), 0L);
        }

        private void set(long value) {
            stack.set(MagitechDataComponents.MANA.get(), Math.max(0, Math.min(value, CAPACITY)));
        }

        @Override
        public long getManaStored() {
            return get();
        }

        @Override
        public long getMaxMana() {
            return CAPACITY;
        }

        @Override
        public long insertMana(long amount, boolean simulate) {
            if (amount <= 0) {
                return 0;
            }
            long accepted = Math.min(amount, CAPACITY - get());
            if (accepted <= 0) {
                return 0;
            }
            if (!simulate) {
                set(get() + accepted);
            }
            return accepted;
        }

        @Override
        public long extractMana(long amount, boolean simulate) {
            if (amount <= 0) {
                return 0;
            }
            long extracted = Math.min(amount, get());
            if (extracted <= 0) {
                return 0;
            }
            if (!simulate) {
                set(get() - extracted);
            }
            return extracted;
        }
    }
}
