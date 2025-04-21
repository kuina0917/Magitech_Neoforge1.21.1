package net.kuina.magitech.fluid.custom;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.neoforge.fluids.FluidType;

import javax.annotation.Nullable;
import java.util.Map;

public class ManaFluidType extends FluidType {

    public static final int MANA_FLUID_DENSITY = 1500; // マナ液体の密度
    public static final int MANA_FLUID_VISCOSITY = 1500; // マナ液体の粘度
    public static final int MANA_FLUID_LIGHT_LEVEL = 10; // マナ液体の光レベル
    public static final Rarity MANA_FLUID_RARITY = Rarity.UNCOMMON; // マナ液体のレアリティ

    public ManaFluidType(Properties properties) {
        super(properties);
    }

    // マナ液体の特性を定義したプロパティ
    public static Properties createProperties() {
        return FluidType.Properties.create()
                .descriptionId("fluid.magitech.mana")
                .motionScale(1.0) // 移動スケール
                .canPushEntity(false) // エンティティを押さない
                .canSwim(false) // 泳がない
                .canDrown(true) // 溺れる
                .fallDistanceModifier(0.0f) // 落下ダメージなし
                .canExtinguish(true) // 消火可能
                .canConvertToSource(false) // ソース状態には変換しない
                .supportsBoating(false) // ボートをサポートしない
                .pathType(PathType.WATER)
                .adjacentPathType(PathType.WATER_BORDER)
                .canHydrate(true) // 植物などを水分補給可能
                .lightLevel(MANA_FLUID_LIGHT_LEVEL) // 光レベル
                .density(MANA_FLUID_DENSITY) // 密度
                .temperature(300) // 温度（例：300K）
                .viscosity(MANA_FLUID_VISCOSITY) // 粘度
                .rarity(MANA_FLUID_RARITY); // レアリティ
    }
}