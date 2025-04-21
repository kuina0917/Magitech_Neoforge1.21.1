package net.kuina.magitech.item;



import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.Tags;
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
    public static final DeferredItem<BucketItem> MANA_BUCKET = ITEMS.register("mana_bucket",
            () -> new BucketItem(Fluids.WATER, new Item.Properties()));
    //Mana_Bucketの実装完了、マナフルイドが出来たらWATERと入れ替える


    public static void register (IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
