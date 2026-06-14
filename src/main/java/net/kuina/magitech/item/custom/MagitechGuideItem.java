package net.kuina.magitech.item.custom;

import net.kuina.magitech.client.screen.ResearchScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 魔導書。右クリックで研究ツリー画面を開く。
 */
public class MagitechGuideItem extends Item {
    public ResearchScreen screen;
    public MagitechGuideItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) {
            openResearchScreen();
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }

    private void openResearchScreen() {
        Minecraft.getInstance().setScreen(new ResearchScreen());
    }
}
