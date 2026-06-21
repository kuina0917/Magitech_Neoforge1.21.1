package net.kuina.magitech.client.event;

import net.kuina.magitech.network.c2s.OpenRelicBoardPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = "magitech", bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class RelicBoardClientEvents {

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof InventoryScreen screen)) return;

        int guiLeft = (screen.width - 176) / 2;
        int guiTop = (screen.height - 166) / 2;

        int btnX = guiLeft + 130;
        int btnY = guiTop + 10;

        event.addListener(Button.builder(
            Component.literal("\u25C7"),
            btn -> {
                PacketDistributor.sendToServer(OpenRelicBoardPayload.INSTANCE);
            }
        ).pos(btnX, btnY).size(20, 20).build());
    }
}
