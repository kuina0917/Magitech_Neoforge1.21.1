package net.kuina.magitech.block.custom;

import net.kuina.magitech.block.base.ManaContainerBlockEntity;
import net.kuina.magitech.block.magitechblockentities;
import net.kuina.magitech.block.magitechblocks;
import net.kuina.magitech.capability.ManaCapabilities;
import net.kuina.magitech.energy.IManaStorage;
import net.kuina.magitech.energy.ManaTransfer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * マナ抽出機コアのロジック。
 * 3x3x3 の構造体を認識すると、大気中から極少量のマナを抽出する。
 * 周囲の「住所（座標）」を登録して、特定地点へマナを無線転送できる。
 */
public class ManaExtractorCoreBlockEntity extends ManaContainerBlockEntity {
    private int checkTimer = 0;
    private int scanTimer = 0;
    
    public static final long GEN_RATE = 1L;
    private static final long MAX_CAPACITY = 10_000L;
    
    // 統計: 1秒(20tick)あたりの生成量・転送量
    private long manaGenPerSecond = 0;
    private long manaTransferPerSecond = 0;
    private int statTimer = 0;
    private long accumulatedGen = 0;
    private long accumulatedTransfer = 0;
    
    // 転送先として有効化されている座標リスト
    private final Set<BlockPos> targetAddresses = new HashSet<>();
    // スキャンで見つかった候補リスト（GUI表示用）
    private final List<BlockPos> foundAddresses = new ArrayList<>();

    public ManaExtractorCoreBlockEntity(BlockPos pos, BlockState state) {
        super(magitechblockentities.MANA_EXTRACTOR_CORE_ENTITY.get(), pos, state, MAX_CAPACITY);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        // 1. 構造チェック (2秒ごと)
        if (++checkTimer >= 40) {
            checkTimer = 0;
            if (getBlockState().getValue(ManaExtractorCoreBlock.FORMED)) {
                if (!checkStructure()) {
                    level.setBlock(worldPosition, getBlockState().setValue(ManaExtractorCoreBlock.FORMED, false), 3);
                }
            }
        }

        // 2. 周囲のスキャン (5秒ごと)
        if (++scanTimer >= 100) {
            scanTimer = 0;
            if (getBlockState().getValue(ManaExtractorCoreBlock.FORMED)) {
                scanNearbyManaBlocks();
            }
        }

        if (getBlockState().getValue(ManaExtractorCoreBlock.FORMED)) {
            // 3. マナの抽出
            if (mana.getManaStored() < MAX_CAPACITY) {
                long before = mana.getManaStored();
                mana.insertMana(GEN_RATE, false);
                accumulatedGen += mana.getManaStored() - before;
            }
            
            // 4. 登録された「住所」へマナを転送
            if (mana.getManaStored() > 0) {
                transferManaToAddresses();
            }
        }

        // 5. 統計更新（1秒ごと）
        if (++statTimer >= 20) {
            statTimer = 0;
            manaGenPerSecond = accumulatedGen;
            manaTransferPerSecond = accumulatedTransfer;
            accumulatedGen = 0;
            accumulatedTransfer = 0;
        }
    }

    /** 半径 8 ブロック以内のマナ対応ブロックを探す */
    private void scanNearbyManaBlocks() {
        foundAddresses.clear();
        int radius = 8;
        for (BlockPos target : BlockPos.betweenClosed(worldPosition.offset(-radius, -radius, -radius), worldPosition.offset(radius, radius, radius))) {
            if (target.equals(worldPosition)) continue;
            
            IManaStorage storage = level.getCapability(ManaCapabilities.MANA_BLOCK, target, null);
            if (storage != null && storage.canReceive()) {
                foundAddresses.add(target.immutable());
            }
        }
    }

    private void transferManaToAddresses() {
        if (targetAddresses.isEmpty()) return;

        int targets = targetAddresses.size();
        long totalToSend = Math.min(10L * targets, mana.getManaStored());
        if (totalToSend <= 0) return;

        long base = totalToSend / targets;
        if (base <= 0) return;

        long remainder = totalToSend % targets;

        int idx = 0;
        for (BlockPos targetPos : targetAddresses) {
            IManaStorage targetStorage = level.getCapability(ManaCapabilities.MANA_BLOCK, targetPos, null);
            if (targetStorage == null || !targetStorage.canReceive()) { idx++; continue; }

            long share = base + (idx < remainder ? 1 : 0);
            idx++;

            long taken = mana.extractMana(share, false);
            if (taken <= 0) continue;

            long accepted = targetStorage.insertMana(taken, true);
            if (accepted > 0) {
                targetStorage.insertMana(accepted, false);
            }
            long refund = taken - accepted;
            if (refund > 0) {
                mana.insertMana(refund, false);
            }
            accumulatedTransfer += accepted;
        }
    }

    public List<BlockPos> getFoundAddresses() { return foundAddresses; }
    public boolean isTargetEnabled(BlockPos pos) { return targetAddresses.contains(pos); }
    
    public long getManaStored() { return mana.getManaStored(); }
    public long getMaxCapacity() { return MAX_CAPACITY; }
    public long getManaGenPerSecond() { return manaGenPerSecond; }
    public long getManaTransferPerSecond() { return manaTransferPerSecond; }
    
    public void toggleTarget(BlockPos pos) {
        if (targetAddresses.contains(pos)) {
            targetAddresses.remove(pos);
        } else {
            targetAddresses.add(pos);
        }
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ListTag list = new ListTag();
        for (BlockPos pos : targetAddresses) {
            CompoundTag posTag = new CompoundTag();
            posTag.put("pos", NbtUtils.writeBlockPos(pos));
            list.add(posTag);
        }
        tag.put("Targets", list);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        targetAddresses.clear();
        if (tag.contains("Targets", Tag.TAG_LIST)) {
            ListTag list = tag.getList("Targets", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag entry = list.getCompound(i);
                NbtUtils.readBlockPos(entry, "pos").ifPresent(targetAddresses::add);
            }
        }
    }

    /**
     * コアを前面中央とした 3x3x3 の構造をチェック。
     * コアの向き(FACING)に基づいて、背後に 3x3x3 の立方体があるか確認する。
     */
    public boolean checkStructure() {
        if (level == null) return false;

        // コアの向きを取得
        net.minecraft.core.Direction facing = getBlockState().getValue(ManaExtractorCoreBlock.FACING);
        // 背後方向（立方体があるべき方向）
        net.minecraft.core.Direction back = facing.getOpposite();
        // 右方向（前面から見て）
        net.minecraft.core.Direction right = facing.getClockWise();

        // コアの位置から見て、立方体の中心位置を計算
        // コアは前面中央なので、1ブロック奥が立方体の中心
        BlockPos centerPos = worldPosition.relative(back);

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    // 立方体の中の相対座標を、ワールド座標に変換
                    // x: 横方向, y: 高さ方向, z: 奥行方向
                    BlockPos targetPos = centerPos
                            .relative(right, x)
                            .relative(net.minecraft.core.Direction.UP, y)
                            .relative(back, z);

                    // コア自身の位置はチェックから除外
                    if (targetPos.equals(worldPosition)) continue;

                    if (!level.getBlockState(targetPos).is(magitechblocks.MANA_EXTRACTOR_CASING.get())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}

