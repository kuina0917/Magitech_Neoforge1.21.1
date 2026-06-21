package net.kuina.magitech.energy;

import net.kuina.magitech.capability.ManaCapabilities;
import net.kuina.magitech.energy.custom.EtherEnergyStorage;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PlayerEtherEnergy {

    public static final long DEFAULT_CAPACITY = 1000L;

    private static final Map<UUID, EtherEnergyStorage> CACHE = new ConcurrentHashMap<>();

    public static EtherEnergyStorage get(Player player) {
        UUID uuid = player.getUUID();
        EtherEnergyStorage storage = CACHE.get(uuid);
        if (storage == null) {
            storage = loadStorageFromNBT(player);
            if (storage == null) {
                storage = new EtherEnergyStorage(0, DEFAULT_CAPACITY);
            }
            CACHE.put(uuid, storage);
        }
        return storage;
    }

    public static void addEnergy(Player player, long amount) {
        EtherEnergyStorage storage = get(player);
        storage.addEnergy(amount);
        saveToNBT(player, storage);
    }

    public static void increaseCapacity(Player player, long amount) {
        EtherEnergyStorage storage = get(player);
        long newCapacity = storage.getCapacity() + amount;
        storage.setCapacity(newCapacity);
        saveToNBT(player, storage);
    }

    public static boolean tryConsume(Player player, long amount) {
        if (!hasManaContainer(player)) return false;
        if (getTotalMana(player) < amount) return false;

        long remaining = amount;

        EtherEnergyStorage storage = get(player);
        long fromPersonal = Math.min(remaining, storage.getEnergy());
        if (fromPersonal > 0) {
            storage.consume(fromPersonal);
            remaining -= fromPersonal;
            saveToNBT(player, storage);
        }

        remaining = extractFromContainers(player, remaining);

        return true;
    }

    public static void setEnergy(Player player, long energy) {
        EtherEnergyStorage storage = get(player);
        storage.addEnergy(energy - storage.getEnergy());
        saveToNBT(player, storage);
    }

    public static long getEnergy(Player player) {
        EtherEnergyStorage storage = get(player);
        return storage.getEnergy();
    }

    public static void resetEnergy(Player player) {
        EtherEnergyStorage storage = get(player);
        long amountToAdd = storage.getCapacity() - storage.getEnergy();
        storage.addEnergy(amountToAdd);
        saveToNBT(player, storage);
    }

    public static void saveToNBT(Player player, EtherEnergyStorage storage) {
        CompoundTag tag = player.getPersistentData();
        tag.putLong("etherEnergy", storage.getEnergy());
        tag.putLong("etherCapacity", storage.getCapacity());
    }

    public static EtherEnergyStorage loadStorageFromNBT(Player player) {
        CompoundTag tag = player.getPersistentData();
        if (tag.contains("etherEnergy") && tag.contains("etherCapacity")) {
            long energy = tag.getLong("etherEnergy");
            long capacity = tag.getLong("etherCapacity");
            return new EtherEnergyStorage(energy, capacity);
        }
        return null;
    }

    public static void setCapacity(Player player, long newCapacity) {
        EtherEnergyStorage storage = get(player);
        storage.setCapacity(newCapacity);
        if (storage.getEnergy() > newCapacity) {
            storage.setEnergy(newCapacity);
        }
        saveToNBT(player, storage);
    }

    // ─── Unified mana system ────────────────────────────────────────────

    public static boolean hasManaContainer(Player player) {
        if (player == null) return false;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getCapability(ManaCapabilities.MANA_ITEM) != null) return true;
        }
        for (ItemStack stack : player.getInventory().armor) {
            if (stack.getCapability(ManaCapabilities.MANA_ITEM) != null) return true;
        }
        if (player.getOffhandItem().getCapability(ManaCapabilities.MANA_ITEM) != null) return true;
        return false;
    }

    public static long getTotalMana(Player player) {
        if (!hasManaContainer(player)) return 0;
        long total = getEnergy(player);
        total += sumContainerMana(player);
        return total;
    }

    public static long getTotalCapacity(Player player) {
        if (!hasManaContainer(player)) return 0;
        long total = get(player).getCapacity();
        total += sumContainerCapacity(player);
        return total;
    }

    private static long sumContainerMana(Player player) {
        long total = 0;
        for (ItemStack stack : player.getInventory().items) {
            IManaStorage storage = stack.getCapability(ManaCapabilities.MANA_ITEM);
            if (storage != null) total += storage.getManaStored();
        }
        for (ItemStack stack : player.getInventory().armor) {
            IManaStorage storage = stack.getCapability(ManaCapabilities.MANA_ITEM);
            if (storage != null) total += storage.getManaStored();
        }
        IManaStorage offhand = player.getOffhandItem().getCapability(ManaCapabilities.MANA_ITEM);
        if (offhand != null) total += offhand.getManaStored();
        return total;
    }

    private static long sumContainerCapacity(Player player) {
        long total = 0;
        for (ItemStack stack : player.getInventory().items) {
            IManaStorage storage = stack.getCapability(ManaCapabilities.MANA_ITEM);
            if (storage != null) total += storage.getMaxMana();
        }
        for (ItemStack stack : player.getInventory().armor) {
            IManaStorage storage = stack.getCapability(ManaCapabilities.MANA_ITEM);
            if (storage != null) total += storage.getMaxMana();
        }
        IManaStorage offhand = player.getOffhandItem().getCapability(ManaCapabilities.MANA_ITEM);
        if (offhand != null) total += offhand.getMaxMana();
        return total;
    }

    private static long extractFromContainers(Player player, long amount) {
        long remaining = amount;
        for (ItemStack stack : player.getInventory().items) {
            if (remaining <= 0) break;
            IManaStorage container = stack.getCapability(ManaCapabilities.MANA_ITEM);
            if (container != null && container.canExtract()) {
                remaining -= container.extractMana(remaining, false);
            }
        }
        for (ItemStack stack : player.getInventory().armor) {
            if (remaining <= 0) break;
            IManaStorage container = stack.getCapability(ManaCapabilities.MANA_ITEM);
            if (container != null && container.canExtract()) {
                remaining -= container.extractMana(remaining, false);
            }
        }
        if (remaining > 0) {
            IManaStorage offhand = player.getOffhandItem().getCapability(ManaCapabilities.MANA_ITEM);
            if (offhand != null && offhand.canExtract()) {
                remaining -= offhand.extractMana(remaining, false);
            }
        }
        return remaining;
    }
}
