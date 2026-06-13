package net.kuina.magitech.fluidtype.custom;

import net.kuina.magitech.fluidtype.magitechfluidtypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;

/**
 * マナ液体の性質（FluidType）。
 * 物理挙動・音などの「サーバー/共通」設定はコンストラクタで、
 * テクスチャなどの「クライアント描画」設定はイベントで登録する。
 */
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ManaFluidType extends FluidType {

    public ManaFluidType() {
        super(FluidType.Properties.create()
                .fallDistanceModifier(0F)   // 落下ダメージを無効化
                .canExtinguish(true)        // 火を消せる
                .supportsBoating(true)      // ボートで進める
                .canHydrate(true)           // 農地などを潤せる
                .motionScale(0.007D)        // 中にいるときの動きの鈍さ
                .lightLevel(8)              // 自身が発する明るさ
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)       // バケツで汲む音
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.LILY_PAD_PLACE)   // バケツで流す音
                .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)); // 蒸発する音
    }

    /**
     * クライアント側の描画拡張を登録する。
     * 静止時・流動時に使うテクスチャを指定する。
     */
    @SubscribeEvent
    public static void registerFluidTypeExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            private static final ResourceLocation STILL_TEXTURE =
                    ResourceLocation.parse("magitech:block/mana_still");
            private static final ResourceLocation FLOWING_TEXTURE =
                    ResourceLocation.parse("magitech:block/mana_flowing");

            @Override
            public ResourceLocation getStillTexture() {
                return STILL_TEXTURE;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return FLOWING_TEXTURE;
            }
        }, magitechfluidtypes.MANA_FLUID_TYPE.get());
    }
}
