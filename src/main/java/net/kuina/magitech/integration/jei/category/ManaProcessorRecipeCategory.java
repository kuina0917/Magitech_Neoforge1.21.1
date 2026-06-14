package net.kuina.magitech.integration.jei.category;

import net.kuina.magitech.block.magitechblocks;
import net.kuina.magitech.magitech;
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

    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(magitech.MOD_ID, "mana_processing");
    public static final RecipeType<ManaProcessorRecipe> TYPE = RecipeType.create(magitech.MOD_ID, "mana_processing", ManaProcessorRecipe.class);
    private static final ResourceLocation MANA_BAR_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            magitech.MOD_ID, "textures/gui/mana_bar.png");
    private static final ResourceLocation FURNACE_TEXTURE = ResourceLocation.withDefaultNamespace(
            "textures/gui/container/furnace.png");

    private static final int MANA_BAR_X = 4;
    private static final int MANA_BAR_Y = 6;
    private static final int MANA_BAR_W = 12;
    private static final int MANA_BAR_H = 48;

    private static final int INPUT_X = 24;
    private static final int INPUT_Y = 22;

    private static final int ARROW_X = 50;
    private static final int ARROW_Y = 22;
    private static final int ARROW_W = 24;
    private static final int ARROW_H = 16;

    private static final int OUTPUT_X = 82;
    private static final int OUTPUT_Y = 22;

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableStatic manaBarBg;
    private final IDrawableAnimated progressArrow;
    private final Component title;

    public ManaProcessorRecipeCategory(IGuiHelper guiHelper) {
        this.title = Component.translatable("block.magitech.mana_processor");

        this.background = guiHelper.createBlankDrawable(120, 60);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(magitechblocks.MANA_PROCESSOR.get()));

        this.manaBarBg = guiHelper.createDrawable(MANA_BAR_TEXTURE, 0, 0, MANA_BAR_W, MANA_BAR_H);

        IDrawableStatic arrowEmpty = guiHelper.createDrawable(FURNACE_TEXTURE, 79, 34, ARROW_W, ARROW_H);
        this.progressArrow = guiHelper.createAnimatedDrawable(arrowEmpty,
                100,
                IDrawableAnimated.StartDirection.LEFT, false);
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
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ManaProcessorRecipe recipe, IFocusGroup focuses) {
        ItemStack[] items = recipe.getIngredient().getItems();
        if (items.length > 0) {
            builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X, INPUT_Y)
                    .addItemStack(items[0]);
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, OUTPUT_Y)
                .addItemStack(recipe.getResult());
    }

    @Override
    public void draw(ManaProcessorRecipe recipe, IRecipeSlotsView slotsView, GuiGraphics guiGraphics, double mouseX,
            double mouseY) {
        manaBarBg.draw(guiGraphics, MANA_BAR_X, MANA_BAR_Y);

        int filled = (int) (recipe.getManaCost() * MANA_BAR_H / recipe.getManaCost());
        if (filled > 0) {
            int drawY = MANA_BAR_Y + MANA_BAR_H - filled;
            guiGraphics.blit(MANA_BAR_TEXTURE, MANA_BAR_X, drawY, 0, MANA_BAR_H - filled,
                    MANA_BAR_W, filled, MANA_BAR_W, MANA_BAR_H);
        }

        progressArrow.draw(guiGraphics, ARROW_X, ARROW_Y);

        Font font = Minecraft.getInstance().font;
        Component manaText = Component.translatable("jei.magitech.mana_cost", recipe.getManaCost());
        Component timeText = Component.translatable("jei.magitech.duration_seconds", recipe.getProcessTime() / 20);
        guiGraphics.drawString(font, manaText, 6, MANA_BAR_Y + MANA_BAR_H + 4, 0xFF555555, false);
        guiGraphics.drawString(font, timeText, 6, MANA_BAR_Y + MANA_BAR_H + 14, 0xFF555555, false);
    }
}
