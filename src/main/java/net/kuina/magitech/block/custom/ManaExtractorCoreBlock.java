package net.kuina.magitech.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.kuina.magitech.block.base.MachineBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * マルチブロック「マナ抽出機」のコア。
 * 3x3x3 のケーシングに囲まれることで起動する。
 */
public class ManaExtractorCoreBlock extends MachineBlock {
    public static final BooleanProperty FORMED = BooleanProperty.create("formed");
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public ManaExtractorCoreBlock(BlockBehaviour.Properties properties) {
        super(properties.strength(4f).requiresCorrectToolForDrops());
        this.registerDefaultState(this.stateDefinition.any().setValue(FORMED, false).setValue(FACING, Direction.NORTH));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ManaExtractorCoreBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ManaExtractorCoreBlockEntity core) {
                if (!state.getValue(FORMED)) {
                    if (core.checkStructure()) {
                        level.setBlock(pos, state.setValue(FORMED, true), 3);
                        player.displayClientMessage(Component.translatable("msg.magitech.multiblock_formed"), true);
                    } else {
                        player.displayClientMessage(Component.translatable("msg.magitech.multiblock_incomplete"), true);
                    }
                } else {
                    // 起動済みなら設定GUIを開く
                    player.openMenu(new net.minecraft.world.MenuProvider() {
                        @Override
                        public Component getDisplayName() {
                            return Component.translatable("block.magitech.mana_extractor_core");
                        }

                        @Nullable
                        @Override
                        public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
                            return new net.kuina.magitech.menu.ManaExtractorMenu(id, inv, core);
                        }
                    }, pos);
                }
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FORMED, FACING);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        return (lvl, pos, st, be) -> {
            if (be instanceof ManaExtractorCoreBlockEntity core) {
                core.tick();
            }
        };
    }
}
