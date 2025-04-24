package net.kuina.magitech.block.custom;

import net.kuina.magitech.fluid.magitechfluids;

import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.LiquidBlock;


public class ManaBlock extends LiquidBlock {
    public ManaBlock(BlockBehaviour.Properties properties) {
        super(magitechfluids.MANA.get(),  //
                properties.mapColor(MapColor.COLOR_CYAN)
                        .strength(100f).lightLevel(s -> 10)
                        .noCollission().noLootTable().liquid()
                        .pushReaction(PushReaction.DESTROY)
                        .sound(SoundType.EMPTY).replaceable());

    }}