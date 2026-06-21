package net.kuina.magitech.relic;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerRelicBoard {
    private static final String BOARD_KEY = "magitech_relic_board";

    private static final Map<UUID, RelicBoard> CACHE = new ConcurrentHashMap<>();

    public static RelicBoard get(Player player) {
        UUID uuid = player.getUUID();
        RelicBoard board = CACHE.get(uuid);
        if (board == null) {
            board = loadFromNBT(player);
            if (board == null) {
                board = new RelicBoard();
            }
            CACHE.put(uuid, board);
        }
        return board;
    }

    public static void markDirty(Player player) {
        RelicBoard board = CACHE.get(player.getUUID());
        if (board != null) {
            saveToNBT(player, board);
        }
    }

    public static void saveToNBT(Player player, RelicBoard board) {
        CompoundTag tag = player.getPersistentData();
        tag.put(BOARD_KEY, board.saveToNBT());
    }

    public static RelicBoard loadFromNBT(Player player) {
        CompoundTag tag = player.getPersistentData();
        if (tag.contains(BOARD_KEY)) {
            return RelicBoard.loadFromNBT(tag.getCompound(BOARD_KEY));
        }
        return null;
    }

    public static void removeFromCache(Player player) {
        CACHE.remove(player.getUUID());
    }
}
