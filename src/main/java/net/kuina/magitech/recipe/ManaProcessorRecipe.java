package net.kuina.magitech.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class ManaProcessorRecipe implements Recipe<ManaProcessorRecipeInput> {

    private final Ingredient ingredient;
    private final ItemStack result;
    private final int manaCost;
    private final int processTime;

    public ManaProcessorRecipe(Ingredient ingredient, ItemStack result, int manaCost, int processTime) {
        this.ingredient = ingredient;
        this.result = result;
        this.manaCost = manaCost;
        this.processTime = processTime;
    }

    public Ingredient getIngredient() { return ingredient; }
    public ItemStack getResult() { return result; }
    public int getManaCost() { return manaCost; }
    public int getManaPerTick() { return Math.max(1, manaCost / processTime); }
    public int getProcessTime() { return processTime; }

    @Override
    public boolean matches(ManaProcessorRecipeInput input, Level level) {
        return ingredient.test(input.getItem(0));
    }

    @Override
    public ItemStack assemble(ManaProcessorRecipeInput input, HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<ManaProcessorRecipe> {
        public static final Type INSTANCE = new Type();
    }

    public static class Serializer implements RecipeSerializer<ManaProcessorRecipe> {

        public static final Serializer INSTANCE = new Serializer();

        private static final MapCodec<ManaProcessorRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        Ingredient.CODEC.fieldOf("ingredient").forGetter(ManaProcessorRecipe::getIngredient),
                        ItemStack.CODEC.fieldOf("result").forGetter(ManaProcessorRecipe::getResult),
                        Codec.INT.fieldOf("manaCost").forGetter(ManaProcessorRecipe::getManaCost),
                        Codec.INT.fieldOf("processTime").forGetter(ManaProcessorRecipe::getProcessTime)
                ).apply(instance, ManaProcessorRecipe::new)
        );

        private static final StreamCodec<RegistryFriendlyByteBuf, ManaProcessorRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, r -> r.ingredient,
                        ItemStack.STREAM_CODEC, r -> r.result,
                        ByteBufCodecs.VAR_INT, r -> r.manaCost,
                        ByteBufCodecs.VAR_INT, r -> r.processTime,
                        ManaProcessorRecipe::new
                );

        @Override
        public MapCodec<ManaProcessorRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ManaProcessorRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
