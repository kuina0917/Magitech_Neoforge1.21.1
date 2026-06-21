package net.kuina.magitech.item.custom;

import net.kuina.magitech.component.MagitechDataComponents;
import net.kuina.magitech.item.MagitechItems;
import net.kuina.magitech.relic.RelicData;
import net.kuina.magitech.relic.PlayerRelicBoard;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;

public class RelicItem extends Item {

    public RelicItem(Properties properties) {
        super(properties);
    }

    public static ItemStack create(RelicData data) {
        ItemStack stack = new ItemStack(MagitechItems.RELIC.get());
        stack.set(MagitechDataComponents.RELIC_DATA.get(), data);
        return stack;
    }

    public static RelicData getData(ItemStack stack) {
        return stack.get(MagitechDataComponents.RELIC_DATA.get());
    }

    @Override
    public Component getName(ItemStack stack) {
        RelicData data = getData(stack);
        if (data != null) {
            return Component.translatable(data.getDisplayName());
        }
        return super.getName(stack);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            RelicData data = getData(stack);
            if (data != null) {
                player.sendSystemMessage(Component.translatable("message.magitech.relic.info", data.getDisplayName()));
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        RelicData data = getData(stack);
        if (data != null) {
            tooltip.add(Component.translatable("tooltip.magitech.relic.rarity",
                Component.translatable("rarity.magitech." + data.rarity().getSerializedName())));

            tooltip.add(Component.translatable("tooltip.magitech.relic.shape",
                data.shape().width() + "x" + data.shape().height() + " (" + data.shape().getCellCount() + ")"));

            tooltip.add(Component.translatable("tooltip.magitech.relic.element",
                Component.translatable("element.magitech." + data.element().getSerializedName())));

            if (!data.mainEffects().isEmpty()) {
                tooltip.add(Component.translatable("tooltip.magitech.relic.main_effects"));
                for (var entry : data.mainEffects().entrySet()) {
                    tooltip.add(Component.literal("  " + formatEffect(entry)));
                }
            }

            if (!data.subEffects().isEmpty()) {
                tooltip.add(Component.translatable("tooltip.magitech.relic.sub_effects"));
                for (var entry : data.subEffects().entrySet()) {
                    tooltip.add(Component.literal("  " + formatEffect(entry)));
                }
            }
        }
    }

    private static String formatEffect(Map.Entry<net.kuina.magitech.relic.RelicEffectType, Float> entry) {
        String key = "effect.magitech." + entry.getKey().getSerializedName();
        String displayName = net.minecraft.network.chat.Component.translatable(key).getString();
        String value = entry.getValue() >= 1.0f
            ? "+" + String.format("%.0f", entry.getValue())
            : "+" + String.format("%.0f%%", entry.getValue() * 100);
        return value + " " + displayName;
    }
}
