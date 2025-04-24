package net.kuina.magitech.item.custom;

import net.kuina.magitech.fluid.magitechfluids;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;

public class ManaBucketItem extends BucketItem {
	public ManaBucketItem(Item.Properties properties ) {
        super(magitechfluids.MANA.get(), properties.craftRemainder(Items.BUCKET).stacksTo(1).rarity(Rarity.COMMON));
    }
}

