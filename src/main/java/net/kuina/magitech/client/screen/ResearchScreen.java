package net.kuina.magitech.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.kuina.magitech.research.ResearchManager;
import net.kuina.magitech.research.ResearchNode;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * FTB Quests 風の研究ツリー画面（銀河背景）。
 */
public class ResearchScreen extends Screen {
    private double scrollX = 0;
    private double scrollY = 0;
    private static final int NODE_SIZE = 32;

    public ResearchScreen() {
        super(Component.translatable("gui.magitech.research_tree"));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 銀河風の背景描画
        renderGalaxyBackground(guiGraphics);
        
        // グリッド背景の描画
        drawGrid(guiGraphics);

        // ノード間の線を描画
        for (ResearchNode node : ResearchManager.getNodes().values()) {
            for (String parentId : node.getParents()) {
                ResearchNode parent = ResearchManager.getNodes().get(parentId);
                if (parent != null) {
                    drawLine(guiGraphics, parent, node);
                }
            }
        }

        // ノードを描画
        for (ResearchNode node : ResearchManager.getNodes().values()) {
            drawNode(guiGraphics, node, mouseX, mouseY);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // ぼかし無効化のため空にする（render内で自前描画）
    }

    private void renderGalaxyBackground(GuiGraphics guiGraphics) {
        // 1. 真っ暗すぎない、目に優しい深い紺色のベース
        guiGraphics.fill(0, 0, this.width, this.height, 0xFF0B1015);

        // 2. プロシーチャルな星雲（グラデーションで表現し、テクスチャエラーを回避）
        // 左上から右下へ、うっすらとした青緑のグラデーション
        guiGraphics.fillGradient(0, 0, this.width, this.height, 0x33004444, 0x11002222);

        // 3. 控えめな星々（白ではなく少し青み・黄色みを入れた柔らかい点）
        long time = System.currentTimeMillis();
        java.util.Random random = new java.util.Random(42);
        for (int i = 0; i < 120; i++) {
            int x = random.nextInt(2000);
            int y = random.nextInt(2000);
            
            int sx = (int) ((x + scrollX * 0.4) % this.width);
            int sy = (int) ((y + scrollY * 0.4) % this.height);
            if (sx < 0) sx += this.width;
            if (sy < 0) sy += this.height;

            float twinkle = 0.3f + (float) Math.sin((time / 600.0) + i) * 0.2f;
            int alpha = (int) (twinkle * 255);
            int starColor = i % 3 == 0 ? 0xCCE0F0 : (i % 3 == 1 ? 0xFFF5E0 : 0xFFFFFF);
            int color = (alpha << 24) | starColor;
            
            guiGraphics.fill(sx, sy, sx + 1, sy + 1, color);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) { // 左クリック
            for (ResearchNode node : ResearchManager.getNodes().values()) {
                int renderX = (int) (this.width / 2 + node.getX() + scrollX);
                int renderY = (int) (this.height / 2 + node.getY() + scrollY);
                if (mouseX >= renderX && mouseX < renderX + NODE_SIZE && mouseY >= renderY && mouseY < renderY + NODE_SIZE) {
                    // ノードがクリックされたら詳細画面を開く
                    this.minecraft.setScreen(new ResearchTaskScreen(this, node));
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public float backgroundBlurValue() {
        return 0.0f;
    }

    private void drawGrid(GuiGraphics guiGraphics) {
        int gridSize = 32;
        int offX = (int) (scrollX % gridSize);
        int offY = (int) (scrollY % gridSize);

        for (int x = offX; x < this.width; x += gridSize) {
            guiGraphics.fill(x, 0, x + 1, this.height, 0x22AAAAAA);
        }
        for (int y = offY; y < this.height; y += gridSize) {
            guiGraphics.fill(0, y, this.width, y + 1, 0x22AAAAAA);
        }
    }

    private void drawLine(GuiGraphics guiGraphics, ResearchNode from, ResearchNode to) {
        int x1 = (int) (this.width / 2 + from.getX() + scrollX + NODE_SIZE / 2);
        int y1 = (int) (this.height / 2 + from.getY() + scrollY + NODE_SIZE / 2);
        int x2 = (int) (this.width / 2 + to.getX() + scrollX + NODE_SIZE / 2);
        int y2 = (int) (this.height / 2 + to.getY() + scrollY + NODE_SIZE / 2);
        
        int color = 0x88AAAAAA;
        if (x1 == x2) {
            guiGraphics.fill(x1 - 1, Math.min(y1, y2), x1 + 1, Math.max(y1, y2), color);
        } else if (y1 == y2) {
            guiGraphics.fill(Math.min(x1, x2), y1 - 1, Math.max(x1, x2), y1 + 1, color);
        } else {
            guiGraphics.fill(Math.min(x1, x2), y1 - 1, Math.max(x1, x2), y1 + 1, color);
            guiGraphics.fill(x2 - 1, Math.min(y1, y2), x2 + 1, Math.max(y1, y2), color);
        }
    }

    private void drawNode(GuiGraphics guiGraphics, ResearchNode node, int mouseX, int mouseY) {
        int renderX = (int) (this.width / 2 + node.getX() + scrollX);
        int renderY = (int) (this.height / 2 + node.getY() + scrollY);
        int centerX = renderX + NODE_SIZE / 2;
        int centerY = renderY + NODE_SIZE / 2;

        boolean hovered = mouseX >= renderX && mouseX < renderX + NODE_SIZE && mouseY >= renderY && mouseY < renderY + NODE_SIZE;
        
        // 1. ノードの外枠（丸い魔法陣のような縁取り）
        int outerColor = hovered ? 0xFFFFD700 : 0xFFB8860B; // 金色 or 暗い金色
        drawCircle(guiGraphics, centerX, centerY, NODE_SIZE / 2 + 1, outerColor);
        
        // 2. ノードの背景（丸い黒背景）
        int bgColor = hovered ? 0xFF333333 : 0xFF111111;
        drawCircle(guiGraphics, centerX, centerY, NODE_SIZE / 2, bgColor);

        // アイコン
        guiGraphics.renderFakeItem(node.getIcon(), renderX + 8, renderY + 8);

        if (hovered) {
            guiGraphics.renderTooltip(this.font, Component.translatable(node.getTitleKey()), mouseX, mouseY);
        }
    }

    /** 簡易的な円描画ヘルパー（fillを組み合わせて作成） */
    private void drawCircle(GuiGraphics guiGraphics, int x, int y, int radius, int color) {
        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                if (i * i + j * j <= radius * radius) {
                    guiGraphics.fill(x + i, y + j, x + i + 1, y + j + 1, color);
                }
            }
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0) {
            this.scrollX += dragX;
            this.scrollY += dragY;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
