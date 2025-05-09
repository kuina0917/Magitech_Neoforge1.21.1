package net.kuina.magitech;


import net.kuina.magitech.client.overlay.EnergyHudOverlay;
import net.kuina.magitech.component.magitechcomponents;
import net.kuina.magitech.energy.PlayerEventHandler;
import net.kuina.magitech.entity.magitechentities;
import net.kuina.magitech.client.renderer.MagicCircleRenderer;
import net.kuina.magitech.client.renderer.MagicCircleRapidFireRenderer;

import net.kuina.magitech.item.magitechitems;

import net.kuina.magitech.client.renderer.ZoltrakProjectileRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.item.CreativeModeTabs;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;




@Mod(magitech.MOD_ID)
public class magitech{
    public static final String MOD_ID = "magitech";
    private static final Logger LOGGER = LogUtils.getLogger();
    public magitech(IEventBus modEventBus, ModContainer modContainer)
    {

        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);
        net.kuina.magitech.block.magitechblocks.BLOCKS.register(modEventBus);
        net.kuina.magitech.block.magitechblockentities.BLOCK_ENTITIES.register(modEventBus);
        net.kuina.magitech.item.magitechitems.ITEMS.register(modEventBus);
        net.kuina.magitech.fluid.magitechfluids.FLUIDS.register(modEventBus);
        net.kuina.magitech.fluidtype.magitechfluidtypes.FLUID_TYPE.register(modEventBus);
        net.kuina.magitech.entity.magitechentities.ENTITIES.register(modEventBus);
        magitechcomponents.register(modEventBus);
        modEventBus.addListener(EnergyHudOverlay::registerGuiOverlay);
        modEventBus.addListener(this::addCreative);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        NeoForge.EVENT_BUS.register(new PlayerEventHandler());
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {


    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS){
            event.accept(magitechitems.LOW_MANA_INGOT);
            event.accept(magitechitems.MIDDLE_MANA_INGOT);
            event.accept(magitechitems.HIGH_MANA_INGOT);
            event.accept(magitechitems.MANA_BUCKET);
            event.accept(magitechitems.MANA_CRYSTAL);
            event.accept(magitechitems.CRYSTAL_ROD);
            event.accept(magitechitems.CREATIVE_ETHER_ENERGY_BLOCK);

        }
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS){
            event.accept(magitechitems.MANA_STONE);
            event.accept(magitechitems.MANA_COBBLESTONE);
            event.accept(magitechitems.TESTBLOCK);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event){
    }

    @EventBusSubscriber(modid = "magitech", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        { event.enqueueWork(() -> {
            EntityRenderers.register(
                    magitechentities.ZOLTRAK_PROJECTILE.get(),
                    ZoltrakProjectileRenderer::new
            );

            EntityRenderers.register(
                    magitechentities.MAGIC_CIRCLE.get(),
                    MagicCircleRenderer::new

            );
            EntityRenderers.register(
                    magitechentities.MAGIC_CIRCLE_RAPIDFIRE.get(),
                    MagicCircleRapidFireRenderer::new

            );


        });
        }}}


