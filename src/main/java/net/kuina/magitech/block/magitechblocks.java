package net.kuina.magitech.block;

import net.kuina.magitech.block.custom.*;
import net.kuina.magitech.magitech;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

import static net.minecraft.world.level.block.SoundType.*;

public class magitechblocks {
        public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(magitech.MOD_ID);
        // マナの追加
        public static final DeferredBlock<Block> MANA = register("mana", ManaBlock::new);

        // ブロックの追加
        public static final DeferredBlock<Block> MANA_STONE = register("mana_stone", props -> new Block(
                        BlockBehaviour.Properties.of().strength(2f).requiresCorrectToolForDrops().sound(STONE)));
        public static final DeferredBlock<Block> MANA_COBBLESTONE = register("mana_cobblestone", props -> new Block(
                        BlockBehaviour.Properties.of().strength(2f).requiresCorrectToolForDrops().sound(STONE)));
        public static final DeferredBlock<Block> TESTBLOCK = register("lie_dirt", props -> new Block(
                        BlockBehaviour.Properties.of().strength(1f).requiresCorrectToolForDrops().sound(GLASS)));
        public static final DeferredBlock<CreativeEtherEnergyBlock> CREATIVE_ETHER_ENERGY_BLOCK = register(
                        "creative_ether_energy_block", CreativeEtherEnergyBlock::new);

        public static final DeferredBlock<Block> ACTIVE_MAGITECH_BLOCK = register("active_magitech_block",
                        props -> new Block(BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops()
                                        .sound(METAL).lightLevel(state -> 5)));

        // マナ貯蔵タンク
        public static final DeferredBlock<ManaTankBlock> MANA_TANK = register("mana_tank", ManaTankBlock::new);

        // マナ加工機
        public static final DeferredBlock<ManaProcessorBlock> MANA_PROCESSOR = register("mana_processor",
                        ManaProcessorBlock::new);

        // マルチブロック：マナ抽出機 (Mana Extractor)
        public static final DeferredBlock<Block> MANA_EXTRACTOR_CASING = register("mana_extractor_casing",
                        props -> new Block(BlockBehaviour.Properties.of().strength(4f).requiresCorrectToolForDrops().sound(METAL)));
        public static final DeferredBlock<net.kuina.magitech.block.custom.ManaExtractorCoreBlock> MANA_EXTRACTOR_CORE = register("mana_extractor_core",
                        net.kuina.magitech.block.custom.ManaExtractorCoreBlock::new);

        private static <B extends Block> DeferredBlock<B> register(String name,
                        Function<BlockBehaviour.Properties, ? extends B> supplier) {
                return BLOCKS.registerBlock(name, supplier, BlockBehaviour.Properties.of());
        }
}