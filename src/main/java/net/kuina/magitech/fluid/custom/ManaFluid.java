package net.kuina.magitech.fluid.custom;

import net.kuina.magitech.block.magitechblocks;
import net.kuina.magitech.fluidtype.magitechfluidtypes;
import net.kuina.magitech.item.magitechitems;
import net.kuina.magitech.fluid.magitechfluids;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.block.state.StateDefinition;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

public abstract class ManaFluid extends BaseFlowingFluid {

    // Fluidの共通プロパティ（NeoForge式）
    public static final Properties PROPERTIES = new Properties(
            () -> magitechfluidtypes.MANA_FLUID_TYPE.get(),                  // FluidType
            () -> magitechfluids.MANA.get(),           // Still
            () -> magitechfluids.FLOWING_MANA.get()          // Flowing
    ).explosionResistance(100f)
            .bucket(() -> magitechitems.MANA_BUCKET.get())
            .block(() -> (LiquidBlock) magitechblocks.MANA.get());

    // コンストラクタ
    private ManaFluid() {
        super(PROPERTIES);
    }

    // ドリップ演出
    @Override
    public ParticleOptions getDripParticle() {return ParticleTypes.ASH;}

    // Sourceクラス（8段階・ソース）
    public static class Source extends ManaFluid {

        public int getAmount(FluidState state) {return 8;}
        public boolean isSource(FluidState state) {return true;}

    }

    // Flowingクラス（レベル付き）
    public static class Flowing extends ManaFluid {
        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
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
