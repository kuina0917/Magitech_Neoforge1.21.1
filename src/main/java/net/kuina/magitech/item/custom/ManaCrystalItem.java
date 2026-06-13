package net.kuina.magitech.item.custom;

import net.kuina.magitech.energy.PlayerEtherEnergy;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;

public class ManaCrystalItem extends Item {

    public ManaCrystalItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            // 上限を増やす処理
            PlayerEtherEnergy.increaseCapacity(player, 50);

            // 増加後の新しい上限を取得（これが必要！）
            long newCapacity = PlayerEtherEnergy.get(player).getCapacity();

            // メッセージを表示
            player.displayClientMessage(
                    Component.translatable("msg.magitech.increased_capacity", newCapacity),
                    true
            );
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }}