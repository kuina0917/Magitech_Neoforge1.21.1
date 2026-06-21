package net.kuina.magitech.item;

import net.kuina.magitech.item.custom.RelicBoardItem;
import net.kuina.magitech.item.custom.RelicItem;
import net.kuina.magitech.item.custom.RodItem;
import net.kuina.magitech.Magitech;
import net.kuina.magitech.block.MagitechBlocks;
import net.kuina.magitech.item.custom.ManaBucketItem;
import net.kuina.magitech.item.custom.ManaCrystalItem;
import net.kuina.magitech.item.custom.ManaPickaxeItem;
import net.kuina.magitech.item.custom.ManaTankBlockItem;
import net.kuina.magitech.item.custom.PortableManaTankItem;
import net.kuina.magitech.relic.RelicData;
import net.kuina.magitech.relic.RelicEffectType;
import net.kuina.magitech.relic.RelicElement;
import net.kuina.magitech.relic.RelicRarity;
import net.kuina.magitech.relic.RelicShape;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Map;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public class MagitechItems {
        public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Magitech.MOD_ID);

        public static final DeferredItem<Item> MANA_BUCKET = register("mana_bucket", ManaBucketItem::new);

        // ここで初めてブロックアイテムとして登録
        public static final DeferredItem<Item> MANA = block(MagitechBlocks.MANA);
        public static final DeferredItem<Item> MANA_STONE = block(MagitechBlocks.MANA_STONE);
        public static final DeferredItem<Item> MANA_COBBLESTONE = block(MagitechBlocks.MANA_COBBLESTONE);
        public static final DeferredItem<Item> TESTBLOCK = block(MagitechBlocks.TESTBLOCK);
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
                        MagitechBlocks.CREATIVE_ETHER_ENERGY_BLOCK);
        public static final DeferredItem<Item> ACTIVE_MAGITECH_BLOCK = block(MagitechBlocks.ACTIVE_MAGITECH_BLOCK);

        /** マナ貯蔵タンク（ツールチップにマナ量を表示するブロックアイテム）。 */
        public static final DeferredItem<Item> MANA_TANK = ITEMS.registerItem("mana_tank",
                        props -> new ManaTankBlockItem(MagitechBlocks.MANA_TANK.get(), props),
                        new Item.Properties());

        /** 機械用のコアアイテム。各機械の中心に使用する。 */
        public static final DeferredItem<Item> MACHINE_CORE = register("machine_core", Item::new);

        /** 携帯マナタンク。スタック単位で持ち運べないよう 1 個制限。 */
        public static final DeferredItem<Item> PORTABLE_MANA_TANK = register("portable_mana_tank",
                        props -> new PortableManaTankItem(props.stacksTo(1)));

        /** マナ加工機（ブロックアイテム）。 */
        public static final DeferredItem<Item> MANA_PROCESSOR = block(MagitechBlocks.MANA_PROCESSOR);

        /** マルチブロック：マナ抽出機のケーシング（ブロックアイテム）。 */
        public static final DeferredItem<Item> MANA_EXTRACTOR_CASING = block(MagitechBlocks.MANA_EXTRACTOR_CASING);
        /** マルチブロック：マナ抽出機のコア（ブロックアイテム）。 */
        public static final DeferredItem<Item> MANA_EXTRACTOR_CORE = block(MagitechBlocks.MANA_EXTRACTOR_CORE);

        // ─── 遺物システム ────────────────────────────────────────────

        /** 遺物ベースアイテム（DataComponent で中身を切り替え）。 */
        public static final DeferredItem<Item> RELIC = register("relic",
                props -> new RelicItem(props.stacksTo(1)));

        /** 遺物ボードを開くアイテム。 */
        public static final DeferredItem<Item> RELIC_BOARD = register("relic_board",
                props -> new RelicBoardItem(props.stacksTo(1)));

        // ─── 定義済み遺物テンプレート ────────────────────────────────

        public static final DeferredItem<Item> DEMON_STEEL_HEART = register("demon_steel_heart",
                props -> new RelicItem(props.stacksTo(1)));

        public static final DeferredItem<Item> GIANT_MUSCLE_FIBER = register("giant_muscle_fiber",
                props -> new RelicItem(props.stacksTo(1)));

        public static final DeferredItem<Item> SMITHING_GOD_CORE = register("smithing_god_core",
                props -> new RelicItem(props.stacksTo(1)));

        public static final DeferredItem<Item> WORLD_TREE_SPROUT = register("world_tree_sprout",
                props -> new RelicItem(props.stacksTo(1)));

        public static final DeferredItem<Item> RESONANCE_SPORES = register("resonance_spores",
                props -> new RelicItem(props.stacksTo(1)));

        public static final DeferredItem<Item> FLOATING_POLLEN_SAC = register("floating_pollen_sac",
                props -> new RelicItem(props.stacksTo(1)));

        public static final DeferredItem<Item> ELEMENTAL_CORE = register("elemental_core",
                props -> new RelicItem(props.stacksTo(1)));

        public static final DeferredItem<Item> PHASE_CRYSTAL = register("phase_crystal",
                props -> new RelicItem(props.stacksTo(1)));

        public static final DeferredItem<Item> IMAGINARY_OPERATOR = register("imaginary_operator",
                props -> new RelicItem(props.stacksTo(1)));

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

        // ─── 遺物スタック生成ヘルパ ──────────────────────────────────

        public static ItemStack createDemonSteelHeart() {
                return RelicItem.create(new RelicData(
                        "demon_steel_heart", RelicRarity.RARE,
                        RelicShape.square(2),
                        Map.of(RelicEffectType.ATTACK_DAMAGE, 4.0f, RelicEffectType.MAX_HEALTH, -0.05f),
                        Map.of(RelicEffectType.LIFE_STEAL, 0.02f),
                        RelicElement.METAL
                ));
        }

        public static ItemStack createGiantMuscleFiber() {
                return RelicItem.create(new RelicData(
                        "giant_muscle_fiber", RelicRarity.UNCOMMON,
                        RelicShape.vertical(2),
                        Map.of(RelicEffectType.DEFENSE, 3.0f, RelicEffectType.MAX_HEALTH, 0.10f),
                        Map.of(RelicEffectType.ATTACK_DAMAGE, 1.0f),
                        RelicElement.EARTH
                ));
        }

        public static ItemStack createSmithingGodCore() {
                return RelicItem.create(new RelicData(
                        "smithing_god_core", RelicRarity.EPIC,
                        RelicShape.TShape(),
                        Map.of(RelicEffectType.ATTACK_DAMAGE, 6.0f, RelicEffectType.ATTACK_SPEED, 0.15f),
                        Map.of(RelicEffectType.CRIT_RATE, 0.03f, RelicEffectType.CRIT_DAMAGE, 0.10f),
                        RelicElement.FIRE
                ));
        }

        public static ItemStack createWorldTreeSprout() {
                return RelicItem.create(new RelicData(
                        "world_tree_sprout", RelicRarity.RARE,
                        RelicShape.single(),
                        Map.of(RelicEffectType.MAX_HEALTH, 0.15f, RelicEffectType.HEALING, 1.0f),
                        Map.of(RelicEffectType.MANA_REGEN, 0.5f),
                        RelicElement.LIFE
                ));
        }

        public static ItemStack createResonanceSpores() {
                return RelicItem.create(new RelicData(
                        "resonance_spores", RelicRarity.UNCOMMON,
                        RelicShape.horizontal(2),
                        Map.of(RelicEffectType.STATUS_EFFECT_CHANCE, 0.15f),
                        Map.of(RelicEffectType.CRIT_RATE, 0.02f, RelicEffectType.DODGE, 0.01f),
                        RelicElement.AIR
                ));
        }

        public static ItemStack createFloatingPollenSac() {
                return RelicItem.create(new RelicData(
                        "floating_pollen_sac", RelicRarity.COMMON,
                        RelicShape.LShape(),
                        Map.of(RelicEffectType.RANGE, 2.0f),
                        Map.of(RelicEffectType.ACCURACY, 0.03f),
                        RelicElement.AIR
                ));
        }

        public static ItemStack createElementalCore() {
                return RelicItem.create(new RelicData(
                        "elemental_core", RelicRarity.EPIC,
                        RelicShape.cross(),
                        Map.of(RelicEffectType.MAGIC_DAMAGE, 0.15f, RelicEffectType.MANA_MAX, 50.0f),
                        Map.of(RelicEffectType.CAST_SPEED, 0.10f, RelicEffectType.MANA_REGEN, 1.0f),
                        RelicElement.NEUTRAL
                ));
        }

        public static ItemStack createPhaseCrystal() {
                return RelicItem.create(new RelicData(
                        "phase_crystal", RelicRarity.RARE,
                        RelicShape.horizontal(3),
                        Map.of(RelicEffectType.CAST_SPEED, 0.20f, RelicEffectType.MANA_MAX, 30.0f),
                        Map.of(RelicEffectType.DODGE, 0.05f),
                        RelicElement.VOID
                ));
        }

        public static ItemStack createImaginaryOperator() {
                return RelicItem.create(new RelicData(
                        "imaginary_operator", RelicRarity.LEGENDARY,
                        RelicShape.TShape(),
                        Map.of(RelicEffectType.MAGIC_DAMAGE, 0.30f, RelicEffectType.MANA_REGEN, 3.0f),
                        Map.of(RelicEffectType.CRIT_RATE, 0.05f, RelicEffectType.CRIT_DAMAGE, 0.15f),
                        RelicElement.VOID
                ));
        }
}