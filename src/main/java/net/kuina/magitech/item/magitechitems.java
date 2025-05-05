package net.kuina.magitech.item;

import net.kuina.magitech.item.custom.RodItem;
import net.kuina.magitech.magitech;
import net.kuina.magitech.block.magitechblocks;
import net.kuina.magitech.item.custom.ManaBucketItem;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public class magitechitems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(magitech.MOD_ID);

    public static final DeferredItem<Item> MANA_BUCKET =
            register("mana_bucket", ManaBucketItem::new);

    // ここで初めてブロックアイテムとして登録
    public static final DeferredItem<Item> MANA =
            block(magitechblocks.MANA);
    public static final DeferredItem<Item> MANA_STONE =
            block(magitechblocks.MANA_STONE);
    public static final DeferredItem<Item> MANA_COBBLESTONE =
            block(magitechblocks.MANA_COBBLESTONE);
    public static final DeferredItem<Item> TESTBLOCK =
            block(magitechblocks.TESTBLOCK);
    public static final DeferredItem<Item> LOW_MANA_INGOT =
            register("low_mana_ingot", Item::new);
    public static final DeferredItem<Item> MIDDLE_MANA_INGOT =
            register("middle_mana_ingot", Item::new);
    public static final DeferredItem<Item> HIGH_MANA_INGOT =
            register("high_mana_ingot", Item::new);
    public static final DeferredItem<Item> MANA_CRYSTAL =
            register("mana_crystal", Item::new);
    public static final DeferredItem<Item> CRYSTAL_ROD =
            register("crystal_rod", props -> new RodItem(props
                            .stacksTo(1)
                            .durability(200)
                            .rarity(Rarity.UNCOMMON)
            ));



    private static <I extends Item> DeferredItem<I> register(String name, Function<Item.Properties, ? extends I> sup) {
        return ITEMS.registerItem(name, sup, new Item.Properties());
    }

    private static DeferredItem<Item> block(DeferredHolder<Block, Block> block) {
        return ITEMS.registerItem(
                block.getId().getPath(),
                props -> new BlockItem(block.get(), props),
                new Item.Properties()
        );
    }
}