package net.kuina.magitech.block;


import net.kuina.magitech.block.custom.CreativeEtherEnergyBlockEntity;
import net.kuina.magitech.block.custom.ManaProcessorBlockEntity;
import net.kuina.magitech.block.custom.ManaTankBlockEntity;
import net.kuina.magitech.Magitech;
import net.kuina.magitech.block.MagitechBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.core.registries.BuiltInRegistries;
public class MagitechBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Magitech.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CreativeEtherEnergyBlockEntity>> CREATIVE_ETHER_ENERGY_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("creative_ether_energy_block_entity", () ->
                    BlockEntityType.Builder.of(CreativeEtherEnergyBlockEntity::new,
                            MagitechBlocks.CREATIVE_ETHER_ENERGY_BLOCK.get()
                    ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ManaTankBlockEntity>> MANA_TANK_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("mana_tank_block_entity", () ->
                    BlockEntityType.Builder.of(ManaTankBlockEntity::new,
                            MagitechBlocks.MANA_TANK.get()
                    ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ManaProcessorBlockEntity>> MANA_PROCESSOR_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("mana_processor_block_entity", () ->
                    BlockEntityType.Builder.of(ManaProcessorBlockEntity::new,
                            MagitechBlocks.MANA_PROCESSOR.get()
                    ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<net.kuina.magitech.block.custom.ManaExtractorCoreBlockEntity>> MANA_EXTRACTOR_CORE_ENTITY =
            BLOCK_ENTITIES.register("mana_extractor_core_entity", () ->
                    BlockEntityType.Builder.of(net.kuina.magitech.block.custom.ManaExtractorCoreBlockEntity::new,
                            MagitechBlocks.MANA_EXTRACTOR_CORE.get()
                    ).build(null));
}