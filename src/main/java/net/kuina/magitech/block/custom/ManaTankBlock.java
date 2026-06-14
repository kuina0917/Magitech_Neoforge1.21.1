package net.kuina.magitech.block.custom;

import net.kuina.magitech.block.base.MachineBlock;
import net.kuina.magitech.capability.ManaCapabilities;
import net.kuina.magitech.energy.IManaStorage;
import net.kuina.magitech.energy.ManaTransfer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * マナ貯蔵タンク。
 *
 * <p>内部にマナを貯め、{@link ManaCapabilities#MANA_BLOCK} として公開する。
 * 動作確認用に、手に持った携帯マナタンクで右クリックするとタンク→アイテムへ
 * マナを移し、素手で右クリックすると現在の貯蔵量を表示する。</p>
 */
public class ManaTankBlock extends MachineBlock {

    /** 1 回の操作で移動させるマナ量。 */
    private static final long TRANSFER_PER_USE = 1_000L;

    public ManaTankBlock(BlockBehaviour.Properties properties) {
        super(properties.strength(3f).requiresCorrectToolForDrops());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ManaTankBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    /** 手に持ったアイテム（携帯マナタンクなど）で右クリックしたとき。 */
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hit) {

        IManaStorage itemStorage = stack.getCapability(ManaCapabilities.MANA_ITEM);
        if (itemStorage == null) {
            // マナを保持できないアイテムなら通常の処理（素手扱い）に任せる
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }

        if (!(level.getBlockEntity(pos) instanceof ManaTankBlockEntity tank)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        // タンク → 携帯アイテム へマナを移す
        long moved = ManaTransfer.move(tank.getManaStorage(), itemStorage, TRANSFER_PER_USE);
        player.displayClientMessage(Component.translatable("msg.magitech.mana_transferred",
                moved, itemStorage.getManaStored(), itemStorage.getMaxMana()), true);
        return ItemInteractionResult.CONSUME;
    }

    /** 素手で右クリックしたとき。貯蔵量を表示する。 */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hit) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof ManaTankBlockEntity tank) {
            IManaStorage storage = tank.getManaStorage();
            player.displayClientMessage(Component.translatable("msg.magitech.mana_stored",
                    storage.getManaStored(), storage.getMaxMana()), true);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> net.minecraft.world.level.block.entity.BlockEntityTicker<T> getTicker(
            Level level, BlockState state, net.minecraft.world.level.block.entity.BlockEntityType<T> type) {
        return null; // 今は自動処理なし（将来、隣接装置への搬出などを追加する余地）
    }
}
