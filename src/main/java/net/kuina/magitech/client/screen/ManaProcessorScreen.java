package net.kuina.magitech.client.screen;

import net.kuina.magitech.menu.ManaProcessorMenu;
import net.kuina.magitech.util.ModUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * マナ加工機の GUI 画面。
 *
 * <p>背景・スロット枠・矢印は {@code textures/gui/mana_processor.png}（176x166）を
 * そのまま貼り付けて描画する。マナバーは {@code textures/gui/mana_bar.png}（12x48）を
 * 下から溜まるように部分描画し、進捗矢印には半透明の緑を重ねて進行度を表す。</p>
 */
public class ManaProcessorScreen extends AbstractContainerScreen<ManaProcessorMenu> {

    /** 背景テクスチャ（GUI 全体、176x166）。 */
    private static final ResourceLocation BG_TEXTURE = ModUtil.rl("textures/gui/mana_processor.png");
    /** マナバーの中身テクスチャ（12x48）。 */
    private static final ResourceLocation MANA_BAR_TEXTURE = ModUtil.rl("textures/gui/mana_bar.png");
    /** バニラのかまどテクスチャ（256x256）。空の矢印（下地）を流用する。 */
    private static final ResourceLocation FURNACE_TEXTURE = ModUtil.mc("textures/gui/container/furnace.png");
    /** かまどの「焼成中の矢印」スプライト（1.21 ではスプライト化されている）。色付けして進捗表示に使う。 */
    private static final ResourceLocation BURN_PROGRESS_SPRITE = ModUtil.mc("container/furnace/burn_progress");

    // マナバーの空枠は背景テクスチャに描かれている。中身を流し込む領域（GUI 左上からの相対座標）。
    private static final int MANA_BAR_X = 14;
    private static final int MANA_BAR_Y = 18;
    private static final int MANA_BAR_WIDTH = 12;
    private static final int MANA_BAR_HEIGHT = 48;

    // 進捗矢印を描く位置（GUI 左上からの相対座標）。かまどの矢印（24x16）を流用する。
    private static final int ARROW_X = 79;
    private static final int ARROW_Y = 34;
    private static final int ARROW_WIDTH = 24;
    private static final int ARROW_HEIGHT = 16;
    // かまどテクスチャ内の UV：空の矢印 (79,34) を下地として使う。
    private static final int ARROW_EMPTY_U = 79;
    private static final int ARROW_EMPTY_V = 34;
    // 進行中の矢印に重ねる色（マナバーと同じ水色 0x29B6F6）。スプライトに乗算で着色される。
    private static final float PROGRESS_R = 0x29 / 255.0f;
    private static final float PROGRESS_G = 0xB6 / 255.0f;
    private static final float PROGRESS_B = 0xF6 / 255.0f;

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

        // 背景（パネル・スロット枠・矢印・マナバーの空枠がすべて含まれる）
        guiGraphics.blit(BG_TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

        // マナバー：下から上へ溜まるように、テクスチャの下側 filled ピクセル分だけを描く
        int filled = this.menu.getManaScaled(MANA_BAR_HEIGHT);
        if (filled > 0) {
            int drawY = y + MANA_BAR_Y + (MANA_BAR_HEIGHT - filled);
            int v = MANA_BAR_HEIGHT - filled;
            guiGraphics.blit(MANA_BAR_TEXTURE, x + MANA_BAR_X, drawY, 0, v,
                    MANA_BAR_WIDTH, filled, MANA_BAR_WIDTH, MANA_BAR_HEIGHT);
        }

        // 進捗矢印：まず空の矢印（グレー）を下地として描く
        int arrowX = x + ARROW_X;
        int arrowY = y + ARROW_Y;
        guiGraphics.blit(FURNACE_TEXTURE, arrowX, arrowY, ARROW_EMPTY_U, ARROW_EMPTY_V,
                ARROW_WIDTH, ARROW_HEIGHT, 256, 256);
        // その上に、進行度ぶんだけ「焼成中の矢印」スプライトを白色に着色（1.0fで元の色を維持）して左から重ねる
        int progress = this.menu.getProgressScaled(ARROW_WIDTH);
        if (progress > 0) {
            guiGraphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
            guiGraphics.blitSprite(BURN_PROGRESS_SPRITE, ARROW_WIDTH, ARROW_HEIGHT, 0, 0,
                    arrowX, arrowY, progress, ARROW_HEIGHT);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // マナバーにカーソルを合わせたら現在量をツールチップ表示
        int barX = this.leftPos + MANA_BAR_X;
        int barTop = this.topPos + MANA_BAR_Y;
        if (mouseX >= barX && mouseX < barX + MANA_BAR_WIDTH && mouseY >= barTop && mouseY < barTop + MANA_BAR_HEIGHT) {
            guiGraphics.renderTooltip(this.font,
                    Component.translatable("tooltip.magitech.mana_stored", this.menu.getManaStored(), this.menu.getMaxMana()),
                    mouseX, mouseY);
        }

        this.renderTooltip(guiGraphics, mouseX, mouseY); // スロットのアイテム説明
    }
}
