package net.kuina.magitech.block.custom;

import net.kuina.magitech.block.magitechblockentities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CreativeEtherEnergyBlock extends Block implements EntityBlock {

    public CreativeEtherEnergyBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    /* ---------- BlockEntity ---------- */

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CreativeEtherEnergyBlockEntity(pos, state);
    }

    /* ---------- サーバー Ticker ---------- */
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level lvl, BlockState st,
                                                                  BlockEntityType<T> type) {
        return lvl.isClientSide ? null : (svLevel, pos, state, be) -> {
            if (be instanceof CreativeEtherEnergyBlockEntity cee) {
                CreativeEtherEnergyBlockEntity.tick((ServerLevel) svLevel, pos, state, cee);
            }
        };
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}