package net.kuina.magitech.recipe;

import net.kuina.magitech.magitech;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, magitech.MOD_ID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, magitech.MOD_ID);

    public static final Supplier<RecipeType<ManaProcessorRecipe>> MANA_PROCESSING_TYPE =
            TYPES.register("mana_processing", () -> ManaProcessorRecipe.Type.INSTANCE);

    public static final Supplier<RecipeSerializer<ManaProcessorRecipe>> MANA_PROCESSING_SERIALIZER =
            SERIALIZERS.register("mana_processing", () -> ManaProcessorRecipe.Serializer.INSTANCE);
}
