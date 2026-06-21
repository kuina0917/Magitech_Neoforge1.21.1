package net.kuina.magitech.client.screen;

import net.kuina.magitech.menu.ManaTankMenu;
import net.kuina.magitech.util.ModUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class ManaTankScreen extends AbstractContainerScreen<ManaTankMenu> {

    private static final ResourceLocation MANA_BAR_TEXTURE = ModUtil.rl("textures/gui/mana_bar.png");
    private static final ResourceLocation SLOT_TEXTURE = ModUtil.rl("textures/gui/gui_item_slot.png");

    private static final int MANA_BAR_X = 14;
    private static final int MANA_BAR_Y = 18;
    private static final int MANA_BAR_WIDTH = 12;
    private static final int MANA_BAR_HEIGHT = 48;

    private static final int R = 6;

    public ManaTankScreen(ManaTankMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 35;
        this.titleLabelY = 6;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        fillRoundRect(g, x, y, imageWidth, imageHeight, 0xFF1E0038);
        outlineRoundRect(g, x, y, imageWidth, imageHeight, 0xFFAA66FF);

        for (Slot slot : this.menu.slots) {
            g.blit(SLOT_TEXTURE, x + slot.x - 1, y + slot.y - 1, 0, 0, 18, 18, 18, 18);
        }

        int filled = this.menu.getManaScaled(MANA_BAR_HEIGHT);
        if (filled > 0) {
            int drawY = y + MANA_BAR_Y + (MANA_BAR_HEIGHT - filled);
            int v = MANA_BAR_HEIGHT - filled;
            g.blit(MANA_BAR_TEXTURE, x + MANA_BAR_X, drawY, 0, v,
                    MANA_BAR_WIDTH, filled, MANA_BAR_WIDTH, MANA_BAR_HEIGHT);
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);

        int barX = this.leftPos + MANA_BAR_X;
        int barTop = this.topPos + MANA_BAR_Y;
        if (mouseX >= barX && mouseX < barX + MANA_BAR_WIDTH
                && mouseY >= barTop && mouseY < barTop + MANA_BAR_HEIGHT) {
            g.renderTooltip(this.font,
                    Component.translatable("tooltip.magitech.mana_stored",
                            this.menu.getManaStored(), this.menu.getMaxMana()),
                    mouseX, mouseY);
        }

        this.renderTooltip(g, mouseX, mouseY);
    }

    private void fillRoundRect(GuiGraphics g, int x, int y, int w, int h, int color) {
        g.fill(x + R, y, x + w - R, y + h, color);
        g.fill(x, y + R, x + w, y + h - R, color);
    }

    private void outlineRoundRect(GuiGraphics g, int x, int y, int w, int h, int color) {
        int o = 1;
        g.fill(x + R - o, y - o, x + w - R + o, y, color);
        g.fill(x + R - o, y + h, x + w - R + o, y + h + o, color);
        g.fill(x - o, y + R - o, x, y + h - R + o, color);
        g.fill(x + w, y + R - o, x + w + o, y + h - R + o, color);
        g.fill(x + R, y, x + w - R, y + 1, color);
        g.fill(x + R, y + h - 1, x + w - R, y + h, color);
        g.fill(x, y + R, x + 1, y + h - R, color);
        g.fill(x + w - 1, y + R, x + w, y + h - R, color);

        int d = 1;
        g.fill(x + R - d, y, x + R + d, y + 1, color);
        g.fill(x + w - R - d, y, x + w - R + d, y + 1, color);
        g.fill(x + R - d, y + h - 1, x + R + d, y + h, color);
        g.fill(x + w - R - d, y + h - 1, x + w - R + d, y + h, color);
        g.fill(x, y + R - d, x + 1, y + R + d, color);
        g.fill(x, y + h - R - d, x + 1, y + h - R + d, color);
        g.fill(x + w - 1, y + R - d, x + w, y + R + d, color);
        g.fill(x + w - 1, y + h - R - d, x + w, y + h - R + d, color);
    }
}
