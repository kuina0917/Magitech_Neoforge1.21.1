package net.kuina.magitech.fluid.custom;

import net.kuina.magitech.block.magitechblocks;
import net.kuina.magitech.fluid.magitechfluids;
import net.kuina.magitech.fluidtype.magitechfluidtypes;
import net.kuina.magitech.item.magitechitems;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

/**
 * マナの液体本体。
 *
 * <p>NeoForge では 1 つの液体を「ソース（水源）」と「フロー（流れ）」の
 * 2 つの {@link Fluid} で表現する。この抽象クラスに両者の共通設定をまとめ、
 * 実体は内部クラス {@link Source} / {@link Flowing} として用意する。</p>
 */
public abstract class ManaFluid extends BaseFlowingFluid {

    /**
     * ソースとフロー共通のプロパティ。
     * FluidType・水源・流れ・バケツ・設置ブロックなどを紐付ける。
     */
    public static final Properties PROPERTIES = new Properties(
            () -> magitechfluidtypes.MANA_FLUID_TYPE.get(), // この液体の種類（FluidType）
            () -> magitechfluids.MANA.get(),                // 水源となる Fluid
            () -> magitechfluids.FLOWING_MANA.get())        // 流れとなる Fluid
            .explosionResistance(100f)                          // 爆発耐性
            .bucket(() -> magitechitems.MANA_BUCKET.get())      // バケツアイテム
            .block(() -> (LiquidBlock) magitechblocks.MANA.get()); // ワールドに置かれるブロック

    private ManaFluid() {
        super(PROPERTIES);
    }

    /** 滴り落ちるときのパーティクル。 */
    @Override
    public ParticleOptions getDripParticle() {
        return ParticleTypes.ASH;
    }

    /**
     * 水源（満タンで動かない状態）。常に量 8・isSource = true。
     */
    public static class Source extends ManaFluid {

        @Override
        public int getAmount(FluidState state) {
            return 8;
        }

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }
    }

    /**
     * 流れ（高さ＝レベルを持ち、低い方へ広がる状態）。
     */
    public static class Flowing extends ManaFluid {

        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL); // 流れの高さ（1〜7）を状態として追加
        }

        @Override
        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        @Override
        public boolean isSource(FluidState state) {
            return false;
        }
    }
}
