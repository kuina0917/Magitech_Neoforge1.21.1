package net.kuina.magitech;

import net.kuina.magitech.block.magitechblockentities;
import net.kuina.magitech.block.magitechblocks;
import net.kuina.magitech.client.overlay.EnergyHudOverlay;
import net.kuina.magitech.component.magitechcomponents;
import net.kuina.magitech.energy.PlayerEventHandler;
import net.kuina.magitech.entity.magitechentities;
import net.kuina.magitech.client.renderer.MagicCircleRenderer;
import net.kuina.magitech.client.renderer.MagicCircleRapidFireRenderer;

import net.kuina.magitech.fluid.magitechfluids;
import net.kuina.magitech.fluidtype.magitechfluidtypes;
import net.kuina.magitech.item.magitechitems;
import net.kuina.magitech.item.magitechtabs;
import net.kuina.magitech.menu.magitechmenus;

import net.kuina.magitech.client.renderer.ZoltrakProjectileRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;

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
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(magitech.MOD_ID)
public class magitech {
    public static final String MOD_ID = "magitech";
    private static final Logger LOGGER = LogUtils.getLogger();

    public magitech(IEventBus modEventBus, ModContainer modContainer) {

        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this); // プレイヤーイベント等の登録

        // 登録系
        magitechblocks.BLOCKS.register(modEventBus);
        magitechblockentities.BLOCK_ENTITIES.register(modEventBus);
        magitechitems.ITEMS.register(modEventBus);
        magitechfluids.FLUIDS.register(modEventBus);
        magitechfluidtypes.FLUID_TYPE.register(modEventBus);
        magitechentities.ENTITIES.register(modEventBus);
        magitechcomponents.register(modEventBus);
        magitechtabs.register(modEventBus);
        magitechmenus.register(modEventBus);
        net.kuina.magitech.recipe.ModRecipes.SERIALIZERS.register(modEventBus);
        net.kuina.magitech.recipe.ModRecipes.TYPES.register(modEventBus);
        net.kuina.magitech.worldgen.magitechfeatures.register(modEventBus);

        // ネットワークパケットの登録
        modEventBus.addListener((net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent event) -> {
            final net.neoforged.neoforge.network.registration.PayloadRegistrar registrar = event.registrar(MOD_ID);
            registrar.playToClient(
                    net.kuina.magitech.network.SyncManaTargetsPayload.TYPE,
                    net.kuina.magitech.network.SyncManaTargetsPayload.STREAM_CODEC,
                    net.kuina.magitech.network.ManaExtractorPayloadHandler::handleSyncTargets
            );
        });

        // HUD
        modEventBus.addListener(EnergyHudOverlay::registerGuiOverlay);

        // コンフィグ
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        // プレイヤーイベントハンドラ
        NeoForge.EVENT_BUS.register(new PlayerEventHandler());
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.RegisterCommandsEvent e) -> {
                net.kuina.magitech.command.SetManaCommand.register(e.getDispatcher());
            });
        });

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    @EventBusSubscriber(modid = "magitech", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                EntityRenderers.register(
                        magitechentities.ZOLTRAK_PROJECTILE.get(),
                        ZoltrakProjectileRenderer::new);

                EntityRenderers.register(
                        magitechentities.MAGIC_CIRCLE.get(),
                        MagicCircleRenderer::new

                );
                EntityRenderers.register(
                        magitechentities.MAGIC_CIRCLE_RAPIDFIRE.get(),
                        MagicCircleRapidFireRenderer::new

                );
            });
        }

        /** GUI 画面の登録（MenuType と Screen を結び付ける）。 */
        @SubscribeEvent
        public static void onRegisterScreens(net.neoforged.neoforge.client.event.RegisterMenuScreensEvent event) {
            event.register(
                    magitechmenus.MANA_PROCESSOR_MENU.get(),
                    net.kuina.magitech.client.screen.ManaProcessorScreen::new);
            event.register(
                    magitechmenus.MANA_EXTRACTOR_MENU.get(),
                    net.kuina.magitech.client.screen.ManaExtractorScreen::new);
        }
    }
}
ださい