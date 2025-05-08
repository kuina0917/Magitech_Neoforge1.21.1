package net.kuina.magitech.energy;

import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * プレイヤーごとの EtherEnergyStorage を手軽に取得・操作するユーティリティ。
 *
 * - get(...)             : 必ずストレージを返す（存在しなければ生成）
 * - setCapacity(...)     : 最大容量だけを変更（値は現在量ごとリセット）
 * - fill(...)            : 満タンにする
 * - tryConsume(...)      : 指定量あれば消費して true を返す
 * - getEnergy(...)       : 現在量だけ取得
 */
public final class PlayerEtherEnergy {

    /** デフォルトの最大容量 */
    public static final int DEFAULT_CAPACITY = 100;

    /** UUID → ストレージ */
    private static final Map<UUID, EtherEnergyStorage> CACHE = new ConcurrentHashMap<>();

    private PlayerEtherEnergy() {} // インスタンス化禁止

    /* --------------------------------------------------------------------- */
    /*  基本操作                                                              */
    /* --------------------------------------------------------------------- */

    public static EtherEnergyStorage get(Player player) {
        return CACHE.computeIfAbsent(player.getUUID(),
                id -> new EtherEnergyStorage(DEFAULT_CAPACITY));
    }

    public static void reset(Player player) {
        CACHE.remove(player.getUUID());
    }

    public static void clearAll() {
        CACHE.clear();
    }

    /* --------------------------------------------------------------------- */
    /*  よく使うショートカット                                                */
    /* --------------------------------------------------------------------- */

    /** 満タンにする */
    public static void fill(Player player) {
        get(player).resetEnergy();
    }

    /** 残量を問い合わせだけしたい場合 */
    public static int getEnergy(Player player) {
        return get(player).getEnergy();
    }

    /**
     * 指定量消費を試みる。<br>
     * 残っていれば減算して true、足りなければ何もせず false。
     */
    public static boolean tryConsume(Player player, int amount) {
        EtherEnergyStorage storage = get(player);
        if (storage.hasEnergy(amount)) {
            storage.removeEnergy(amount);
            return true;
        }
        return false;
    }

    /**
     * 容量だけ変えたいときに呼ぶ。<br>
     * （今はテスト用途なので “容量＝初期残量” でリセット実装）
     */
    public static void setCapacity(Player player, int newCapacity) {
        CACHE.put(player.getUUID(), new EtherEnergyStorage(newCapacity));
    }
}