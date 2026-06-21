package net.kuina.magitech.client.screen;

import net.kuina.magitech.menu.ManaProcessorMenu;
import net.kuina.magitech.util.ModUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ManaProcessorScreen extends AbstractContainerScreen<ManaProcessorMenu> {

    private static final ResourceLocation BG_TEXTURE = ModUtil.rl("textures/gui/machine_gui.png");
    private static final ResourceLocation SLOT_TEXTURE = ModUtil.rl("textures/gui/gui_item_slot.png");
    private static final ResourceLocation BAR_FRAME_TEXTURE = ModUtil.rl("textures/gui/barframe.png");
    private static final ResourceLocation MANA_BAR_TEXTURE = ModUtil.rl("textures/gui/mana_bar.png");
    private static final ResourceLocation ARROW_TEXTURE = ModUtil.rl("textures/gui/arrow_icon.png");

    private static final int MANA_BAR_X = 14;
    private static final int MANA_BAR_Y = 18;
    private static final int MANA_BAR_WIDTH = 12;
    private static final int MANA_BAR_HEIGHT = 48;

    private static final int ARROW_X = 79;
    private static final int ARROW_Y = 34;
    private static final int ARROW_WIDTH = 22;
    private static final int ARROW_HEIGHT = 15;
    private static final int ARROW_TEX_HEIGHT = ARROW_HEIGHT * 2;

    public ManaProcessorScreen(ManaProcessorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        // 1. Background panel (176x166)
        guiGraphics.blit(BG_TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight, 176, 166);

        // 2. Slot frames (18x18)
        guiGraphics.blit(SLOT_TEXTURE, x + 55, y + 16, 0, 0, 18, 18, 18, 18);
        guiGraphics.blit(SLOT_TEXTURE, x + 55, y + 52, 0, 0, 18, 18, 18, 18);
        guiGraphics.blit(SLOT_TEXTURE, x + 115, y + 34, 0, 0, 18, 18, 18, 18);

        // 3. Mana bar background (barframe = solid gray interior + borders)
        guiGraphics.blit(BAR_FRAME_TEXTURE, x + MANA_BAR_X - 1, y + MANA_BAR_Y - 1, 0, 0, 14, 50, 14, 50);

        // 4. Mana bar fill (mana_bar.png, rendered bottom-up)
        int filled = this.menu.getManaScaled(MANA_BAR_HEIGHT);
        if (filled > 0) {
            int drawY = y + MANA_BAR_Y + (MANA_BAR_HEIGHT - filled);
            int v = MANA_BAR_HEIGHT - filled;
            guiGraphics.blit(MANA_BAR_TEXTURE, x + MANA_BAR_X, drawY,
                    0, v, MANA_BAR_WIDTH, filled, MANA_BAR_WIDTH, MANA_BAR_HEIGHT);
        }

        // 5. Arrow background — upper half (gray icon, always visible)
        guiGraphics.blit(ARROW_TEXTURE, x + ARROW_X, y + ARROW_Y,
                0, 0, ARROW_WIDTH, ARROW_HEIGHT, ARROW_WIDTH, ARROW_TEX_HEIGHT);

        // 6. Arrow progress overlay — lower half (white) clipped left
        int progress = this.menu.getProgressScaled(ARROW_WIDTH);
        if (progress > 0) {
            guiGraphics.blit(ARROW_TEXTURE, x + ARROW_X, y + ARROW_Y,
                    0, ARROW_HEIGHT, progress, ARROW_HEIGHT, ARROW_WIDTH, ARROW_TEX_HEIGHT);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int barX = this.leftPos + MANA_BAR_X;
        int barTop = this.topPos + MANA_BAR_Y;
        if (mouseX >= barX && mouseX < barX + MANA_BAR_WIDTH && mouseY >= barTop && mouseY < barTop + MANA_BAR_HEIGHT) {
            long stored = this.menu.getManaStored();
            long max = this.menu.getMaxMana();
            guiGraphics.renderTooltip(this.font,
                    Component.translatable("tooltip.magitech.mana_stored", stored, max),
                    mouseX, mouseY);
        }
    }
}
