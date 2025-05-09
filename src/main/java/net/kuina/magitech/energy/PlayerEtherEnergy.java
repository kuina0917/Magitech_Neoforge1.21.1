package net.kuina.magitech.energy;

import net.kuina.magitech.energy.custom.EtherEnergyStorage;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * プレイヤーごとの EtherEnergyStorage を手軽に取得・操作するユーティリティ。
 */
public final class PlayerEtherEnergy {

    /** デフォルトの最大容量 */
    public static final long DEFAULT_CAPACITY = 100L;

    /** UUID → ストレージ */
    private static final Map<UUID, EtherEnergyStorage> CACHE = new ConcurrentHashMap<>();

    private PlayerEtherEnergy() {} // インスタンス化禁止

    /* --------------------------------------------------------------------- */
    /*  基本操作                                                         */

    // プレイヤーのエネルギーを取得（新規作成しない場合は永続化されたデータを取得）
    public static EtherEnergyStorage get(Player player) {
        UUID uuid = player.getUUID();
        EtherEnergyStorage storage = CACHE.get(uuid);
        if (storage == null) {
            storage = loadStorageFromNBT(player); // NBTデータから読み込み
            if (storage == null) {
                storage = new EtherEnergyStorage(DEFAULT_CAPACITY);
            }
            CACHE.put(uuid, storage);
        }
        return storage;
    }

    // プレイヤーのエネルギーをNBTデータに保存
    private static void saveToNBT(Player player, EtherEnergyStorage storage) {
        CompoundTag tag = player.getPersistentData();
        tag.putLong("etherEnergy", storage.getEnergy()); // energyをlong型で保存
    }

    // NBTデータからエネルギーを読み込む
    private static EtherEnergyStorage loadStorageFromNBT(Player player) {
        CompoundTag tag = player.getPersistentData();
        if (tag.contains("etherEnergy")) {
            long energy = tag.getLong("etherEnergy");
            return new EtherEnergyStorage(energy);
        }
        return null;
    }

    // プレイヤーのエネルギーを消費
    public static boolean tryConsume(Player player, long amount) {
        EtherEnergyStorage storage = get(player);
        if (storage.getEnergy() >= amount) {
            storage.consume(amount);  // 変更後のメソッド呼び出し
            saveToNBT(player, storage);  // NBTデータを保存
            return true;
        }
        return false;
    }

    // エネルギーを充填
    public static void fill(Player player) {
        EtherEnergyStorage storage = get(player);
        storage.fill();  // 変更後のメソッド呼び出し
        saveToNBT(player, storage);  // NBTデータを保存
    }

    // プレイヤーのエネルギー量を取得
    public static long getEnergy(Player player) {
        EtherEnergyStorage storage = get(player);
        return storage.getEnergy();
    }

    // プレイヤーのエネルギー量を設定
    public static void setEnergy(Player player, long energy) {
        EtherEnergyStorage storage = get(player);
        storage.addEnergy(energy - storage.getEnergy()); // エネルギーを設定
        saveToNBT(player, storage);  // NBTデータを保存
    }
}
