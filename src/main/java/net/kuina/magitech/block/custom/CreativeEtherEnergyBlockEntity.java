package net.kuina.magitech.block.custom;

import net.kuina.magitech.block.MagitechBlockEntities;
import net.kuina.magitech.energy.IEtherEnergyReceiver;
import net.kuina.magitech.energy.IManaStorage;
import net.kuina.magitech.energy.PlayerEtherEnergy;
import net.kuina.magitech.util.ManaHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class CreativeEtherEnergyBlockEntity extends BlockEntity {

    private static final int RADIUS = 5;

    private static final IManaStorage INFINITE_MANA = new IManaStorage() {
        @Override public long getManaStored() { return Long.MAX_VALUE; }
        @Override public long getMaxMana() { return Long.MAX_VALUE; }
        @Override public long insertMana(long amount, boolean simulate) { return amount; }
        @Override public long extractMana(long amount, boolean simulate) { return amount; }
        @Override public boolean canReceive() { return false; }
        @Override public boolean canExtract() { return true; }
    };

    public CreativeEtherEnergyBlockEntity(BlockPos pos, BlockState state) {
        super(MagitechBlockEntities.CREATIVE_ETHER_ENERGY_BLOCK_ENTITY.get(), pos, state);
    }

    public static void tick(ServerLevel level, BlockPos pos, BlockState state,
                            CreativeEtherEnergyBlockEntity self) {

        // ① 周囲のエネルギーレシーバーにフルチャージ
        BlockPos.betweenClosed(pos.offset(-RADIUS, -RADIUS, -RADIUS),
                        pos.offset(RADIUS, RADIUS, RADIUS))
                .forEach(targetPos -> {
                    if (targetPos.equals(pos)) return; // 自分自身は無視
                    BlockEntity be = level.getBlockEntity(targetPos);
                    if (be instanceof IEtherEnergyReceiver receiver) {
                        receiver.getEtherStorage().resetEnergy(); // フルチャージ
                        be.setChanged(); // NBT 保存用
                    }
                });

        // ② プレイヤーへのエネルギー供給
        AABB area = new AABB(pos).inflate(RADIUS);
        level.getEntitiesOfClass(Player.class, area)
                .forEach(player -> {
                    long currentEnergy = PlayerEtherEnergy.getEnergy(player);
                    long maxEnergy = PlayerEtherEnergy.get(player).getCapacity();
                    long amountToAdd = maxEnergy - currentEnergy; // どれだけエネルギーを回復するか計算
                    PlayerEtherEnergy.addEnergy(player, amountToAdd); // エネルギーを加算
                    System.out.println("Energy added to player: " + amountToAdd); // ログで確認
                });

        // ③ 隣接ブロックにマナを無限供給（毎tick最大1000/side）
        ManaHelper.pushToNeighbors(level, pos, INFINITE_MANA, 1000L);
    }}
