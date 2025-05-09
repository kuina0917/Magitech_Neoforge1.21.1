package net.kuina.magitech.energy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;

import static net.kuina.magitech.energy.PlayerEtherEnergy.getEnergy;
import static net.kuina.magitech.energy.PlayerEtherEnergy.setEnergy;

public class PlayerEventHandler {

    // プレイヤーがログインしたときにエネルギーデータを読み込む
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        PlayerDataHandler.loadEnergyData(player);
        System.out.println("Player logged in, energy loaded: " + PlayerEtherEnergy.getEnergy(player)); // ログイン時のエネルギーを表示
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        PlayerDataHandler.saveEnergyData(player);
        System.out.println("Player logged out, energy saved: " + PlayerEtherEnergy.getEnergy(player)); // ログアウト時のエネルギーを表示
    }}