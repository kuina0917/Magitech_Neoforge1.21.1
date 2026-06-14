package net.kuina.magitech.client.screen;

import net.kuina.magitech.research.ResearchNode;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * 個別の研究タスク（クエスト）の詳細を表示する画面。
 */
public class ResearchTaskScreen extends Screen {
    private final Screen parent;
    private final ResearchNode node;

    protected ResearchTaskScreen(Screen parent, ResearchNode node) {
        super(Component.translatable(node.getTitleKey()));
        this.parent = parent;
        this.node = node;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 背景の描画
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        // 中央のパネル
        int panelWidth = 220;
        int panelHeight = 160;
        int x = (this.width - panelWidth) / 2;
        int y = (this.height - panelHeight) / 2;

        // 1. 角を丸めたメインパネル背景（深いネイビーグレー）
        drawRoundedRect(guiGraphics, x, y, panelWidth, panelHeight, 10, 0xFF151B22);
        
        // 2. 金色の丸みを帯びた縁取り
        drawRoundedOutline(guiGraphics, x, y, panelWidth, panelHeight, 10, 0xFFD4AF37);

        // タイトル（装飾的な表示）
        guiGraphics.drawCenteredString(this.font, "§6- §r" + this.title.getString() + " §6-", this.width / 2, y + 12, 0xFFFFFF);

        // アイコン（少し大きく強調）
        guiGraphics.renderFakeItem(node.getIcon(), this.width / 2 - 8, y + 32);

        // 説明文
        Component desc = Component.translatable(node.getTitleKey() + ".desc");
        guiGraphics.drawWordWrap(this.font, desc, x + 15, y + 65, panelWidth - 30, 0xDDDDDD);

        // 戻るヒント
        guiGraphics.drawCenteredString(this.font, Component.literal("§7[ESC to Close]"), this.width / 2, y + panelHeight - 18, 0x888888);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    /** 角の丸い矩形を描画するヘルパー */
    private void drawRoundedRect(GuiGraphics guiGraphics, int x, int y, int width, int height, int radius, int color) {
        // メインの中央部分
        guiGraphics.fill(x + radius, y, x + width - radius, y + height, color);
        // 左側のサイド
        guiGraphics.fill(x, y + radius, x + radius, y + height - radius, color);
        // 右側のサイド
        guiGraphics.fill(x + width - radius, y + radius, x + width, y + height - radius, color);
        
        // 四隅の円形を埋める
        drawQuadrant(guiGraphics, x + radius, y + radius, radius, color, 1); // 左上
        drawQuadrant(guiGraphics, x + width - radius, y + radius, radius, color, 2); // 右上
        drawQuadrant(guiGraphics, x + radius, y + height - radius, radius, color, 3); // 左下
        drawQuadrant(guiGraphics, x + width - radius, y + height - radius, radius, color, 4); // 右下
    }

    private void drawRoundedOutline(GuiGraphics guiGraphics, int x, int y, int width, int height, int radius, int color) {
        // 直線部分
        guiGraphics.fill(x + radius, y, x + width - radius, y + 1, color); // 上
        guiGraphics.fill(x + radius, y + height - 1, x + width - radius, y + height, color); // 下
        guiGraphics.fill(x, y + radius, x + 1, y + height - radius, color); // 左
        guiGraphics.fill(x + width - 1, y + radius, x + width, y + height - radius, color); // 右
        
        // 本当は曲線を描画すべきですが、今回は簡易的に角のピクセルを繋ぎます
    }

    private void drawQuadrant(GuiGraphics guiGraphics, int cx, int cy, int radius, int color, int quadrant) {
        for (int i = 0; i < radius; i++) {
            for (int j = 0; j < radius; j++) {
                if (i * i + j * j <= radius * radius) {
                    int px = (quadrant == 2 || quadrant == 4) ? i : -i;
                    int py = (quadrant == 3 || quadrant == 4) ? j : -j;
                    guiGraphics.fill(cx + px, cy + py, cx + px + 1, cy + py + 1, color);
                }
            }
        }
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // ぼかしを入れずに、単純な黒の半透明背景を描画する
        guiGraphics.fill(0, 0, this.width, this.height, 0x88000000);
    }

    public float backgroundBlurValue() {
        return 0.0f; // ぼかし無効化
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { // ESC
            this.minecraft.setScreen(parent);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
