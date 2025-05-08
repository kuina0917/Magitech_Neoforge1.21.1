package net.kuina.magitech.block.custom;

import net.kuina.magitech.block.magitechblockentities;
import net.kuina.magitech.energy.IEtherEnergyReceiver;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CreativeEtherEnergyBlockEntity extends BlockEntity {

    public CreativeEtherEnergyBlockEntity(BlockPos pos, BlockState state) {
        super(magitechblockentities.CREATIVE_ETHER_ENERGY_BLOCK_ENTITY.get(), pos, state);
    }

    public static void tick(ServerLevel level, BlockPos pos, BlockState state, CreativeEtherEnergyBlockEntity be) {
        BlockPos.betweenClosed(pos.offset(-2, -2, -2), pos.offset(2, 2, 2))
                .forEach(target -> {
                    var other = level.getBlockEntity(target);
                    if (other instanceof IEtherEnergyReceiver receiver) {
                        receiver.getEtherStorage().resetEnergy();
                    }
                });
    }
}