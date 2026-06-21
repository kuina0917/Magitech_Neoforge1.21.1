package net.kuina.magitech.client.screen;

import net.kuina.magitech.component.MagitechDataComponents;
import net.kuina.magitech.network.c2s.PlaceRelicPayload;
import net.kuina.magitech.network.c2s.RemoveRelicPayload;
import net.kuina.magitech.relic.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RelicBoardScreen extends Screen {

    private static final int CELL_SIZE = 28;
    private static final int CELL_GAP = 2;
    private static final int BOARD_PADDING = 12;
    private static final int PANEL_PADDING = 8;

    private RelicBoard board;
    private int gridLeft;
    private int gridTop;
    private int gridPixelSize;

    private final List<RelicSlot> inventoryRelics = new ArrayList<>();
    private int selectedSlotIndex = -1;
    private int hoveredSlotIndex = -1;
    private int ghostRotation = 0;
    private boolean isPlacing = false;

    private record RelicSlot(int slotIndex, ItemStack stack, RelicData data) {}

    public RelicBoardScreen(RelicBoard board) {
        super(Component.translatable("gui.magitech.relic_board.title"));
        this.board = board;
    }

    @Override
    protected void init() {
        super.init();
        recalcLayout();
        scanInventory();
    }

    private void recalcLayout() {
        int boardW = board.getSize() * (CELL_SIZE + CELL_GAP) - CELL_GAP + BOARD_PADDING * 2;
        int boardH = board.getSize() * (CELL_SIZE + CELL_GAP) - CELL_GAP + BOARD_PADDING * 2;
        gridLeft = (width - boardW) / 2;
        gridTop = (height - boardH) / 2 - 20;
        gridPixelSize = board.getSize() * (CELL_SIZE + CELL_GAP);
    }

    private void scanInventory() {
        inventoryRelics.clear();
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        for (int i = 0; i < player.getInventory().items.size(); i++) {
            ItemStack stack = player.getInventory().items.get(i);
            if (stack.isEmpty()) continue;
            RelicData data = stack.get(MagitechDataComponents.RELIC_DATA.get());
            if (data != null) {
                inventoryRelics.add(new RelicSlot(i, stack, data));
            }
        }
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        renderBackground(gui, mouseX, mouseY, partialTick);
        drawTitle(gui);
        drawBoard(gui, mouseX, mouseY);
        drawSynergies(gui);
        drawEffects(gui);
        drawInventoryPanel(gui, mouseX, mouseY);
        drawGhostPreview(gui, mouseX, mouseY);
        drawTooltips(gui, mouseX, mouseY);
        drawControls(gui);
    }

    @Override
    public void renderBackground(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        gui.fill(0, 0, width, height, 0xCC0B1015);
    }

    private void drawTitle(GuiGraphics gui) {
        Component title = Component.translatable("gui.magitech.relic_board.title");
        int titleW = font.width(title);
        gui.drawString(font, title, (width - titleW) / 2, gridTop - 30, 0xFFD4A574, false);

        String sizeStr = board.getSize() + "x" + board.getSize();
        int sizeW = font.width(sizeStr);
        gui.drawString(font, sizeStr, (width - sizeW) / 2, gridTop - 16, 0xFF888888, false);
    }

    private void drawBoard(GuiGraphics gui, int mouseX, int mouseY) {
        int px = gridLeft + BOARD_PADDING;
        int py = gridTop + BOARD_PADDING;
        int cellStep = CELL_SIZE + CELL_GAP;
        int boardSize = board.getSize();

        for (int gy = 0; gy < boardSize; gy++) {
            for (int gx = 0; gx < boardSize; gx++) {
                int cx = px + gx * cellStep;
                int cy = py + gy * cellStep;

                PlacedRelic relic = board.getRelicAt(gx, gy);
                if (relic != null) {
                    drawCell(gui, cx, cy, getElementColor(relic.data().element()), true);
                } else {
                    drawCell(gui, cx, cy, 0xFF333333, false);
                }
            }
        }

        int totalW = boardSize * cellStep - CELL_GAP;
        int totalH = boardSize * cellStep - CELL_GAP;
        gui.fill(px - 1, py - 1, px + totalW + 1, py, 0xFF555555);
        gui.fill(px - 1, py + totalH, px + totalW + 1, py + totalH + 1, 0xFF555555);
        gui.fill(px - 1, py, px, py + totalH, 0xFF555555);
        gui.fill(px + totalW, py, px + totalW + 1, py + totalH, 0xFF555555);
    }

    private void drawCell(GuiGraphics gui, int x, int y, int color, boolean occupied) {
        if (occupied) {
            gui.fill(x, y, x + CELL_SIZE, y + CELL_SIZE, color);
            gui.fill(x + 1, y + 1, x + CELL_SIZE - 1, y + CELL_SIZE - 1, lighten(color, 0.3f));
        } else {
            gui.fill(x, y, x + CELL_SIZE, y + CELL_SIZE, color);
        }
    }

    private void drawSynergies(GuiGraphics gui) {
        List<Synergy> synergies = board.getActiveSynergies();
        if (synergies.isEmpty()) return;

        int x = gridLeft - 120;
        int y = gridTop + BOARD_PADDING;

        Component header = Component.translatable("gui.magitech.relic_board.synergies");
        gui.drawString(font, header, x, y - 10, 0xFFD4A574, false);

        for (int i = 0; i < synergies.size(); i++) {
            Synergy s = synergies.get(i);
            Component name = Component.translatable(s.displayName());
            gui.drawString(font, name, x, y + i * 12, 0xFF88CC88, false);
        }
    }

    private void drawEffects(GuiGraphics gui) {
        Map<RelicEffectType, Float> effects = board.getTotalEffects();
        if (effects.isEmpty()) return;

        int y = gridTop + BOARD_PADDING + board.getSize() * (CELL_SIZE + CELL_GAP) + 20;

        Component header = Component.translatable("gui.magitech.relic_board.effects");
        gui.drawString(font, header, gridLeft, y, 0xFFD4A574, false);

        int x = gridLeft + 4;
        y += 12;
        int count = 0;
        for (var entry : effects.entrySet()) {
            if (entry.getValue() == 0f) continue;
            String text = formatEffectShort(entry.getKey(), entry.getValue());
            gui.drawString(font, text, x + (count % 3) * 110, y + (count / 3) * 12, 0xFFCCCCCC, false);
            count++;
        }
    }

    private void drawInventoryPanel(GuiGraphics gui, int mouseX, int mouseY) {
        int invY = gridTop + BOARD_PADDING + board.getSize() * (CELL_SIZE + CELL_GAP) + 70;
        int invX = gridLeft;

        Component header = Component.translatable("gui.magitech.relic_board.inventory");
        gui.drawString(font, header, invX, invY - 12, 0xFFD4A574, false);

        if (inventoryRelics.isEmpty()) {
            Component empty = Component.translatable("gui.magitech.relic_board.no_relics");
            gui.drawString(font, empty, invX + 4, invY + 8, 0xFF666666, false);
            return;
        }

        int x = invX + 2;
        int y = invY + 2;
        hoveredSlotIndex = -1;

        for (int i = 0; i < inventoryRelics.size(); i++) {
            RelicSlot slot = inventoryRelics.get(i);

            int slotX = x + (i % 7) * 34;
            int slotY = y + (i / 7) * 34;

            boolean hovered = mouseX >= slotX && mouseX < slotX + 30 && mouseY >= slotY && mouseY < slotY + 30;
            boolean isSelected = (selectedSlotIndex == i);
            int bgColor = isSelected ? 0xFF665522 : (hovered ? 0xFF555555 : 0xFF333333);
            gui.fill(slotX, slotY, slotX + 30, slotY + 30, bgColor);

            if (isSelected) {
                gui.fill(slotX, slotY, slotX + 30, slotY + 1, 0xFFD4A574);
                gui.fill(slotX, slotY + 29, slotX + 30, slotY + 30, 0xFFD4A574);
                gui.fill(slotX, slotY, slotX + 1, slotY + 30, 0xFFD4A574);
                gui.fill(slotX + 29, slotY, slotX + 30, slotY + 30, 0xFFD4A574);
            }

            if (hovered) {
                hoveredSlotIndex = i;
            }

            drawMiniShape(gui, slotX + 2, slotY + 2, slot.data(), getElementColor(slot.data().element()));
        }
    }

    private void drawMiniShape(GuiGraphics gui, int x, int y, RelicData data, int color) {
        RelicShape shape = data.shape();
        int cellSize = 6;
        for (int dy = 0; dy < shape.height(); dy++) {
            for (int dx = 0; dx < shape.width(); dx++) {
                if (shape.get(dx, dy)) {
                    int cellX = x + dx * cellSize;
                    int cellY = y + dy * cellSize;
                    gui.fill(cellX, cellY, cellX + cellSize - 1, cellY + cellSize - 1, color);
                }
            }
        }
    }

    private void drawGhostPreview(GuiGraphics gui, int mouseX, int mouseY) {
        if (!isPlacing) return;

        RelicSlot selected = getSelectedSlot();
        if (selected == null) {
            isPlacing = false;
            return;
        }

        int px = gridLeft + BOARD_PADDING;
        int py = gridTop + BOARD_PADDING;
        int cellStep = CELL_SIZE + CELL_GAP;

        int gridGx = (mouseX - px) / cellStep;
        int gridGy = (mouseY - py) / cellStep;

        if (gridGx < 0 || gridGx >= board.getSize() || gridGy < 0 || gridGy >= board.getSize()) {
            return;
        }

        PlacedRelic preview = new PlacedRelic(selected.data(), gridGx, gridGy, ghostRotation);
        RelicShape shape = preview.getRotatedShape();

        boolean canPlace = board.canPlace(preview);
        int color = getElementColor(selected.data().element());
        int previewColor = canPlace ? (color & 0x00FFFFFF) | 0x88000000 : 0x66FF4444;

        for (int dy = 0; dy < shape.height(); dy++) {
            for (int dx = 0; dx < shape.width(); dx++) {
                if (!shape.get(dx, dy)) continue;
                int cellX = px + (gridGx + dx) * cellStep;
                int cellY = py + (gridGy + dy) * cellStep;
                gui.fill(cellX, cellY, cellX + CELL_SIZE, cellY + CELL_SIZE, previewColor);
                gui.fill(cellX + 1, cellY + 1, cellX + CELL_SIZE - 1, cellY + CELL_SIZE - 1, lighten(previewColor, 0.2f));
            }
        }
    }

    private void drawTooltips(GuiGraphics gui, int mouseX, int mouseY) {
        int px = gridLeft + BOARD_PADDING;
        int py = gridTop + BOARD_PADDING;
        int cellStep = CELL_SIZE + CELL_GAP;

        int gridGx = (mouseX - px) / cellStep;
        int gridGy = (mouseY - py) / cellStep;

        if (gridGx >= 0 && gridGx < board.getSize() && gridGy >= 0 && gridGy < board.getSize()) {
            int cellX = px + gridGx * cellStep;
            int cellY = py + gridGy * cellStep;

            if (mouseX >= cellX && mouseX < cellX + CELL_SIZE && mouseY >= cellY && mouseY < cellY + CELL_SIZE) {
                PlacedRelic relic = board.getRelicAt(gridGx, gridGy);
                if (relic != null) {
                    List<Component> tooltip = new ArrayList<>();
                    tooltip.add(Component.translatable(relic.data().getDisplayName()));
                    tooltip.add(Component.translatable("rarity.magitech." + relic.data().rarity().getSerializedName()));
                    for (var e : relic.data().mainEffects().entrySet()) {
                        tooltip.add(Component.literal("  " + formatEffectShort(e.getKey(), e.getValue())));
                    }
                    gui.renderComponentTooltip(font, tooltip, mouseX, mouseY);
                }
            }
        }
    }

    private void drawControls(GuiGraphics gui) {
        int y = height - 20;
        gui.drawString(font, Component.translatable("gui.magitech.relic_board.controls"),
            gridLeft, y, 0xFF888888, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (tryClickBoard((int) mouseX, (int) mouseY)) return true;
            if (tryClickInventory((int) mouseX, (int) mouseY)) return true;
        }
        if (button == 1) {
            if (tryRemoveBoard((int) mouseX, (int) mouseY)) return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean tryClickBoard(int mouseX, int mouseY) {
        int px = gridLeft + BOARD_PADDING;
        int py = gridTop + BOARD_PADDING;
        int cellStep = CELL_SIZE + CELL_GAP;

        int gridGx = (mouseX - px) / cellStep;
        int gridGy = (mouseY - py) / cellStep;

        if (gridGx < 0 || gridGx >= board.getSize() || gridGy < 0 || gridGy >= board.getSize()) {
            return false;
        }

        int cellX = px + gridGx * cellStep;
        int cellY = py + gridGy * cellStep;
        if (mouseX < cellX || mouseX >= cellX + CELL_SIZE || mouseY < cellY || mouseY >= cellY + CELL_SIZE) {
            return false;
        }

        if (!isPlacing) return false;

        RelicSlot selected = getSelectedSlot();
        if (selected == null) {
            isPlacing = false;
            return false;
        }

        PlacedRelic placed = new PlacedRelic(selected.data(), gridGx, gridGy, ghostRotation);
        if (!board.canPlace(placed)) return false;

        net.neoforged.neoforge.network.PacketDistributor.sendToServer(new PlaceRelicPayload(selected.slotIndex(), gridGx, gridGy, ghostRotation));
        board.place(placed);
        inventoryRelics.remove(selected);
        isPlacing = false;
        ghostRotation = 0;
        return true;
    }

    private boolean tryRemoveBoard(int mouseX, int mouseY) {
        int px = gridLeft + BOARD_PADDING;
        int py = gridTop + BOARD_PADDING;
        int cellStep = CELL_SIZE + CELL_GAP;

        int gridGx = (mouseX - px) / cellStep;
        int gridGy = (mouseY - py) / cellStep;

        if (gridGx < 0 || gridGx >= board.getSize() || gridGy < 0 || gridGy >= board.getSize()) {
            return false;
        }

        int cellX = px + gridGx * cellStep;
        int cellY = py + gridGy * cellStep;
        if (mouseX < cellX || mouseX >= cellX + CELL_SIZE || mouseY < cellY || mouseY >= cellY + CELL_SIZE) {
            return false;
        }

        PlacedRelic relic = board.getRelicAt(gridGx, gridGy);
        if (relic == null) return false;

        net.neoforged.neoforge.network.PacketDistributor.sendToServer(new RemoveRelicPayload(gridGx, gridGy));
        board.remove(gridGx, gridGy);
        scanInventory();
        return true;
    }

    private boolean tryClickInventory(int mouseX, int mouseY) {
        int invY = gridTop + BOARD_PADDING + board.getSize() * (CELL_SIZE + CELL_GAP) + 70;
        int invX = gridLeft;

        for (int i = 0; i < inventoryRelics.size(); i++) {
            int slotX = invX + 2 + (i % 7) * 34;
            int slotY = invY + 2 + (i / 7) * 34;

            if (mouseX >= slotX && mouseX < slotX + 30 && mouseY >= slotY && mouseY < slotY + 30) {
                if (isPlacing && selectedSlotIndex == i) {
                    isPlacing = false;
                    selectedSlotIndex = -1;
                } else {
                    selectedSlotIndex = i;
                    isPlacing = true;
                    ghostRotation = 0;
                }
                return true;
            }
        }

        isPlacing = false;
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 82) {
            ghostRotation = (ghostRotation + 1) % 4;
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void tick() {
        super.tick();
        scanInventory();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private RelicSlot getSelectedSlot() {
        if (selectedSlotIndex < 0 || selectedSlotIndex >= inventoryRelics.size()) return null;
        return inventoryRelics.get(selectedSlotIndex);
    }

    private int getElementColor(RelicElement element) {
        return switch (element) {
            case NEUTRAL -> 0xFFAAAAAA;
            case FIRE -> 0xFFFF5533;
            case WATER -> 0xFF3399FF;
            case EARTH -> 0xFF8B5E3C;
            case AIR -> 0xFF88CCEE;
            case METAL -> 0xFFC0C0C0;
            case LIFE -> 0xFF33CC55;
            case VOID -> 0xFF9933FF;
        };
    }

    private int lighten(int color, float amount) {
        int a = color & 0xFF000000;
        int r = Math.min(255, (int) (((color >> 16) & 0xFF) + amount * 255));
        int g = Math.min(255, (int) (((color >> 8) & 0xFF) + amount * 255));
        int b = Math.min(255, (int) ((color & 0xFF) + amount * 255));
        return a | (r << 16) | (g << 8) | b;
    }

    private String formatEffectShort(RelicEffectType type, float value) {
        String key = "effect.magitech." + type.getSerializedName();
        String display = Component.translatable(key).getString();
        if (value >= 1.0f) {
            return "+" + String.format("%.0f", value) + " " + display;
        } else {
            return "+" + String.format("%.0f%%", value * 100) + " " + display;
        }
    }
}
