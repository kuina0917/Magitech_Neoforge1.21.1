package net.kuina.magitech.recipe;

import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record ManaProcessorRecipeInput(ItemStack input) implements RecipeInput {

    @Override
    public ItemStack getItem(int index) {
        return index == 0 ? input : ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return input.isEmpty();
    }

    public void fillStackedContents(StackedContents contents) {
        contents.accountStack(input);
    }
}
