package net.kuina.magitech.block;


import net.kuina.magitech.block.custom.CreativeEtherEnergyBlockEntity;
import net.kuina.magitech.magitech;
import net.kuina.magitech.block.magitechblocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class magitechblockentities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, magitech.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CreativeEtherEnergyBlockEntity>> CREATIVE_ETHER_ENERGY_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("creative_ether_energy_block_entity", () ->
                    BlockEntityType.Builder.of(CreativeEtherEnergyBlockEntity::new,
                            magitechblocks.CREATIVE_ETHER_ENERGY_BLOCK.get()
                    ).build(null));
}