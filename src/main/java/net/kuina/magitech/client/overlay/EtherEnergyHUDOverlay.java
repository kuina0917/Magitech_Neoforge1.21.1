package net.kuina.magitech.client.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import net.kuina.magitech.energy.EtherEnergyStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RenderGuiOverlayEvent;
import net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay;
import net.neoforged.bus.api.SubscribeEvent;

public class EtherEnergyHUDOverlay {

    private static final ResourceLocation BAR_TEXTURE =
            new ResourceLocation("magitech:textures/gui/ether_bar.png");

    @SubscribeEvent
    public void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() != VanillaGuiOverlay.HOTBAR) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        EtherEnergyStorage storage = getEnergyStorage(player);
        int max = storage.getCapacity();
        int current = storage.getEnergy();

        GuiGraphics gui = event.getGuiGraphics();
        int barWidth = 80;
        int barHeight = 10;
        int filled = (int)((float) current / max * barWidth);

        int x = mc.getWindow().getGuiScaledWidth() / 2 - barWidth / 2;
        int y = mc.getWindow().getGuiScaledHeight() - 50;

        // 背景（黒い縁）
        gui.fill(x - 1, y - 1, x + barWidth + 1, y + barHeight + 1, 0xFF000000);

        // バー（シアン色）
        gui.fill(x, y, x + filled, y + barHeight, 0xFF00FFFF);

        // 数値テキスト
        gui.drawString(mc.font,
                Component.literal(current + " / " + max),
                x + barWidth + 5, y,
                0xFFFFFF, true);
    }

    // テスト用エネルギー取得メソッド
    private EtherEnergyStorage getEnergyStorage(LocalPlayer player) {
        return new EtherEnergyStorage(100); // テスト用。後でプレイヤーに紐付ける
    }
}