package net.kuina.magitech.block.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

/**
 * 機械ブロックの基底クラス。
 * 破壊時に BlockEntity のデータ ({@link BlockEntity#saveToItem}) を
 * ドロップ品の NBT に保存し、再設置時に自動復元する。
 *
 * <p>利用例：
 * <pre>{@code
 * public class MyMachineBlock extends MachineBlock {
 *     public MyMachineBlock(Properties props) { super(props); }
 *
 *     public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { ... }
 *     public <T extends BlockEntity> BlockEntityTicker<T> getTicker(...) { ... }
 * }}</pre>
 */
public abstract class MachineBlock extends Block implements EntityBlock {

    public MachineBlock(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        ItemStack stack = new ItemStack(this);
        BlockEntity be = level.getBlockEntity(pos);
        if (be != null) {
            be.saveToItem(stack, level.registryAccess());
        }
        return stack;
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity be, ItemStack tool) {
        if (!level.isClientSide) {
            ItemStack stack = new ItemStack(this);
            if (be != null) {
                be.saveToItem(stack, level.registryAccess());
            }
            Block.popResource(level, pos, stack);
        }
        super.playerDestroy(level, player, pos, state, be, tool);
    }
}
