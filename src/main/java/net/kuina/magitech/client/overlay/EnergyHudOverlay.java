package net.kuina.magitech.client.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.kuina.magitech.energy.PlayerEtherEnergy;
import net.kuina.magitech.item.custom.RodItem;
import net.minecraft.client.gui.GuiGraphics;

public class EnergyHudOverlay {
    public static void registerGuiOverlay(RegisterGuiLayersEvent event) {
        ResourceLocation hudId = ResourceLocation.parse("magitech:ether_energy_overlay");

        // プレイヤー体力バーより上に安全に描画
        event.registerAboveAll(hudId, (guiGraphics, partialTick) -> {
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            if (player == null) return;

            boolean holdingRod = player.getMainHandItem().getItem() instanceof RodItem ||
                    player.getOffhandItem().getItem() instanceof RodItem;
            if (!holdingRod) return;

            long energy = PlayerEtherEnergy.getEnergy(player);
            long max = PlayerEtherEnergy.DEFAULT_CAPACITY;
            float ratio = energy / (float) max;

            int width = 81;
            int height = 9;

            int screenWidth = mc.getWindow().getGuiScaledWidth();
            int screenHeight = mc.getWindow().getGuiScaledHeight();

            int x = screenWidth - width - 5;
            int y = screenHeight - height - 5;

            guiGraphics.fill(x, y, x + width, y + height, 0x80000000);
            guiGraphics.fill(x + 1, y + 1, x + 1 + Math.round((width - 2) * ratio), y + height - 1, 0xFF00FFFF);
        });
    }
}
