package net.kuina.magitech.item;


import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class magitechitems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("magitech");

    public static final DeferredItem<Item> LOW_MANA_STONE = ITEMS.register("lowmanastone",
            ()-> new Item(new Item.Properties()));
    public static final DeferredItem<Item> LOW_MANA_INGOT = ITEMS.register("low_mana_ingot",
            ()-> new Item(new Item.Properties()));
    public static final DeferredItem<Item> MIDDLE_MANA_INGOT = ITEMS.register("middle_mana_ingot",
            ()-> new Item(new Item.Properties()));
    public static final DeferredItem<Item> HIGH_MANA_INGOT = ITEMS.register("high_mana_ingot",
            ()-> new Item(new Item.Properties()));

    public static void register (IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
