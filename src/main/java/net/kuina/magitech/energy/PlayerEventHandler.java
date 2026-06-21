package net.kuina.magitech.energy;

import net.kuina.magitech.relic.PlayerRelicBoard;
import net.kuina.magitech.relic.RelicBoard;
import net.kuina.magitech.relic.RelicEffectsApplicator;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;

import static net.kuina.magitech.energy.PlayerEtherEnergy.getEnergy;
import static net.kuina.magitech.energy.PlayerEtherEnergy.setEnergy;

public class PlayerEventHandler {

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        PlayerDataHandler.loadEnergyData(player);

        RelicBoard board = PlayerRelicBoard.get(player);
        if (board != null) {
            RelicEffectsApplicator.apply(player, board.getTotalEffects());
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        PlayerDataHandler.saveEnergyData(player);
        PlayerRelicBoard.saveToNBT(player, PlayerRelicBoard.get(player));
        PlayerRelicBoard.removeFromCache(player);
    }
}
