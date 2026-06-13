package net.kuina.magitech.fluidtype;

import net.kuina.magitech.fluidtype.custom.ManaFluidType;
import net.kuina.magitech.magitech;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * 液体の「種類（FluidType）」の登録。
 * FluidType は見た目・物理挙動・音などの性質をまとめたもので、
 * 水源と流れの両方が同じ FluidType を共有する。
 *
 * <p>「マナ系の液体」は {@link #registerManaFluidType} で登録すると一覧に加わり、
 * {@link #isManaFluidType} でまとめて判定できる。今後マナ液体を増やしても、
 * つるはしの回復処理など利用側のコードを変更せずに自動で対応できる。</p>
 */
public class magitechfluidtypes {

    public static final DeferredRegister<FluidType> FLUID_TYPE =
            DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, magitech.MOD_ID);

    /** これまでに登録された「マナ系」液体タイプの一覧。 */
    private static final List<DeferredHolder<FluidType, FluidType>> MANA_FLUID_TYPES = new ArrayList<>();

    /** マナ液体の種類。 */
    public static final DeferredHolder<FluidType, FluidType> MANA_FLUID_TYPE =
            registerManaFluidType("mana", ManaFluidType::new);

    /**
     * マナ系の FluidType を登録する。
     * 通常の登録に加えて「マナ液体一覧」にも登録されるため、
     * {@link #isManaFluidType} の判定対象に自動で含まれる。
     */
    public static DeferredHolder<FluidType, FluidType> registerManaFluidType(String name, Supplier<? extends FluidType> factory) {
        DeferredHolder<FluidType, FluidType> holder = FLUID_TYPE.register(name, factory);
        MANA_FLUID_TYPES.add(holder);
        return holder;
    }

    /** 指定した FluidType がマナ系の液体かどうかを判定する。 */
    public static boolean isManaFluidType(FluidType type) {
        for (DeferredHolder<FluidType, FluidType> holder : MANA_FLUID_TYPES) {
            if (holder.get() == type) {
                return true;
            }
        }
        return false;
    }
}
