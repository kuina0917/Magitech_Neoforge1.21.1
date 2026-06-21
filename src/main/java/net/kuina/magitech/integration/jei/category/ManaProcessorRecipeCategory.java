package net.kuina.magitech.integration.jei.category;

import net.kuina.magitech.block.MagitechBlocks;
import net.kuina.magitech.Magitech;
import net.kuina.magitech.recipe.ManaProcessorRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;

public class ManaProcessorRecipeCategory implements IRecipeCategory<ManaProcessorRecipe> {

    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(Magitech.MOD_ID, "mana_processing");
    public static final RecipeType<ManaProcessorRecipe> TYPE = RecipeType.create(Magitech.MOD_ID, "mana_processing", ManaProcessorRecipe.class);
    private static final ResourceLocation BG_TEXTURE = ResourceLocation.fromNamespaceAndPath(Magitech.MOD_ID, "textures/gui/machine_gui.png");
    private static final ResourceLocation SLOT_TEXTURE = ResourceLocation.fromNamespaceAndPath(Magitech.MOD_ID, "textures/gui/gui_item_slot.png");
    private static final ResourceLocation BAR_FRAME_TEXTURE = ResourceLocation.fromNamespaceAndPath(Magitech.MOD_ID, "textures/gui/barframe.png");
    private static final ResourceLocation MANA_BAR_TEXTURE = ResourceLocation.fromNamespaceAndPath(Magitech.MOD_ID, "textures/gui/mana_bar.png");
    private static final ResourceLocation ARROW_TEXTURE = ResourceLocation.fromNamespaceAndPath(Magitech.MOD_ID, "textures/gui/arrow_icon.png");

    private static final int MANA_BAR_X = 13;
    private static final int MANA_BAR_Y = 17;
    private static final int MANA_BAR_W = 12;
    private static final int MANA_BAR_H = 48;

    private static final int INPUT_X = 56;
    private static final int INPUT_Y = 17;

    private static final int CATALYST_X = 56;
    private static final int CATALYST_Y = 53;

    private static final int ARROW_X = 79;
    private static final int ARROW_Y = 34;
    private static final int ARROW_W = 24;
    private static final int ARROW_H = 17;

    private static final int OUTPUT_X = 116;
    private static final int OUTPUT_Y = 35;

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableStatic slotFrame;
    private final IDrawableStatic barFrame;
    private final IDrawableStatic arrowEmpty;
    private final IDrawableAnimated progressArrow;
    private final Component title;

    public ManaProcessorRecipeCategory(IGuiHelper guiHelper) {
        this.title = Component.translatable("block.magitech.mana_processor");

        // IGuiHelper.createDrawable は 5 引数のみ対応
        this.background = guiHelper.createDrawable(BG_TEXTURE, 5, 5, 140, 75);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(MagitechBlocks.MANA_PROCESSOR.get()));

        this.slotFrame = guiHelper.createDrawable(SLOT_TEXTURE, 0, 0, 18, 18);
        this.barFrame = guiHelper.createDrawable(BAR_FRAME_TEXTURE, 0, 0, 14, 50);

        this.arrowEmpty = guiHelper.createDrawable(ARROW_TEXTURE, 0, 0, ARROW_W, ARROW_H);
        this.progressArrow = guiHelper.createAnimatedDrawable(
                guiHelper.createDrawable(ARROW_TEXTURE, 0, ARROW_H, ARROW_W, ARROW_H),
                100, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public RecipeType<ManaProcessorRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public int getWidth() {
        return 140;
    }

    @Override
    public int getHeight() {
        return 80;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ManaProcessorRecipe recipe, IFocusGroup focuses) {
        ItemStack[] items = recipe.getIngredient().getItems();
        if (items.length > 0) {
            builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X - 5, INPUT_Y - 5)
                    .addItemStack(items[0]);
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X - 5, OUTPUT_Y - 5)
                .addItemStack(recipe.getResult());
    }

    @Override
    public void draw(ManaProcessorRecipe recipe, IRecipeSlotsView slotsView, GuiGraphics guiGraphics, double mouseX,
            double mouseY) {
        // JEI は (0,0) 起点なので、座標をオフセットする
        int ox = -5;
        int oy = -5;

        // IDrawable を使わず、正しいテクスチャサイズを指定して blit する（引き延ばし防止）
        guiGraphics.blit(BG_TEXTURE, 0, 0, 5, 5, 140, 75, 176, 166);

        // マナバー枠
        guiGraphics.blit(BAR_FRAME_TEXTURE, MANA_BAR_X + ox - 1, MANA_BAR_Y + oy - 1, 0, 0, 14, 50, 14, 50);
        // マナバー中身
        guiGraphics.blit(MANA_BAR_TEXTURE, MANA_BAR_X + ox, MANA_BAR_Y + oy, 0, 0, MANA_BAR_W, MANA_BAR_H, 12, 48);

        // 各スロット枠
        renderSlot(guiGraphics, INPUT_X + ox - 1, INPUT_Y + oy - 1);
        renderSlot(guiGraphics, CATALYST_X + ox - 1, CATALYST_Y + oy - 1);
        renderSlot(guiGraphics, OUTPUT_X + ox - 1, OUTPUT_Y + oy - 1);

        // 矢印（下地）
        guiGraphics.blit(ARROW_TEXTURE, ARROW_X + ox, ARROW_Y + oy, 0, 0, ARROW_W, ARROW_H, 256, 256);
        // 矢印（進捗）
        progressArrow.draw(guiGraphics, ARROW_X + ox, ARROW_Y + oy);

        Font font = Minecraft.getInstance().font;
        Component manaText = Component.literal(recipe.getManaCost() + " Mana");
        Component timeText = Component.literal((recipe.getProcessTime() / 20) + "s");
        guiGraphics.drawString(font, manaText, 14 + ox, 68 + oy, 0xFF444444, false);
        guiGraphics.drawString(font, timeText, 85 + ox, 68 + oy, 0xFF444444, false);
    }

    private void renderSlot(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(SLOT_TEXTURE, x, y, 0, 0, 18, 18, 18, 18);
    }
}
