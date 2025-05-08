package net.kuina.magitech.item.custom;

import net.kuina.magitech.energy.EtherEnergyStorage;
import net.kuina.magitech.entity.custom.ZoltrakProjectile;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.kuina.magitech.component.magitechcomponents.ZOLTRAK_MODE;

public class RodItem extends Item {

    private static final Vec3[] BACK_FIRE_OFFSETS = new Vec3[]{
            new Vec3(-1.4, 2.0, 0.6),
            new Vec3(-1.4, 1.8, 0.0),
            new Vec3(-1.5, 1.6, -0.6),
            new Vec3(1.4, 2.0, 0.6),
            new Vec3(1.4, 1.8, 0.0),
            new Vec3(1.5, 1.6, -0.6)
    };

    private static final int ENERGY_COST_SINGLE = 10;
    private static final int ENERGY_COST_RAPID = 10;

    // プレイヤーごとのエネルギー管理
    private static final Map<UUID, EtherEnergyStorage> energyMap = new HashMap<>();

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
        int energyCost = isRapid ? ENERGY_COST_RAPID : ENERGY_COST_SINGLE;

        if (!level.isClientSide) {
            EtherEnergyStorage energyStorage = getEnergyStorage(player);
            if (energyStorage.hasEnergy(energyCost)) {
                energyStorage.removeEnergy(energyCost);
            } else {
                player.displayClientMessage(Component.literal("エネルギーが足りません！").withStyle(ChatFormatting.RED), true);
                return InteractionResultHolder.fail(stack);
            }
        }

        if (!isRapid) {
            ZoltrakProjectile projectile = new ZoltrakProjectile(level, player);
            projectile.setPos(player.getX(), player.getY() + 1.0, player.getZ());
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 0.0F);
            level.addFreshEntity(projectile);
            player.getCooldowns().addCooldown(this, 10);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 25.0F, 4.0F + level.random.nextFloat() * 0.2F);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void onUseTick(Level level, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!(user instanceof Player player) || level.isClientSide) return;
        if (!stack.getOrDefault(ZOLTRAK_MODE.get(), false)) return;
        if (level.getGameTime() % 4 != 0) return;

        EtherEnergyStorage energyStorage = getEnergyStorage(player);
        if (!energyStorage.hasEnergy(ENERGY_COST_RAPID)) {
            player.displayClientMessage(Component.literal("エネルギーが足りません！").withStyle(ChatFormatting.RED), true);
            return;
        }
        energyStorage.removeEnergy(ENERGY_COST_RAPID);

        Vec3 offset = BACK_FIRE_OFFSETS[level.getRandom().nextInt(BACK_FIRE_OFFSETS.length)];
        Vec3 rotatedOffset = offset.yRot((float) Math.toRadians(-player.getYRot()));
        Vec3 spawnPos = player.position().add(rotatedOffset);

        ZoltrakProjectile projectile = new ZoltrakProjectile(level, player);
        projectile.setPos(spawnPos);
        projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 0.3F);
        level.addFreshEntity(projectile);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 25.0F, 4.0F + level.random.nextFloat() * 0.2F);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {}

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    private EtherEnergyStorage getEnergyStorage(Player player) {
        return energyMap.computeIfAbsent(player.getUUID(), id -> new EtherEnergyStorage(100));
    }
}