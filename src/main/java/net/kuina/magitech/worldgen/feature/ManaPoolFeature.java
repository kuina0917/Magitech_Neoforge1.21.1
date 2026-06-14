package net.kuina.magitech.worldgen.feature;

import com.mojang.serialization.Codec;
import net.kuina.magitech.block.magitechblocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * 洞窟内に生成される「マナ溜まり」のフィーチャー。
 */
public class ManaPoolFeature extends Feature<NoneFeatureConfiguration> {
    public ManaPoolFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        BlockPos origin = context.origin();
        WorldGenLevel level = context.level();
        RandomSource random = context.random();

        // 1. 生成に最適な「床」を上下 32 ブロックの範囲で探す
        BlockPos pos = null;
        boolean forceFloor = random.nextDouble() < 0.8; // 8割の確率で床を優先

        for (int offset = 0; offset < 32; offset++) {
            BlockPos checkDown = origin.below(offset);
            BlockPos checkUp = origin.above(offset);
            
            // 床優先の場合は下（地面）を優先して探す
            if (isSuitableFloor(level, checkDown)) {
                pos = checkDown;
                break;
            }
            if (!forceFloor && isSuitableFloor(level, checkUp)) {
                pos = checkUp;
                break;
            }
        }

        if (pos == null) return false;

        // 2. 基本サイズの設定
        int baseRadiusX = 5 + random.nextInt(5);
        int baseRadiusZ = 5 + random.nextInt(5);
        int height = 2 + random.nextInt(3);

        // 3. 形状のランダム性
        double[] noiseOffsets = new double[360];
        for (int i = 0; i < 360; i++) noiseOffsets[i] = 0.8 + random.nextDouble() * 0.4;

        // 4. 空間の掘削とマナ流体の設置
        for (int y = -2; y <= height; y++) {
            // 高さに合わせて半径を広げ、階段状（スロープ）にする
            // 下ほど狭く、上ほど広い鉢状の形状
            double yFactor = (y + 2.0) / (height + 2.0); // 0.0 ~ 1.0
            double currentRadiusX = baseRadiusX * (0.7 + yFactor * 0.4);
            double currentRadiusZ = baseRadiusZ * (0.7 + yFactor * 0.4);

            for (int x = -baseRadiusX - 4; x <= baseRadiusX + 4; x++) {
                for (int z = -baseRadiusZ - 4; z <= baseRadiusZ + 4; z++) {
                    double angle = Math.atan2(z, x);
                    if (angle < 0) angle += Math.PI * 2;
                    int deg = (int) Math.toDegrees(angle) % 360;
                    double jitter = noiseOffsets[deg];

                    double dx = (double) x / (currentRadiusX * jitter);
                    double dz = (double) z / (currentRadiusZ * jitter);
                    double distSq = dx * dx + dz * dz;

                    if (distSq < 1.0) {
                        BlockPos target = pos.offset(x, y + 1, z); // y+1 することで、ループ内の y=-1 (液体最上段) が pos.getY() と一致するようにする
                        if (y < 0) {
                            level.setBlock(target, magitechblocks.MANA.get().defaultBlockState(), 2);
                            // 密閉とアクセント（マナの丸石を混ぜる）
                            sealLiquidWithAccents(level, target, random);
                            fillUnderneath(level, target);
                        } else {
                            level.setBlock(target, Blocks.AIR.defaultBlockState(), 2);
                        }
                    }
                }
            }
        }


        // 5. 周囲の侵食と地形の平滑化
        int erodeRange = 8;
        for (int x = -baseRadiusX - erodeRange; x <= baseRadiusX + erodeRange; x++) {
            for (int z = -baseRadiusZ - erodeRange; z <= baseRadiusZ + erodeRange; z++) {
                for (int y = -3; y <= height + 3; y++) {
                    BlockPos target = pos.offset(x, y, z);
                    BlockState state = level.getBlockState(target);

                    double dx = (double) x / (baseRadiusX + erodeRange);
                    double dz = (double) z / (baseRadiusZ + erodeRange);
                    double dy = (double) (y < 0 ? y : (y > height ? y - height : 0)) / erodeRange;
                    double distSq = dx * dx + dz * dz + dy * dy;

                    if (distSq < 1.0) {
                        // 侵食：液体や空気に隣接するブロックを変換
                        if (isAdjacentToManaOrAir(level, target)) {
                             if (isReplaceableByManaStone(state)) {
                                if (random.nextDouble() < (1.0 - distSq) * 0.9) {
                                    level.setBlock(target, magitechblocks.MANA_STONE.get().defaultBlockState(), 2);
                                }
                             }
                        }

                        // 地形の平滑化：不自然な空洞を高度に合わせた石で埋める
                        if (y < -2 && level.getBlockState(target).isAir() && distSq < 0.7) {
                             level.setBlock(target, target.getY() < 0 ? Blocks.DEEPSLATE.defaultBlockState() : Blocks.STONE.defaultBlockState(), 2);
                        }
                    }
                }
            }
        }

        return true;
    }

    /** 液体の周囲をマナストーンで完全に密閉する（アクセント付き） */
    private void sealLiquidWithAccents(WorldGenLevel level, BlockPos pos, net.minecraft.util.RandomSource random) {
        for (net.minecraft.core.Direction dir : net.minecraft.core.Direction.values()) {
            BlockPos adjPos = pos.relative(dir);
            BlockState adjState = level.getBlockState(adjPos);
            
            if (adjState.isAir() || isReplaceableByManaStone(adjState)) {
                if (dir == net.minecraft.core.Direction.UP) continue;

                // 20% の確率でマナの丸石をアクセントとして混ぜる
                BlockState state = random.nextDouble() < 0.2 ? magitechblocks.MANA_COBBLESTONE.get().defaultBlockState() : magitechblocks.MANA_STONE.get().defaultBlockState();
                level.setBlock(adjPos, state, 2);
            }
        }
    }

    /** 液体の周囲をマナストーンで完全に密閉する */
    private void sealLiquid(WorldGenLevel level, BlockPos pos) {
        for (net.minecraft.core.Direction dir : net.minecraft.core.Direction.values()) {
            BlockPos adjPos = pos.relative(dir);
            BlockState adjState = level.getBlockState(adjPos);
            
            // 隣接が空気または置き換え可能なブロックなら、マナストーンで壁を作る
            if (adjState.isAir() || isReplaceableByManaStone(adjState)) {
                // ただし、液体の真上だけは空気のままにする（水面を確保）
                if (dir == net.minecraft.core.Direction.UP) continue;
                
                level.setBlock(adjPos, magitechblocks.MANA_STONE.get().defaultBlockState(), 2);
            }
        }
    }

    /** 湖の底の下を一番近い床まで埋める */
    private void fillUnderneath(WorldGenLevel level, BlockPos pos) {
        BlockPos.MutableBlockPos mutable = pos.mutable().move(0, -1, 0);
        int maxDepth = 12;
        BlockState fillState = pos.getY() < 0 ? Blocks.DEEPSLATE.defaultBlockState() : Blocks.STONE.defaultBlockState();
        
        while (maxDepth > 0 && level.getBlockState(mutable).isAir()) {
            level.setBlock(mutable, fillState, 2);
            mutable.move(0, -1, 0);
            maxDepth--;
        }
    }

    private boolean isReplaceableByManaStone(BlockState state) {
        return state.is(Blocks.STONE) || state.is(Blocks.DEEPSLATE) || 
               state.is(Blocks.DIRT) || state.is(Blocks.GRAVEL) ||
               state.is(Blocks.TUFF) || state.is(Blocks.ANDESITE) ||
               state.is(Blocks.DIORITE) || state.is(Blocks.GRANITE);
    }

    private boolean isAdjacentToManaOrAir(WorldGenLevel level, BlockPos pos) {
        for (net.minecraft.core.Direction dir : net.minecraft.core.Direction.values()) {
            BlockState adj = level.getBlockState(pos.relative(dir));
            if (adj.is(magitechblocks.MANA.get()) || adj.isAir()) return true;
        }
        return false;
    }

    private boolean isSuitableFloor(WorldGenLevel level, BlockPos pos) {
        if (pos.getY() <= level.getMinBuildHeight() + 10 || pos.getY() >= level.getMaxBuildHeight() - 10) return false;
        return level.getBlockState(pos.below()).isSolid() && level.getBlockState(pos).isAir();
    }
}
