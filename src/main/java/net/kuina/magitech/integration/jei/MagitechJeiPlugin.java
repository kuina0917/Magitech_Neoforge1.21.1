package net.kuina.magitech.integration.jei;

import net.kuina.magitech.Magitech;
import net.kuina.magitech.block.MagitechBlocks;
import net.kuina.magitech.integration.jei.category.ManaProcessorRecipeCategory;
import net.kuina.magitech.item.MagitechItems;
import net.kuina.magitech.menu.MagitechMenus;
import net.kuina.magitech.recipe.ManaProcessorRecipe;
import net.kuina.magitech.recipe.ModRecipes;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.registration.IGuiHandlerRegistration;

import java.util.List;

@JeiPlugin
public class MagitechJeiPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(Magitech.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new ManaProcessorRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(MagitechBlocks.MANA_PROCESSOR.get()), ManaProcessorRecipeCategory.TYPE);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(
                net.kuina.magitech.menu.ManaProcessorMenu.class,
                MagitechMenus.MANA_PROCESSOR_MENU.get(),
                ManaProcessorRecipeCategory.TYPE,
                0,  // recipe slot start (input)
                1,  // recipe slot count
                2,  // inventory slot start
                36  // inventory slot count
        );
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(
                net.kuina.magitech.client.screen.ManaProcessorScreen.class,
                79,  // 矢印のX（GUI左上からの相対座標）
                34,  // 矢印のY
                24,  // 矢印の幅
                16,  // 矢印の高さ
                ManaProcessorRecipeCategory.TYPE
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<ManaProcessorRecipe> recipes = getRecipes();
        registration.addRecipes(ManaProcessorRecipeCategory.TYPE, recipes);
    }

    private static List<ManaProcessorRecipe> getRecipes() {
        if (Minecraft.getInstance().level != null) {
            RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
            var holders = manager.getAllRecipesFor(ModRecipes.MANA_PROCESSING_TYPE.get());
            if (!holders.isEmpty()) {
                return holders.stream().map(h -> h.value()).toList();
            }
        }
        return List.of(createDefaultRecipe());
    }

    private static ManaProcessorRecipe createDefaultRecipe() {
        return new ManaProcessorRecipe(
                Ingredient.of(Items.IRON_INGOT),
                new ItemStack(MagitechItems.LOW_MANA_INGOT.get()),
                2000,
                100);
    }
}
