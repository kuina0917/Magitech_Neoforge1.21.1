package net.kuina.magitech.client.screen;

import net.kuina.magitech.menu.ManaExtractorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class ManaExtractorScreen extends AbstractContainerScreen<ManaExtractorMenu> {
    private List<BlockPos> syncPositions = new ArrayList<>();
    private List<Boolean> syncStates = new ArrayList<>();

    public ManaExtractorScreen(ManaExtractorMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 200;
        this.imageHeight = 200;
    }

    public void updateSyncData(List<BlockPos> positions, List<Boolean> states) {
        this.syncPositions = positions;
        this.syncStates = states;
        this.rebuildWidgets(); // リストが変わる可能性があるのでボタンを再構築
    }

    @Override
    protected void init() {
        super.init();
        
        // ターゲットリスト用のボタンを生成
        for (int i = 0; i < syncPositions.size(); i++) {
            final int index = i;
            BlockPos pos = syncPositions.get(i);
            boolean enabled = syncStates.get(i);
            
            String label = String.format("(%d, %d, %d)", pos.getX(), pos.getY(), pos.getZ());
            addRenderableWidget(Button.builder(Component.literal(label), (btn) -> {
                // サーバーへボタンクリック（インデックス）を通知
                if (this.minecraft.gameMode != null) {
                    this.minecraft.gameMode.handleInventoryButtonClick(menu.containerId, index);
                }
            })
            .bounds(leftPos + 10, topPos + 30 + (i * 22), 120, 20)
            .build());
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        guiGraphics.drawCenteredString(this.font, Component.translatable("gui.magitech.extractor.targets"), leftPos + imageWidth / 2, topPos + 10, 0xFFFFFF);

        // ON/OFF 状態の描画
        for (int i = 0; i < syncStates.size(); i++) {
            boolean active = syncStates.get(i);
            guiGraphics.drawString(this.font, active ? "§aCONNECTED" : "§7DISCONNECTED", leftPos + 135, topPos + 36 + (i * 22), 0xFFFFFF);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        // 装飾的な魔法風背景
        guiGraphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF151B22);
        guiGraphics.renderOutline(leftPos, topPos, imageWidth, imageHeight, 0xFFD4AF37);
    }
}
