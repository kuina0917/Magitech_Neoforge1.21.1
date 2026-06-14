package net.kuina.magitech.block.custom;

import net.kuina.magitech.capability.ManaCapabilities;
import net.kuina.magitech.energy.IManaStorage;
import net.kuina.magitech.energy.ManaTransfer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.kuina.magitech.block.base.MachineBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

/**
 * マナ加工機。
 *
 * <p>鉄インゴット＋マナ → 淡輝マナインゴット。マナは隣接タンクから自動補給され、
 * 携帯マナタンクを持って右クリックすれば手動でも補充できる。
 * 何も持たずに右クリックすると GUI が開く。</p>
 */
public class ManaProcessorBlock extends MachineBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    /** 携帯タンクから 1 回の右クリックで注入する量。 */
    private static final long MANUAL_FILL_PER_USE = 2_000L;

    public ManaProcessorBlock(BlockBehaviour.Properties properties) {
        super(properties.strength(3.5f).requiresCorrectToolForDrops());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ManaProcessorBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    /** サーバー側 ticker（加工・マナ補給を毎tick回す）。 */
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }
        return (lvl, pos, st, be) -> {
            if (be instanceof ManaProcessorBlockEntity machine) {
                ManaProcessorBlockEntity.tick((ServerLevel) lvl, pos, st, machine);
            }
        };
    }

    /** 手に持ったアイテムで右クリック：携帯マナタンクならマナを補充する。 */
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hit) {

        IManaStorage itemStorage = stack.getCapability(ManaCapabilities.MANA_ITEM);
        if (itemStorage == null) {
            // マナを持てないアイテムなら、素手と同じく GUI を開く処理へ
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        if (!(level.getBlockEntity(pos) instanceof ManaProcessorBlockEntity machine)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        long moved = ManaTransfer.move(itemStorage, machine.getManaPort(), MANUAL_FILL_PER_USE);
        player.displayClientMessage(Component.translatable("msg.magitech.mana_filled",
                moved, machine.getManaPort().getManaStored(), machine.getManaPort().getMaxMana()), true);
        return ItemInteractionResult.CONSUME;
    }

    /** 素手で右クリック：GUI を開く。 */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hit) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof ManaProcessorBlockEntity machine) {
            player.openMenu(machine, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    /** 破壊時：中の入出力アイテムを落とす。 */
    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof ManaProcessorBlockEntity machine) {
            ItemStackHandler inv = machine.getInventory();
            for (int slot = 0; slot < inv.getSlots(); slot++) {
                popResource(level, pos, inv.getStackInSlot(slot));
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
