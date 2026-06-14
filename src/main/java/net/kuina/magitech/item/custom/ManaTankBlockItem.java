package net.kuina.magitech.item.custom;

import net.kuina.magitech.block.custom.ManaTankBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;

import java.util.List;

/**
 * マナタンクのブロックアイテム。
 * ツールチップに、そのタンクが保持しているマナ量を表示する。
 *
 * <p>マナ量は {@link BlockItem#getBlockEntityData} の NBT に保存される。</p>
 */
public class ManaTankBlockItem extends BlockItem {

    public ManaTankBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents,
            TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        CustomData blockEntityData = stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);
        CompoundTag beTag = blockEntityData.copyTag();
        long stored = beTag.getLong("Mana");
        tooltipComponents.add(Component.translatable("tooltip.magitech.mana_stored", stored, ManaTankBlockEntity.CAPACITY)
                .withStyle(ChatFormatting.AQUA));
    }
}
