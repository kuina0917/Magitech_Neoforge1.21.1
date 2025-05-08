package net.kuina.magitech.block.custom;

import net.kuina.magitech.block.magitechblockentities;
import net.kuina.magitech.energy.IEtherEnergyReceiver;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 置くだけで周囲 5×5×5 に無限にエネルギーを配る BE
 */
public class CreativeEtherEnergyBlockEntity extends BlockEntity {

    /** 立方体半径（±2 ＝ 5 ブロック分） */
    private static final int RADIUS = 2;

    public CreativeEtherEnergyBlockEntity(BlockPos pos, BlockState state) {
        super(magitechblockentities.CREATIVE_ETHER_ENERGY_BLOCK_ENTITY.get(), pos, state);
    }

    /** サーバー側で毎 tick 呼ばれる */
    public static void tick(ServerLevel level, BlockPos pos, BlockState state,
                            CreativeEtherEnergyBlockEntity self) {

        BlockPos.betweenClosed(pos.offset(-RADIUS, -RADIUS, -RADIUS),
                        pos.offset( RADIUS,  RADIUS,  RADIUS))
                .forEach(targetPos -> {
                    if (targetPos.equals(pos)) return;           // 自分自身は無視
                    BlockEntity be = level.getBlockEntity(targetPos);
                    if (be instanceof IEtherEnergyReceiver receiver) {
                        receiver.getEtherStorage().resetEnergy(); // フルチャージ
                        be.setChanged();                         // NBT 保存用
                    }
                });

        // ② プレイヤーへの給電
        level.getEntitiesOfClass(net.minecraft.world.entity.player.Player.class,
                        new net.minecraft.world.phys.AABB(pos).inflate(RADIUS))
                .forEach(p -> net.kuina.magitech.energy.PlayerEtherEnergy.get(p).resetEnergy());
    }
}
