package net.kuina.magitech.block;

import net.kuina.magitech.item.magitechitems;
import net.kuina.magitech.magitech;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class magitechblocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(magitech.MOD_ID);

    // Mana Stone ブロックを登録
    public static final DeferredBlock<Block> MANA_STONE = registerBlock("mana_stone",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));
    public static final DeferredBlock<Block> MANA_COBBLESTONE = registerBlock("mana_cobblestone",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));

    // ブロックの登録メソッド
    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);  // アイテムも登録
        return toReturn;
    }

    // アイテムの登録メソッド
    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        magitechitems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    // イベントバスに登録
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
