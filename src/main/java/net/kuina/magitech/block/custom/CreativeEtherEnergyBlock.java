package net.kuina.magitech.block.custom;

import net.kuina.magitech.block.magitechblockentities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class CreativeEtherEnergyBlock extends Block implements EntityBlock {

    public CreativeEtherEnergyBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CreativeEtherEnergyBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null :
                (lvl, pos, st, be) -> {
                    if (type == magitechblockentities.CREATIVE_ETHER_ENERGY_BLOCK_ENTITY.get()) {
                        CreativeEtherEnergyBlockEntity.tick((ServerLevel) lvl, pos, st, (CreativeEtherEnergyBlockEntity) be);
                    }
                };
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}