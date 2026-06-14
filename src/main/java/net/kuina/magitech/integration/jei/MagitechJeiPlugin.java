package net.kuina.magitech.integration.jei;

import net.kuina.magitech.block.magitechblocks;
import net.kuina.magitech.integration.jei.category.ManaProcessorRecipeCategory;
import net.kuina.magitech.item.magitechitems;
import net.kuina.magitech.magitech;
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

import java.util.List;

@JeiPlugin
public class MagitechJeiPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(magitech.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new ManaProcessorRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(magitechblocks.MANA_PROCESSOR.get()), ManaProcessorRecipeCategory.TYPE);
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
                new ItemStack(magitechitems.LOW_MANA_INGOT.get()),
                2000,
                100);
    }
}
