package net.kuina.magitech.util;

import net.kuina.magitech.capability.ManaCapabilities;
import net.kuina.magitech.energy.IManaStorage;
import net.kuina.magitech.energy.ManaTransfer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * マナ（Capability）まわりの定番処理をまとめたユーティリティ。
 *
 * <p><b>何をするもの:</b> 指定した位置のマナ貯蔵窓口（{@link IManaStorage}）を取り出したり、
 * 自分のマナを隣接ブロックへ配ったりする。「装置から別の装置へ送る搬出機能」の土台になる。</p>
 *
 * <p><b>使い方（隣接装置への自動搬出）:</b> ブロックエンティティの tick から呼ぶだけ。</p>
 * <pre>{@code
 * public static void tick(ServerLevel level, BlockPos pos, BlockState state, MyMachineBlockEntity self) {
 *     // 自分のマナを、毎tick最大100ずつ隣のタンク/装置へ送り出す
 *     ManaHelper.pushToNeighbors(level, pos, self.getManaStorage(), 100);
 * }
 * }</pre>
 *
 * <p><b>使い方（特定方向の相手を取得）:</b></p>
 * <pre>{@code
 * IManaStorage above = ManaHelper.getManaStorage(level, pos.above(), Direction.DOWN);
 * if (above != null) { ... }
 * }</pre>
 */
public final class ManaHelper {

    private ManaHelper() {
    }

    /**
     * 指定位置・指定面のマナ貯蔵窓口を取得する。
     *
     * @param side 相手ブロックの「どの面から」アクセスするか。未ロード時や窓口が無い場合は null。
     */
    @Nullable
    public static IManaStorage getManaStorage(Level level, BlockPos pos, Direction side) {
        if (level == null || !level.isLoaded(pos)) {
            return null;
        }
        return level.getCapability(ManaCapabilities.MANA_BLOCK, pos, side);
    }

    /**
     * 自分のマナを 6 方向すべての隣接ブロックへ、各面ごとに最大 {@code maxPerSide} 単位ずつ送り出す。
     *
     * <p>相手の受け入れ可能量に応じて自動で調整されるので、あふれや過剰消費は起きない。</p>
     *
     * @param source     送り出す側のマナ貯蔵（通常は自分のブロックエンティティのもの）
     * @param maxPerSide 1 面あたりに送る最大量
     * @return 実際に送り出した合計量
     */
    public static long pushToNeighbors(Level level, BlockPos pos, IManaStorage source, long maxPerSide) {
        if (level == null || source == null || maxPerSide <= 0) {
            return 0;
        }
        long total = 0;
        for (Direction dir : Direction.values()) {
            // 相手から見ると、自分は dir の反対側の面に接している
            IManaStorage neighbor = getManaStorage(level, pos.relative(dir), dir.getOpposite());
            if (neighbor == null) {
                continue;
            }
            total += ManaTransfer.move(source, neighbor, maxPerSide);
        }
        return total;
    }

    /**
     * 6 方向すべての隣接ブロックから、各面ごとに最大 {@code maxPerSide} 単位ずつマナを引き込む。
     *
     * <p>マナを消費する装置（加工機など）が、隣のタンクから燃料を自動補給するのに使う。</p>
     *
     * @param destination 受け取る側のマナ貯蔵（通常は自分のブロックエンティティのもの）
     * @param maxPerSide  1 面あたりに引き込む最大量
     * @return 実際に引き込んだ合計量
     */
    public static long pullFromNeighbors(Level level, BlockPos pos, IManaStorage destination, long maxPerSide) {
        if (level == null || destination == null || maxPerSide <= 0) {
            return 0;
        }
        long total = 0;
        for (Direction dir : Direction.values()) {
            IManaStorage neighbor = getManaStorage(level, pos.relative(dir), dir.getOpposite());
            if (neighbor == null) {
                continue;
            }
            total += ManaTransfer.move(neighbor, destination, maxPerSide);
        }
        return total;
    }
}
