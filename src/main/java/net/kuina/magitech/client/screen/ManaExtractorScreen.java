package net.kuina.magitech.client.screen;

import net.kuina.magitech.menu.ManaExtractorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class ManaExtractorScreen extends AbstractContainerScreen<ManaExtractorMenu> {
    private List<BlockPos> syncPositions = new ArrayList<>();
    private List<Boolean> syncStates = new ArrayList<>();
    private List<Component> addressNames = new ArrayList<>();
    private long manaStored = 0;
    private long manaGenRate = 0;
    private long manaTransferRate = 0;

    private int selectedTab = 0;
    private int scrollOffset = 0;

    private static final long MAX_CAPACITY = 10_000L;
    private static final int R = 6;
    private static final int TAB_H = 18;
    private static final int TAB_OVER = 7;

    public ManaExtractorScreen(ManaExtractorMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 222;
        this.imageHeight = 210;
    }

    public void updateSyncData(List<BlockPos> positions, List<Boolean> states,
                               long manaStored, long manaGenRate, long manaTransferRate) {
        this.syncPositions = positions;
        this.syncStates = states;
        this.manaStored = manaStored;
        this.manaGenRate = manaGenRate;
        this.manaTransferRate = manaTransferRate;
        resolveAddressNames();
        this.rebuildWidgets();
    }

    private void resolveAddressNames() {
        addressNames.clear();
        for (BlockPos pos : syncPositions) {
            if (minecraft != null && minecraft.level != null) {
                BlockState state = minecraft.level.getBlockState(pos);
                addressNames.add(state.getBlock().getName());
            } else {
                addressNames.add(Component.literal(String.format("%d, %d, %d", pos.getX(), pos.getY(), pos.getZ())));
            }
        }
    }

    @Override
    protected void init() {
        super.init();
        resolveAddressNames();
        rebuildAddressWidgets();
    }

    private void rebuildAddressWidgets() {
        if (selectedTab == 1) {
            int visibleRows = (imageHeight - 24) / 20;
            int end = Math.min(syncPositions.size(), scrollOffset + visibleRows);
            for (int i = scrollOffset; i < end; i++) {
                final int index = i;
                boolean active = i < syncStates.size() && syncStates.get(i);
                BlockPos pos = syncPositions.get(i);
                String coord = String.format("[%d, %d, %d]", pos.getX(), pos.getY(), pos.getZ());
                String name = i < addressNames.size() ? addressNames.get(i).getString() : "?";
                String btnText = slimText(
                        (active ? "\u2605 " : "\u2606 ") + coord + " " + name,
                        imageWidth - 24);
                addRenderableWidget(Button.builder(Component.literal(btnText), (btn) -> {
                    if (this.minecraft.gameMode != null) {
                        this.minecraft.gameMode.handleInventoryButtonClick(menu.containerId, index);
                    }
                })
                .bounds(leftPos + 10, topPos + 14 + (i - scrollOffset) * 20, imageWidth - 20, 16)
                .build());
            }
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g, mouseX, mouseY, partialTick);
        super.render(g, mouseX, mouseY, partialTick);
        this.renderTooltip(g, mouseX, mouseY);

        drawBackgroundGlow(g);

        if (selectedTab == 0) {
            drawManaInfo(g);
        } else {
            drawAddressContent(g);
        }

        drawTabs(g);
    }

    // ─── rounded rect ───────────────────────────────────────────────────

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

    // ─── glow ───────────────────────────────────────────────────────────

    private void drawBackgroundGlow(GuiGraphics g) {
        fillRoundRect(g, leftPos - 1, topPos - 1, imageWidth + 2, imageHeight + 2, 0x226622AA);
        fillRoundRect(g, leftPos - 2, topPos - 2, imageWidth + 4, imageHeight + 4, 0x0C6622AA);
    }

    // ─── tabs ───────────────────────────────────────────────────────────

    private void drawTabs(GuiGraphics g) {
        int tabY = topPos - TAB_OVER;
        String[][] labels = {
                {"\u2728", Component.translatable("block.magitech.mana_extractor_core").getString()},
                {"\u2728", Component.translatable("gui.magitech.extractor.targets").getString()},
        };
        int[] tws = new int[2];
        for (int i = 0; i < 2; i++) {
            tws[i] = font.width(labels[i][0] + " " + labels[i][1]) + 16;
        }
        int gap = 2;
        int totalW = tws[0] + gap + tws[1];
        int startX = leftPos + (imageWidth - totalW) / 2;

        for (int i = 0; i < 2; i++) {
            int x = startX + (i == 0 ? 0 : tws[0] + gap);
            int w = tws[i];
            boolean active = selectedTab == i;

            if (active) {
                g.fill(x + R, tabY, x + w - R, tabY + TAB_H, 0xFF1E0038);
                g.fill(x, tabY + R, x + w, tabY + TAB_H, 0xFF1E0038);
                g.fill(x + R, tabY, x + w - R, tabY + 1, 0xFFAA66FF);
                g.fill(x, tabY + R, x + 1, tabY + TAB_H, 0xFFAA66FF);
                g.fill(x + w - 1, tabY + R, x + w, tabY + TAB_H, 0xFFAA66FF);
                g.fill(x + R - 1, tabY, x + R + 1, tabY + 1, 0xFFAA66FF);
                g.fill(x + w - R - 1, tabY, x + w - R + 1, tabY + 1, 0xFFAA66FF);
                g.drawCenteredString(this.font, labels[i][0] + " " + labels[i][1],
                        x + w / 2, tabY + (TAB_H - 8) / 2 + 1, 0xFFFFD700);
            } else {
                g.fill(x + R, tabY, x + w - R, tabY + TAB_H, 0xFF100024);
                g.fill(x, tabY + R, x + w, tabY + TAB_H - R, 0xFF100024);
                g.fill(x + R, tabY, x + w - R, tabY + 1, 0xFF8855AA);
                g.fill(x + R, tabY + TAB_H - 1, x + w - R, tabY + TAB_H, 0xFF8855AA);
                g.fill(x, tabY + R, x + 1, tabY + TAB_H - R, 0xFF8855AA);
                g.fill(x + w - 1, tabY + R, x + w, tabY + TAB_H - R, 0xFF8855AA);
                int d = 1;
                g.fill(x + R - d, tabY, x + R + d, tabY + 1, 0xFF8855AA);
                g.fill(x + w - R - d, tabY, x + w - R + d, tabY + 1, 0xFF8855AA);
                g.fill(x + R - d, tabY + TAB_H - 1, x + R + d, tabY + TAB_H, 0xFF8855AA);
                g.fill(x + w - R - d, tabY + TAB_H - 1, x + w - R + d, tabY + TAB_H, 0xFF8855AA);
                g.fill(x, tabY + R - d, x + 1, tabY + R + d, 0xFF8855AA);
                g.fill(x + w - 1, tabY + R - d, x + w, tabY + R + d, 0xFF8855AA);
                g.fill(x, tabY + TAB_H - R - d, x + 1, tabY + TAB_H - R + d, 0xFF8855AA);
                g.fill(x + w - 1, tabY + TAB_H - R - d, x + w, tabY + TAB_H - R + d, 0xFF8855AA);
                g.drawCenteredString(this.font, labels[i][0] + " " + labels[i][1],
                        x + w / 2, tabY + (TAB_H - 8) / 2 + 1, 0xFF8855AA);
            }
        }
    }

    // ─── page: mana core ────────────────────────────────────────────────

    private void drawManaInfo(GuiGraphics g) {
        int cx = leftPos + imageWidth / 2;

        int barX = leftPos + 16;
        int barW = imageWidth - 32;
        int barY = topPos + 14;
        int barH = 20;

        g.fill(barX - 2, barY - 2, barX + barW + 2, barY + barH + 2, 0x226622AA);
        g.fill(barX + R, barY, barX + barW - R, barY + barH, 0xFF100024);
        g.fill(barX, barY + R, barX + barW, barY + barH - R, 0xFF100024);

        long stored = Math.min(manaStored, MAX_CAPACITY);
        int filled = MAX_CAPACITY > 0 ? (int) ((double) stored / MAX_CAPACITY * barW) : 0;
        if (filled > 0) {
            int fc;
            double ratio = (double) stored / MAX_CAPACITY;
            if (ratio > 0.7) fc = 0xFF00FFAA;
            else if (ratio > 0.4) fc = 0xFF44DDFF;
            else fc = 0xFF00BFFF;

            g.fill(barX + R, barY, barX + filled - R, barY + barH, fc);
            g.fill(barX, barY + R, barX + filled, barY + barH - R, fc);
            if (filled > R) {
                g.fill(barX, barY, barX + R, barY + R, fc);
                g.fill(barX + filled - R, barY, barX + filled, barY + R, fc);
                g.fill(barX, barY + barH - R, barX + R, barY + barH, fc);
                g.fill(barX + filled - R, barY + barH - R, barX + filled, barY + barH, fc);
            } else {
                g.fill(barX, barY, barX + filled, barY + barH, fc);
            }
        }

        int brd = 0xFF9944FF;
        g.fill(barX + R, barY, barX + barW - R, barY + 1, brd);
        g.fill(barX + R, barY + barH - 1, barX + barW - R, barY + barH, brd);
        g.fill(barX, barY + R, barX + 1, barY + barH - R, brd);
        g.fill(barX + barW - 1, barY + R, barX + barW, barY + barH - R, brd);
        g.fill(barX + R - 1, barY, barX + R + 1, barY + 1, brd);
        g.fill(barX + barW - R - 1, barY, barX + barW - R + 1, barY + 1, brd);
        g.fill(barX + R - 1, barY + barH - 1, barX + R + 1, barY + barH, brd);
        g.fill(barX + barW - R - 1, barY + barH - 1, barX + barW - R + 1, barY + barH, brd);
        g.fill(barX, barY + R - 1, barX + 1, barY + R + 1, brd);
        g.fill(barX, barY + barH - R - 1, barX + 1, barY + barH - R + 1, brd);
        g.fill(barX + barW - 1, barY + R - 1, barX + barW, barY + R + 1, brd);
        g.fill(barX + barW - 1, barY + barH - R - 1, barX + barW, barY + barH - R + 1, brd);

        String manaText = String.format("%,d / %,d", manaStored, MAX_CAPACITY);
        g.drawCenteredString(this.font, manaText,
                barX + barW / 2, barY + (barH - 8) / 2, 0xFFE0E0FF);

        int statY = barY + barH + 14;
        g.drawCenteredString(this.font,
                Component.translatable("gui.magitech.extractor.generating", manaGenRate),
                cx, statY, 0xFF88FFBB);
        g.drawCenteredString(this.font,
                Component.translatable("gui.magitech.extractor.transferring", manaTransferRate),
                cx, statY + 12, 0xFFFFAA66);
    }

    // ─── page: addresses ────────────────────────────────────────────────

    private void drawAddressContent(GuiGraphics g) {
    }

    private String slimText(String text, int maxPx) {
        if (font == null) return text;
        if (font.width(text) <= maxPx) return text;
        while (!text.isEmpty() && font.width(text + "\u2026") > maxPx) {
            text = text.substring(0, text.length() - 1);
        }
        return text + "\u2026";
    }

    // ─── input ──────────────────────────────────────────────────────────

    @Override
    public boolean mouseScrolled(double mx, double my, double scrollX, double scrollY) {
        if (selectedTab == 1 && !syncPositions.isEmpty()) {
            int visibleRows = (imageHeight - 24) / 20;
            int maxScroll = Math.max(0, syncPositions.size() - visibleRows);
            int prev = scrollOffset;
            scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset - (int) Math.round(scrollY)));
            if (scrollOffset != prev) {
                this.rebuildWidgets();
            }
            return true;
        }
        return super.mouseScrolled(mx, my, scrollX, scrollY);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        int tabY = topPos - TAB_OVER;
        if (my >= tabY && my < tabY + TAB_H) {
            String[][] labels = {
                    {"\u2728", Component.translatable("block.magitech.mana_extractor_core").getString()},
                    {"\u2728", Component.translatable("gui.magitech.extractor.targets").getString()},
            };
            int[] tws = new int[2];
            for (int i = 0; i < 2; i++) {
                tws[i] = font.width(labels[i][0] + " " + labels[i][1]) + 16;
            }
            int gap = 2;
            int totalW = tws[0] + gap + tws[1];
            int startX = leftPos + (imageWidth - totalW) / 2;

            for (int i = 0; i < 2; i++) {
                int tx = startX + (i == 0 ? 0 : tws[0] + gap);
                if (mx >= tx && mx < tx + tws[i]) {
                    if (selectedTab != i) {
                        selectedTab = i;
                        this.rebuildWidgets();
                    }
                    return true;
                }
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mx, int my) {
    }

    @Override
    protected void renderBg(GuiGraphics g, float pt, int mx, int my) {
        fillRoundRect(g, leftPos, topPos, imageWidth, imageHeight, 0xFF1E0038);
        outlineRoundRect(g, leftPos, topPos, imageWidth, imageHeight, 0xFFAA66FF);
    }
}
