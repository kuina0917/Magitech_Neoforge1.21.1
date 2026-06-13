package net.kuina.magitech.energy;

/**
 * マナの転送ユーティリティ。
 * 2 つの {@link IManaStorage} 間でマナを安全に移動させる。
 */
public final class ManaTransfer {

    private ManaTransfer() {
    }

    /**
     * {@code from} から {@code to} へ最大 {@code max} 単位のマナを移動する。
     *
     * <p>「取り出せる量」と「受け取れる量」の小さい方だけを実際に動かすので、
     * あふれたり過剰に減ったりしない。</p>
     *
     * @return 実際に移動した量
     */
    public static long move(IManaStorage from, IManaStorage to, long max) {
        if (from == null || to == null || max <= 0) {
            return 0;
        }
        if (!from.canExtract() || !to.canReceive()) {
            return 0;
        }

        // 取り出せる量と受け取れる量を試算し、小さい方を実際に動かす
        long extractable = from.extractMana(max, true);
        if (extractable <= 0) {
            return 0;
        }
        long movable = to.insertMana(extractable, true);
        if (movable <= 0) {
            return 0;
        }

        from.extractMana(movable, false);
        to.insertMana(movable, false);
        return movable;
    }
}
