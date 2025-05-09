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
import net.minecraft.core.component.DataComponents; // Vanillaの場合
import static net.kuina.magitech.component.magitechcomponents.ZOLTRAK_MODE;


public class RodItem extends Item {

    // ENERGY コンポーネントを DataComponentType.Builder を使って作成
    // ENERGYをDataComponentTypeとして定義
    public static final DataComponentType<Long> ENERGY = DataComponentType.<Long>builder()
            .persistent(Codec.LONG)  // Codec.LONGを使用してlong型を指定
            .networkSynchronized(ByteBufCodecs.VAR_LONG)  // ネットワーク同期
            .build();  // buildメソッドでコンポーネントを作成

    private static final Vec3[] OFFSETS = {
            new Vec3(-1.4, 2.0,  0.6),
            new Vec3(-1.4, 1.8,  0.0),
            new Vec3(-1.5, 1.6, -0.6),
            new Vec3( 1.4, 2.0,  0.6),
            new Vec3( 1.4, 1.8,  0.0),
            new Vec3( 1.5, 1.6, -0.6)
    };

    private static final long SINGLE_COST = 10L;
    private static final long RAPID_COST  = 14L;

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

    /* DataComponentを使ったエネルギー保存 */
    public static void saveEnergyToComponents(ItemStack stack, long energy) {
        // DataComponentを使ってエネルギーを保存
        stack.set(ENERGY, energy); // ENERGYコンポーネントを使って保存
    }

    public static long getEnergyFromComponents(ItemStack stack) {
        // DataComponentを使ってエネルギーを取得
        return stack.getOrDefault(ENERGY, 0L); // ENERGYコンポーネントを使って取得
    }

    /* ログイン時やアイテム使用時にエネルギーを復元する例 */
    public static void loadEnergyOnLogin(Player player, ItemStack stack) {
        long savedEnergy = getEnergyFromComponents(stack);  // コンポーネントからエネルギーを取得
        PlayerEtherEnergy.setEnergy(player, savedEnergy);  // プレイヤーにエネルギーを設定
    }

    /* その他オーバーライド */
    @Override public void releaseUsing(ItemStack s, Level l, LivingEntity e, int t) {}
    @Override public int  getUseDuration(ItemStack s, LivingEntity e){ return 72000; }
    @Override public UseAnim getUseAnimation(ItemStack s){ return UseAnim.BOW; }
}
