package net.kuina.magitech.item.custom;

import net.kuina.magitech.energy.PlayerEtherEnergy;
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

import static net.kuina.magitech.component.magitechcomponents.ZOLTRAK_MODE;

public class RodItem extends Item {

    /* 背面６点の発射オフセット（左右３発ずつ） */
    private static final Vec3[] OFFSETS = {
            new Vec3(-1.4, 2.0,  0.6),
            new Vec3(-1.4, 1.8,  0.0),
            new Vec3(-1.5, 1.6, -0.6),
            new Vec3( 1.4, 2.0,  0.6),
            new Vec3( 1.4, 1.8,  0.0),
            new Vec3( 1.5, 1.6, -0.6)
    };

    private static final int SINGLE_COST = 10;
    private static final int RAPID_COST  = 10;

    public RodItem(Properties p) { super(p); }

    /* --------------------------------------------------------------------- */
    /*  右クリック                                                            */
    /* --------------------------------------------------------------------- */
    @Override
    public InteractionResultHolder<ItemStack> use(Level lvl, Player pl, InteractionHand hand) {
        ItemStack stk = pl.getItemInHand(hand);

        /* スニークでモード切替 */
        if (pl.isCrouching()) {
            if (!lvl.isClientSide) {
                boolean rapid = stk.getOrDefault(ZOLTRAK_MODE.get(), false);
                stk.set(ZOLTRAK_MODE.get(), !rapid);
                pl.displayClientMessage(Component.literal(
                                "モード切替: " + (!rapid ? "連射" : "単発"))
                        .withStyle(ChatFormatting.AQUA), true);
            }
            return InteractionResultHolder.sidedSuccess(stk, lvl.isClientSide);
        }

        boolean rapid = stk.getOrDefault(ZOLTRAK_MODE.get(), false);

        /* -------------------------------- 単発モード ----------------------- */
        if (!rapid) {
            if (!lvl.isClientSide) {                                   // ★ サーバー側だけで消費
                if (!PlayerEtherEnergy.tryConsume(pl, SINGLE_COST)) {
                    warnNoEnergy(pl);
                    return InteractionResultHolder.fail(stk);
                }
            }

            fire(lvl, pl, new Vec3(0, 1, 0), 0f);
            pl.getCooldowns().addCooldown(this, 10);
            return InteractionResultHolder.sidedSuccess(stk, lvl.isClientSide);
        }

        /* -------------------------------- 連射モード ----------------------- */
        pl.startUsingItem(hand);
        return InteractionResultHolder.consume(stk);
    }

    /* --------------------------------------------------------------------- */
    /*  長押し中に毎 tick 呼ばれる（連射）                                    */
    /* --------------------------------------------------------------------- */
    @Override
    public void onUseTick(Level lvl, LivingEntity usr, ItemStack stk, int rem) {
        if (!(usr instanceof Player pl) || lvl.isClientSide)   return;
        if (!stk.getOrDefault(ZOLTRAK_MODE.get(), false))      return; // 単発モードなら無視
        if (lvl.getGameTime() % 4 != 0)                        return; // 4tick に 1 発

        /* エネルギー消費（サーバー側だけ呼ばれる） */
        if (!PlayerEtherEnergy.tryConsume(pl, RAPID_COST)) {
            warnNoEnergy(pl);
            return;
        }

        Vec3 off = OFFSETS[lvl.getRandom().nextInt(OFFSETS.length)]
                .yRot((float) Math.toRadians(-pl.getYRot()));
        fire(lvl, pl, off, 0.3f);
    }

    /* --------------------------------------------------------------------- */
    /*  発射物生成 + 効果音                                                   */
    /* --------------------------------------------------------------------- */
    private static void fire(Level lvl, Player pl, Vec3 off, float inaccurate) {
        ZoltrakProjectile proj = new ZoltrakProjectile(lvl, pl);
        proj.setPos(pl.getX() + off.x, pl.getY() + off.y, pl.getZ() + off.z);
        proj.shootFromRotation(pl, pl.getXRot(), pl.getYRot(), 0, 1.5f, inaccurate);
        lvl.addFreshEntity(proj);

        lvl.playSound(null, pl.getX(), pl.getY(), pl.getZ(),
                SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS,
                25f, 4f + lvl.random.nextFloat() * 0.2f);

        // デバッグ用：残量表示
        pl.displayClientMessage(Component.literal(
                "残量: " + PlayerEtherEnergy.getEnergy(pl)), true);
    }

    private static void warnNoEnergy(Player pl){
        pl.displayClientMessage(Component.literal("エネルギーが足りません！")
                .withStyle(ChatFormatting.RED), true);
    }

    /* その他オーバーライド */
    @Override public void releaseUsing(ItemStack s, Level l, LivingEntity e, int t) {}
    @Override public int  getUseDuration(ItemStack s, LivingEntity e){ return 72000; }
    @Override public UseAnim getUseAnimation(ItemStack s){ return UseAnim.BOW; }
}

