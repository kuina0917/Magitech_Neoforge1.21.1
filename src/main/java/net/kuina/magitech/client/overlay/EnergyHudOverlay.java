package net.kuina.magitech.client.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.kuina.magitech.energy.PlayerEtherEnergy;
import net.minecraft.client.gui.GuiGraphics;

public class EnergyHudOverlay {
    public static void registerGuiOverlay(RegisterGuiLayersEvent event) {
        ResourceLocation hudId = ResourceLocation.parse("magitech:ether_energy_overlay");

        event.registerAboveAll(hudId, (guiGraphics, partialTick) -> {
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            if (player == null) return;

            if (!PlayerEtherEnergy.hasManaContainer(player)) return;

            long mana = PlayerEtherEnergy.getTotalMana(player);
            long max = PlayerEtherEnergy.getTotalCapacity(player);
            float ratio = max > 0 ? mana / (float) max : 0;

            int barWidth = 108;
            int barHeight = 14;
            int screenWidth = mc.getWindow().getGuiScaledWidth();
            int screenHeight = mc.getWindow().getGuiScaledHeight();
            int hotbarRightX = (screenWidth / 2) + 91;
            int x = hotbarRightX + 5;
            int y = screenHeight - 22;

            double time = System.currentTimeMillis() / 1000.0;

            // Outer glow
            int glowAlpha = (int) (Math.sin(time * 1.5) * 8 + 28);
            guiGraphics.fill(x - 2, y - 2, x + barWidth + 2, y + barHeight + 2, (glowAlpha << 24) | 0x9944FF);

            // Background
            guiGraphics.fill(x, y, x + barWidth, y + barHeight, 0x80100028);

            // Mana fill with gradient
            if (ratio > 0 && max > 0) {
                int fillWidth = Math.round((barWidth - 2) * ratio);
                if (fillWidth > 0) {
                    guiGraphics.fill(x + 1, y + 1, x + 1 + fillWidth, y + barHeight - 1, 0xFF4400CC);
                    int halfH = (barHeight - 1) / 2;
                    guiGraphics.fill(x + 1, y + 1, x + 1 + fillWidth, y + 1 + halfH, 0x8800FFFF);
                    int cx = x + 1 + fillWidth / 2;
                    int cw = Math.min(fillWidth, 28);
                    guiGraphics.fill(cx - cw / 2, y + 1, cx + cw / 2, y + barHeight - 1, 0x50FFFFFF);
                }
            }

            // Border
            guiGraphics.fill(x, y, x + barWidth, y + 1, 0xFF6622CC);
            guiGraphics.fill(x, y + barHeight - 1, x + barWidth, y + barHeight, 0xFF6622CC);
            guiGraphics.fill(x, y, x + 1, y + barHeight, 0xFF6622CC);
            guiGraphics.fill(x + barWidth - 1, y, x + barWidth, y + barHeight, 0xFF6622CC);

            // Crystal end decorations
            drawCrystal(guiGraphics, x + 4, y + barHeight / 2, 0xFF9944FF);
            drawCrystal(guiGraphics, x + barWidth - 5, y + barHeight / 2, 0xFF9944FF);

            // Sparkle particles
            for (int i = 0; i < 4; i++) {
                double phase = time * 1.2 + i * 1.571;
                float px = (float) ((Math.sin(phase) * 0.5 + 0.5) * (barWidth - 10)) + 5;
                float py = y + (float) (Math.cos(phase * 1.7) * 3 + 2);
                float alpha = (float) (Math.sin(phase * 2.5) * 0.3 + 0.5);
                int sc = ((int) (alpha * 180) << 24) | 0xCCCCFF;
                int spx = (int) px;
                int spy = (int) py;
                guiGraphics.fill(spx, spy, spx + 1, spy + 1, sc);
            }

            // Text
            String text = "\u2727 " + mana + " / " + max + " \u2727";
            int tw = mc.font.width(text);
            int tx = x + (barWidth - tw) / 2;
            int ty = y + (barHeight - mc.font.lineHeight) / 2;
            guiGraphics.drawString(mc.font, text, tx + 1, ty + 1, 0x60000000, false);
            guiGraphics.drawString(mc.font, text, tx - 1, ty - 1, 0x40000000, false);
            guiGraphics.drawString(mc.font, text, tx, ty, 0xFFE0CCFF, false);
        });
    }

    private static void drawCrystal(GuiGraphics g, int cx, int cy, int color) {
        g.fill(cx, cy - 2, cx + 1, cy - 1, color);
        g.fill(cx - 1, cy - 1, cx + 2, cy, color);
        g.fill(cx - 1, cy, cx + 2, cy + 1, color);
        g.fill(cx, cy + 1, cx + 1, cy + 2, color);
    }
}
