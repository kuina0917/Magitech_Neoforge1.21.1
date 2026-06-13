package net.kuina.magitech.item.custom;

import com.mojang.serialization.Codec;
import net.kuina.magitech.energy.PlayerEtherEnergy;
import net.kuina.magitech.entity.custom.ZoltrakProjectile;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
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
import net.minecraft.core.component.DataComponentType;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import static net.kuina.magitech.component.magitechcomponents.ZOLTRAK_MODE;

public class RodItem extends Item {
    public static final DataComponentType<Long> ENERGY = DataComponentType.<Long>builder()
            .persistent(Codec.LONG)
            .networkSynchronized(ByteBufCodecs.VAR_LONG)
            .build();

    private static final Vec3[] OFFSETS = {
            new Vec3(-1.4, 2.0,  0.6), new Vec3(-1.4, 1.8,  0.0), new Vec3(-1.5, 1.6, -0.6),
            new Vec3( 1.4, 2.0,  0.6), new Vec3( 1.4, 1.8,  0.0), new Vec3( 1.5, 1.6, -0.6)
    };

    private final Map<UUID, Integer> rapidFireTickMap = new HashMap<>();
    private static final long SINGLE_COST = 15L;

    public RodItem(Properties p) { super(p); }

    @Override
    public InteractionResultHolder<ItemStack> use(Level lvl, Player pl, InteractionHand hand) {
        ItemStack stk = pl.getItemInHand(hand);
        if (pl.isCrouching()) {
            if (!lvl.isClientSide) {
                boolean rapid = stk.getOrDefault(ZOLTRAK_MODE.get(), false);
                stk.set(ZOLTRAK_MODE.get(), !rapid);
                pl.displayClientMessage(Component.literal("モード切替: " + (!rapid ? "連射" : "単発"))
                        .withStyle(ChatFormatting.AQUA), true);
            }
            return InteractionResultHolder.sidedSuccess(stk, lvl.isClientSide);
        }

        boolean rapid = stk.getOrDefault(ZOLTRAK_MODE.get(), false);

        if (!rapid) {
            if (!lvl.isClientSide) {
                if (!PlayerEtherEnergy.tryConsume(pl, SINGLE_COST)) {
                    warnNoEnergy(pl);
                    return InteractionResultHolder.fail(stk);
                }
                fire(lvl, pl, new Vec3(0, 1.5, -1.2).yRot((float) Math.toRadians(-pl.getYRot())), 0.1f, SINGLE_COST);
            }
            pl.getCooldowns().addCooldown(this, 10);
            return InteractionResultHolder.sidedSuccess(stk, lvl.isClientSide);
        }

        pl.startUsingItem(hand);
        return InteractionResultHolder.consume(stk);
    }

    @Override
    public void onUseTick(Level level, LivingEntity user, ItemStack stack, int remainingUseDuration) {
        if (!(user instanceof Player player) || level.isClientSide) return;
        if (!stack.getOrDefault(ZOLTRAK_MODE.get(), false)) return;

        UUID uuid = player.getUUID();
        int tick = rapidFireTickMap.getOrDefault(uuid, 0);
        rapidFireTickMap.put(uuid, tick + 1);

        int interval = Math.max(1, 20 - tick / 15);
        if (level.getGameTime() % interval != 0) return;

        long cost = 20 + (tick / 10);
        if (!PlayerEtherEnergy.tryConsume(player, cost)) {
            warnNoEnergy(player);
            return;
        }

        Vec3 offset = OFFSETS[level.getRandom().nextInt(OFFSETS.length)]
                .yRot((float) Math.toRadians(-player.getYRot()));
        fire(level, player, offset, 0.3f, cost);
    }

    private static void fire(Level lvl, Player pl, Vec3 off, float inaccurate, long cost) {
        ZoltrakProjectile proj = new ZoltrakProjectile(lvl, pl);
        proj.setPos(pl.getX() + off.x, pl.getY() + off.y, pl.getZ() + off.z);
        proj.shootFromRotation(pl, pl.getXRot(), pl.getYRot(), 0, 1.5f, inaccurate);
        lvl.addFreshEntity(proj);

        lvl.playSound(null, pl.getX(), pl.getY(), pl.getZ(),
                SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS,
                25f, 4f + lvl.random.nextFloat() * 0.2f);

        long remaining = PlayerEtherEnergy.getEnergy(pl);
        long capacity = PlayerEtherEnergy.get(pl).getCapacity();
        pl.displayClientMessage(Component.literal(" マナ: ")
                .append(Component.literal(remaining + " / " + capacity).withStyle(ChatFormatting.AQUA))
                .append(Component.literal(" (-" + cost + ")").withStyle(ChatFormatting.RED)), true);
    }

    private static void warnNoEnergy(Player pl){
        pl.displayClientMessage(Component.literal("エネルギーが足りません！")
                .withStyle(ChatFormatting.RED), true);
    }

    public static void saveEnergyToComponents(ItemStack stack, long energy) {
        stack.set(ENERGY, energy);
    }

    public static long getEnergyFromComponents(ItemStack stack) {
        return stack.getOrDefault(ENERGY, 0L);
    }

    public static void loadEnergyOnLogin(Player player, ItemStack stack) {
        long savedEnergy = getEnergyFromComponents(stack);
        PlayerEtherEnergy.setEnergy(player, savedEnergy);
    }

    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (entity instanceof Player pl) {
            rapidFireTickMap.remove(pl.getUUID());
        }
    }

    @Override public int getUseDuration(ItemStack s, LivingEntity e){ return 72000; }
    @Override public UseAnim getUseAnimation(ItemStack s){ return UseAnim.BOW; }
}
