package net.kuina.magitech.item.custom;

import net.kuina.magitech.entity.custom.MagicCircle;
import net.kuina.magitech.entity.custom.ZoltrakProjectile;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import static net.kuina.magitech.component.magitechcomponents.ZOLTRAK_MODE;

public class RodItem extends Item {

    public static final int FIRE_INTERVAL = 5;

    public RodItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isCrouching()) {
            if (!level.isClientSide) {
                boolean current = stack.getOrDefault(ZOLTRAK_MODE.get(), false);
                stack.set(ZOLTRAK_MODE.get(), !current);
                String modeName = !current ? "連射モード" : "単発モード";
                player.displayClientMessage(Component.literal("モード切替: " + modeName).withStyle(ChatFormatting.AQUA), true);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        boolean isRapid = stack.getOrDefault(ZOLTRAK_MODE.get(), false);

        if (isRapid) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        } else {
            if (!level.isClientSide) {
                Vec3[] offsets = {
                        new Vec3(-0.5, 1.5, -0.5),
                        new Vec3(0.5, 1.5, -0.5),
                        new Vec3(-0.5, 1.0, -0.5),
                        new Vec3(0.5, 1.0, -0.5)
                };
                Vec3 offset = offsets[level.getRandom().nextInt(offsets.length)];
                Vec3 worldPos = player.position().add(offset.yRot((float) Math.toRadians(-player.getYRot())));

                MagicCircle circle = new MagicCircle(level, worldPos.x, worldPos.y, worldPos.z, player);
                level.addFreshEntity(circle);
                player.getCooldowns().addCooldown(this, 10);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }
    }

    @Override
    public void onUseTick(Level level, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!(user instanceof Player player) || level.isClientSide) return;

        boolean isRapid = stack.getOrDefault(ZOLTRAK_MODE.get(), false);
        if (!isRapid) return;

        int elapsed = getUseDuration(stack, user) - remainingUseTicks;
        if (elapsed % FIRE_INTERVAL == 0) {
            ZoltrakProjectile projectile = new ZoltrakProjectile(level, player);
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(projectile);
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }
}