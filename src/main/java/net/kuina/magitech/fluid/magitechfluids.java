package net.kuina.magitech.fluid;

import net.kuina.magitech.fluid.custom.ManaFluid;
import net.kuina.magitech.magitech;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;


public class magitechfluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, magitech.MOD_ID);
    public static final DeferredHolder<Fluid, FlowingFluid> MANA = FLUIDS.register("mana", () -> new ManaFluid.Source());
    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_MANA = FLUIDS.register("flowing_mana", () -> new ManaFluid.Flowing());




    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientFluidSetup {
        @SubscribeEvent
        public static void ClientSetup(FMLClientSetupEvent event) {
            ItemBlockRenderTypes.setRenderLayer(MANA.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(FLOWING_MANA.get(), RenderType.translucent());

        }
    }
}


