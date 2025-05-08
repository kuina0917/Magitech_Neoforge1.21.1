package net.kuina.magitech.energy;


import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerEtherEnergy {
    private static final int DEFAULT_CAPACITY = 100;

    // プレイヤーのUUIDとエネルギーを紐づける
    private static final HashMap<UUID, EtherEnergyStorage> playerEnergyMap = new HashMap<>();

    public static EtherEnergyStorage get(Player player) {
        return playerEnergyMap.computeIfAbsent(player.getUUID(), uuid -> new EtherEnergyStorage(DEFAULT_CAPACITY));
    }

    public static void set(Player player, EtherEnergyStorage storage) {
        playerEnergyMap.put(player.getUUID(), storage);
    }

    public static void reset(Player player) {
        playerEnergyMap.remove(player.getUUID());
    }

    public static void clearAll() {
        playerEnergyMap.clear();
    }
}
