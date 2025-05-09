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

    public static final long DEFAULT_CAPACITY = 100L;

    private static final Map<UUID, EtherEnergyStorage> CACHE = new ConcurrentHashMap<>();

    // プレイヤーのエネルギーを取得
    public static EtherEnergyStorage get(Player player) {
        UUID uuid = player.getUUID();
        EtherEnergyStorage storage = CACHE.get(uuid);
        if (storage == null) {
            // NBT からエネルギーを読み込む
            storage = loadStorageFromNBT(player);
            if (storage == null) {
                // エネルギーが保存されていない場合は 0 に設定して初期化
                storage = new EtherEnergyStorage(0, DEFAULT_CAPACITY);  // 初期エネルギーは 0 に設定
            }
            CACHE.put(uuid, storage);
        }
        return storage;
    }

    // プレイヤーのエネルギーを追加
    public static void addEnergy(Player player, long amount) {
        EtherEnergyStorage storage = get(player);
        System.out.println("Before adding energy: " + storage.getEnergy()); // 追加前のエネルギーを表示
        storage.addEnergy(amount); // エネルギー追加
        System.out.println("After adding energy: " + storage.getEnergy()); // 追加後のエネルギーを表示
        saveToNBT(player, storage); // 保存
    }

    // プレイヤーのエネルギーを消費
    public static boolean tryConsume(Player player, long amount) {
        EtherEnergyStorage storage = get(player);
        if (storage.getEnergy() >= amount) {
            storage.consume(amount); // エネルギー消費
            saveToNBT(player, storage); // 保存
            return true;
        }
        return false;
    }

    // プレイヤーのエネルギーを設定
    public static void setEnergy(Player player, long energy) {
        EtherEnergyStorage storage = get(player);
        storage.addEnergy(energy - storage.getEnergy()); // 設定
        saveToNBT(player, storage); // 保存
    }

    // プレイヤーのエネルギー量を取得
    public static long getEnergy(Player player) {
        EtherEnergyStorage storage = get(player);
        return storage.getEnergy();
    }

    public static void resetEnergy(Player player) {
        EtherEnergyStorage storage = get(player);
        long amountToAdd = storage.getCapacity() - storage.getEnergy(); // 回復する量を計算
        storage.addEnergy(amountToAdd); // 必要な分だけ加算
        saveToNBT(player, storage); // NBTに保存
    }

    public static void saveToNBT(Player player, EtherEnergyStorage storage) {
        CompoundTag tag = player.getPersistentData();
        tag.putLong("etherEnergy", storage.getEnergy());
        System.out.println("Saving energy to NBT: " + storage.getEnergy()); // NBTに保存するエネルギーを表示
    }
    // NBTデータからエネルギーを読み込む
    private static EtherEnergyStorage loadStorageFromNBT(Player player) {
        CompoundTag tag = player.getPersistentData();
        if (tag.contains("etherEnergy")) {
            long energy = tag.getLong("etherEnergy");
            return new EtherEnergyStorage(energy, DEFAULT_CAPACITY); // energyとcapacityの両方を渡す
        }
        return null;
    }
}
