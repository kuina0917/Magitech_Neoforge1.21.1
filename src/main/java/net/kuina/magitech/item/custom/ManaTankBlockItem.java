package net.kuina.magitech.item.custom;

import net.kuina.magitech.block.custom.ManaTankBlockEntity;
import net.kuina.magitech.component.magitechcomponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.List;

/**
 * マナタンクのブロックアイテム。
 * ツールチップに、そのタンクが保持しているマナ量を表示する。
 *
 * <p>マナ量は {@link magitechcomponents#MANA} コンポーネントに保存される。
 * タンクを設置／破壊すると {@link net.kuina.magitech.block.custom.ManaTankBlock} が
 * このコンポーネントと内部ストレージの間で値を引き継ぐ。</p>
 */
public class ManaTankBlockItem extends BlockItem {

    public ManaTankBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents,
            TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        long stored = stack.getOrDefault(magitechcomponents.MANA.get(), 0L);
        tooltipComponents.add(Component.translatable("tooltip.magitech.mana_stored", stored, ManaTankBlockEntity.CAPACITY)
                .withStyle(ChatFormatting.AQUA));
    }
}
