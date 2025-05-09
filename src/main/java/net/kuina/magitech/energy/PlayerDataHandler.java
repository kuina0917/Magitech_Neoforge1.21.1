package net.kuina.magitech.energy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;

public class PlayerDataHandler {

    private static final String ENERGY_KEY = "etherEnergy"; // PlayerEtherEnergyと統一

    // プレイヤーエネルギーデータの保存
    public static void saveEnergyData(Player player) {
        long energy = PlayerEtherEnergy.getEnergy(player); // EtherEnergyStorage 経由で取得
        CompoundTag tag = player.getPersistentData();
        tag.putLong(ENERGY_KEY, energy); // 同じキー名で保存
        System.out.println("Saving energy to NBT: " + energy); // 保存されるエネルギーを表示
    }

    // プレイヤーエネルギーデータの読み込み
    public static void loadEnergyData(Player player) {
        CompoundTag tag = player.getPersistentData();
        if (tag.contains(ENERGY_KEY)) {
            long energy = tag.getLong(ENERGY_KEY);
            PlayerEtherEnergy.setEnergy(player, energy); // EtherEnergyStorage に反映
            System.out.println("Loaded energy from NBT: " + energy); // 読み込んだエネルギーを表示
        }
    }
}