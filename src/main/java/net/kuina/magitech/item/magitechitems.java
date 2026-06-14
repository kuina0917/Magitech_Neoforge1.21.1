package net.kuina.magitech.item;

import net.kuina.magitech.block.custom.CreativeEtherEnergyBlock;
import net.kuina.magitech.item.custom.RodItem;
import net.kuina.magitech.magitech;
import net.kuina.magitech.block.magitechblocks;
import net.kuina.magitech.item.custom.ManaBucketItem;
import net.kuina.magitech.item.custom.ManaCrystalItem;
import net.kuina.magitech.item.custom.ManaPickaxeItem;
import net.kuina.magitech.item.custom.ManaTankBlockItem;
import net.kuina.magitech.item.custom.PortableManaTankItem;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public class magitechitems {
        public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(magitech.MOD_ID);

        public static final DeferredItem<Item> MANA_BUCKET = register("mana_bucket", ManaBucketItem::new);

        // ここで初めてブロックアイテムとして登録
        public static final DeferredItem<Item> MANA = block(magitechblocks.MANA);
        public static final DeferredItem<Item> MANA_STONE = block(magitechblocks.MANA_STONE);
        public static final DeferredItem<Item> MANA_COBBLESTONE = block(magitechblocks.MANA_COBBLESTONE);
        public static final DeferredItem<Item> TESTBLOCK = block(magitechblocks.TESTBLOCK);
        public static final DeferredItem<Item> LOW_MANA_INGOT = register("low_mana_ingot", Item::new);
        public static final DeferredItem<Item> MIDDLE_MANA_INGOT = register("middle_mana_ingot", Item::new);
        public static final DeferredItem<Item> HIGH_MANA_INGOT = register("high_mana_ingot", Item::new);
        public static final DeferredItem<Item> MANA_CRYSTAL = register("mana_crystal", ManaCrystalItem::new);
        public static final DeferredItem<Item> MAGITECH_GUIDE = register("magitech_guide",
                        props -> new net.kuina.magitech.item.custom.MagitechGuideItem(props.stacksTo(1)));

        /** 低級マナつるはし用のツールティア（修理素材は淡輝マナインゴット） */
        public static final Tier LOW_MANA_TIER = new Tier() {
                @Override
                public int getUses() {
                        return 500;
                }

                @Override
                public float getSpeed() {
                        return 6.5F;
                }

                @Override
                public float getAttackDamageBonus() {
                        return 2.0F;
                }

                @Override
                public TagKey<Block> getIncorrectBlocksForDrops() {
                        return BlockTags.INCORRECT_FOR_IRON_TOOL;
                }

                @Override
                public int getEnchantmentValue() {
                        return 14;
                }

                @Override
                public Ingredient getRepairIngredient() {
                        return Ingredient.of(LOW_MANA_INGOT.get());
                }
        };

        public static final DeferredItem<Item> LOW_MANA_PICKAXE = register("low_mana_pickaxe",
                        props -> new ManaPickaxeItem(LOW_MANA_TIER,
                                        props.attributes(PickaxeItem.createAttributes(LOW_MANA_TIER, 1.0F, -2.8F))));
        public static final DeferredItem<Item> CRYSTAL_ROD = register("crystal_rod", props -> new RodItem(props
                        .stacksTo(1)
                        .durability(200)
                        .rarity(Rarity.UNCOMMON)));
        /** ★ 追加：クリエイティブ・エーテル・エネルギー・ブロックのアイテム */
        public static final DeferredItem<Item> CREATIVE_ETHER_ENERGY_BLOCK = block(
                        magitechblocks.CREATIVE_ETHER_ENERGY_BLOCK);
        public static final DeferredItem<Item> ACTIVE_MAGITECH_BLOCK = block(magitechblocks.ACTIVE_MAGITECH_BLOCK);

        /** マナ貯蔵タンク（ツールチップにマナ量を表示するブロックアイテム）。 */
        public static final DeferredItem<Item> MANA_TANK = ITEMS.registerItem("mana_tank",
                        props -> new ManaTankBlockItem(magitechblocks.MANA_TANK.get(), props),
                        new Item.Properties());

        /** 携帯マナタンク。スタック単位で持ち運べないよう 1 個制限。 */
        public static final DeferredItem<Item> PORTABLE_MANA_TANK = register("portable_mana_tank",
                        props -> new PortableManaTankItem(props.stacksTo(1)));

        /** マナ加工機（ブロックアイテム）。 */
        public static final DeferredItem<Item> MANA_PROCESSOR = block(magitechblocks.MANA_PROCESSOR);

        /** マルチブロック：マナ抽出機のケーシング（ブロックアイテム）。 */
        public static final DeferredItem<Item> MANA_EXTRACTOR_CASING = block(magitechblocks.MANA_EXTRACTOR_CASING);
        /** マルチブロック：マナ抽出機のコア（ブロックアイテム）。 */
        public static final DeferredItem<Item> MANA_EXTRACTOR_CORE = block(magitechblocks.MANA_EXTRACTOR_CORE);

        private static <I extends Item> DeferredItem<I> register(String name,
                        Function<Item.Properties, ? extends I> sup) {
                return ITEMS.registerItem(name, sup, new Item.Properties());
        }

        /** 任意の Block を BlockItem 化して登録するヘルパ */
        private static DeferredItem<Item> block(DeferredHolder<Block, ? extends Block> holder) {
                return ITEMS.registerItem(
                                holder.getId().getPath(), // 登録名＝ブロックと同じ
                                props -> new BlockItem(holder.get(), props),
                                new Item.Properties());
        }
}