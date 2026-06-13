package net.kuina.magitech.client.screen;

import net.kuina.magitech.menu.ManaProcessorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/**
 * マナ加工機の GUI 画面。
 *
 * <p>専用のテクスチャ画像を用意せずに済むよう、背景・スロット枠・進捗矢印・マナバーを
 * すべて単色の矩形で描画する（後でテクスチャ化したい場合は {@link #renderBg} を差し替える）。</p>
 */
public class ManaProcessorScreen extends AbstractContainerScreen<ManaProcessorMenu> {

    // 各色（ARGB）
    private static final int COLOR_PANEL = 0xFFC6C6C6; // 背景パネル
    private static final int COLOR_SLOT = 0xFF8B8B8B;  // スロット枠
    private static final int COLOR_BAR_BG = 0xFF373737; // バー背景
    private static final int COLOR_MANA = 0xFF29B6F6;  // マナ（水色）
    private static final int COLOR_PROGRESS = 0xFF66BB6A; // 進捗（緑）

    private static final int ARROW_WIDTH = 24;
    private static final int MANA_BAR_HEIGHT = 52;

    public ManaProcessorScreen(ManaProcessorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        // タイトルとラベルを左上に寄せる
        this.titleLabelX = 8;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        // 背景パネル
        guiGraphics.fill(x, y, x + this.imageWidth, y + this.imageHeight, COLOR_PANEL);

        // スロット枠（入力・出力・プレイヤー在庫）
        drawSlot(guiGraphics, x + 56, y + 35);  // 入力
        drawSlot(guiGraphics, x + 116, y + 35); // 出力
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                drawSlot(guiGraphics, x + 8 + col * 18, y + 84 + row * 18);
            }
        }
        for (int col = 0; col < 9; col++) {
            drawSlot(guiGraphics, x + 8 + col * 18, y + 142);
        }

        // 進捗矢印（入力→出力の間、緑のバーが伸びる）
        int arrowX = x + 80;
        int arrowY = y + 39;
        guiGraphics.fill(arrowX, arrowY, arrowX + ARROW_WIDTH, arrowY + 8, COLOR_BAR_BG);
        int progress = this.menu.getProgressScaled(ARROW_WIDTH);
        if (progress > 0) {
            guiGraphics.fill(arrowX, arrowY, arrowX + progress, arrowY + 8, COLOR_PROGRESS);
        }

        // マナバー（左端、下から上へ溜まる縦バー）
        int barX = x + 12;
        int barTop = y + 17;
        guiGraphics.fill(barX, barTop, barX + 12, barTop + MANA_BAR_HEIGHT, COLOR_BAR_BG);
        int filled = this.menu.getManaScaled(MANA_BAR_HEIGHT);
        if (filled > 0) {
            guiGraphics.fill(barX, barTop + (MANA_BAR_HEIGHT - filled), barX + 12, barTop + MANA_BAR_HEIGHT, COLOR_MANA);
        }
    }

    /** 16x16 アイテム位置に合わせて 18x18 のスロット枠を描く。 */
    private void drawSlot(GuiGraphics guiGraphics, int slotX, int slotY) {
        guiGraphics.fill(slotX - 1, slotY - 1, slotX + 17, slotY + 17, COLOR_SLOT);
        guiGraphics.fill(slotX, slotY, slotX + 16, slotY + 16, COLOR_BAR_BG);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // マナバーにカーソルを合わせたら現在量をツールチップ表示
        int barX = this.leftPos + 12;
        int barTop = this.topPos + 17;
        if (mouseX >= barX && mouseX < barX + 12 && mouseY >= barTop && mouseY < barTop + MANA_BAR_HEIGHT) {
            guiGraphics.renderTooltip(this.font,
                    Component.translatable("tooltip.magitech.mana_stored", this.menu.getManaStored(), this.menu.getMaxMana()),
                    mouseX, mouseY);
        }

        this.renderTooltip(guiGraphics, mouseX, mouseY); // スロットのアイテム説明
    }
}
