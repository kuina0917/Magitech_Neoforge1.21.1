package net.kuina.magitech.item.custom;

import net.kuina.magitech.fluidtype.MagitechFluidTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * マナつるはしの共通ベースクラス。
 *
 * <p>マナ液体に触れている間、一定間隔で耐久値を回復する。
 * 持ち主のインベントリ内にあるとき（{@link #inventoryTick}）だけでなく、
 * 地面やマナ液体に投げ入れられたアイテムの状態（{@link #onEntityItemUpdate}）でも回復する。
 * 今後マナつるはしを追加するときは、このクラスを使い回せばよい。</p>
 *
 * <ul>
 *   <li>標準の回復速度でよい場合 … {@code new ManaPickaxeItem(tier, props)}</li>
 *   <li>回復速度を変えたい場合 … {@code new ManaPickaxeItem(tier, props, intervalTicks, amount)}</li>
 *   <li>独自の挙動を足したい場合 … このクラスを継承する</li>
 * </ul>
 */
public class ManaPickaxeItem extends PickaxeItem {

    /** 標準の回復間隔（tick）。20tick = 1秒。 */
    public static final int DEFAULT_REPAIR_INTERVAL_TICKS = 20;

    /** 標準の 1 回あたりの回復量。 */
    public static final int DEFAULT_REPAIR_AMOUNT = 1;

    /** 耐久を回復するまでの間隔（tick）。 */
    private final int repairIntervalTicks;

    /** 1 回の回復で戻す耐久値。 */
    private final int repairAmount;

    /** 標準の回復速度（1秒ごとに1回復）で作成する。 */
    public ManaPickaxeItem(Tier tier, Properties properties) {
        this(tier, properties, DEFAULT_REPAIR_INTERVAL_TICKS, DEFAULT_REPAIR_AMOUNT);
    }

    /**
     * 回復速度を指定して作成する。
     *
     * @param repairIntervalTicks 回復間隔（tick）。1 以上を指定すること
     * @param repairAmount        1 回あたりの回復量
     */
    public ManaPickaxeItem(Tier tier, Properties properties, int repairIntervalTicks, int repairAmount) {
        super(tier, properties);
        this.repairIntervalTicks = Math.max(1, repairIntervalTicks);
        this.repairAmount = Math.max(1, repairAmount);
    }

    /** カーソルを合わせたときの説明欄に、現在の耐久値を表示する。 */
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents,
            TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        int max = stack.getMaxDamage();
        int remaining = max - stack.getDamageValue(); // 残り耐久 = 最大 - ダメージ
        tooltipComponents.add(Component.translatable("tooltip.magitech.durability", remaining, max)
                .withStyle(ChatFormatting.GRAY));
    }

    /** インベントリ内にあるとき（持ち主が液体に入っている場合）。 */
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        tryRepairInMana(stack, level, entity);
    }

    /** 地面やマナ液体に投げ入れられたアイテムの状態のとき。 */
    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        tryRepairInMana(stack, entity.level(), entity);
        return false; // 通常のアイテム挙動はそのまま続行させる
    }

    /**
     * マナ液体に触れていれば耐久を回復する共通処理。
     *
     * @param fluidCarrier 液体に入っているか判定する対象
     *                     （インベントリ内なら持ち主、ドロップ中ならアイテムエンティティ自身）
     */
    private void tryRepairInMana(ItemStack stack, Level level, Entity fluidCarrier) {
        // 処理はサーバー側だけ、かつ一定間隔ごとに行う
        if (level.isClientSide) {
            return;
        }
        if (level.getGameTime() % repairIntervalTicks != 0) {
            return;
        }

        // すでに耐久が満タンなら回復不要
        if (stack.getDamageValue() <= 0) {
            return;
        }

        // マナ液体に触れているときだけ回復する
        if (isTouchingMana(fluidCarrier)) {
            int repaired = Math.max(0, stack.getDamageValue() - repairAmount);
            stack.setDamageValue(repaired);
        }
    }

    /**
     * 対象がいずれかのマナ系液体に触れているか。
     * 判定対象は {@link magitechfluidtypes#isManaFluidType} に従うため、
     * 今後マナ液体を追加してもこのメソッドを変更する必要はない。
     * サブクラスで条件を変えたい場合はここを上書きする。
     */
    protected boolean isTouchingMana(Entity entity) {
        return entity.isInFluidType((fluidType, height) -> MagitechFluidTypes.isManaFluidType(fluidType));
    }
}
