package net.kuina.magitech.fluid;

import net.kuina.magitech.fluid.custom.ManaFluid;
import net.kuina.magitech.magitech;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FlowingFluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * マナ液体の登録。
 * 水源（{@link #MANA}）と流れ（{@link #FLOWING_MANA}）の 2 種類を登録する。
 */
public class magitechfluids {

    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(BuiltInRegistries.FLUID, magitech.MOD_ID);

    /** マナの水源。 */
    public static final DeferredHolder<Fluid, FlowingFluid> MANA =
            FLUIDS.register("mana", ManaFluid.Source::new);

    /** マナの流れ。 */
    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_MANA =
            FLUIDS.register("flowing_mana", ManaFluid.Flowing::new);

    /**
     * クライアント側の描画設定。
     * 半透明（translucent）レイヤーで描画し、奥が透けて見えるようにする。
     */
    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientFluidSetup {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            ItemBlockRenderTypes.setRenderLayer(MANA.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(FLOWING_MANA.get(), RenderType.translucent());
        }
    }
}
