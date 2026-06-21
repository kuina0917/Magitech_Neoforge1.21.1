package net.kuina.magitech.relic;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelicBoard {
    private int size;
    private final List<PlacedRelic> placedRelics;
    private final List<Synergy> activeSynergies;

    public RelicBoard() {
        this(4);
    }

    public RelicBoard(int size) {
        this.size = size;
        this.placedRelics = new ArrayList<>();
        this.activeSynergies = new ArrayList<>();
    }

    public int getSize() {
        return size;
    }

    public void expand(int newSize) {
        if (newSize > size && newSize <= 6) {
            this.size = newSize;
        }
    }

    public List<PlacedRelic> getPlacedRelics() {
        return placedRelics;
    }

    public List<Synergy> getActiveSynergies() {
        return activeSynergies;
    }

    public boolean canPlace(PlacedRelic relic) {
        RelicShape shape = relic.getRotatedShape();
        for (int dy = 0; dy < shape.height(); dy++) {
            for (int dx = 0; dx < shape.width(); dx++) {
                if (!shape.get(dx, dy)) continue;
                int gx = relic.posX() + dx;
                int gy = relic.posY() + dy;
                if (gx < 0 || gx >= size || gy < 0 || gy >= size) return false;
                if (getRelicAt(gx, gy) != null) return false;
            }
        }
        return true;
    }

    public boolean place(PlacedRelic relic) {
        if (!canPlace(relic)) return false;
        placedRelics.add(relic);
        recalculateSynergies();
        return true;
    }

    public boolean remove(int x, int y) {
        PlacedRelic relic = getRelicAt(x, y);
        if (relic != null) {
            placedRelics.remove(relic);
            recalculateSynergies();
            return true;
        }
        return false;
    }

    public PlacedRelic getRelicAt(int gx, int gy) {
        for (PlacedRelic relic : placedRelics) {
            if (relic.occupiesCell(gx, gy)) return relic;
        }
        return null;
    }

    public PlacedRelic getRelicById(String id) {
        for (PlacedRelic relic : placedRelics) {
            if (relic.data().id().equals(id)) return relic;
        }
        return null;
    }

    public boolean[][] getOccupiedGrid() {
        boolean[][] grid = new boolean[size][size];
        for (PlacedRelic relic : placedRelics) {
            RelicShape shape = relic.getRotatedShape();
            for (int dy = 0; dy < shape.height(); dy++) {
                for (int dx = 0; dx < shape.width(); dx++) {
                    if (shape.get(dx, dy)) {
                        int gx = relic.posX() + dx;
                        int gy = relic.posY() + dy;
                        if (gx >= 0 && gx < size && gy >= 0 && gy < size) {
                            grid[gy][gx] = true;
                        }
                    }
                }
            }
        }
        return grid;
    }

    public Map<RelicEffectType, Float> getTotalEffects() {
        Map<RelicEffectType, Float> total = new HashMap<>();
        for (PlacedRelic relic : placedRelics) {
            for (var entry : relic.data().mainEffects().entrySet()) {
                total.merge(entry.getKey(), entry.getValue(), Float::sum);
            }
            for (var entry : relic.data().subEffects().entrySet()) {
                total.merge(entry.getKey(), entry.getValue(), Float::sum);
            }
        }
        for (Synergy synergy : activeSynergies) {
            for (var entry : synergy.effects().entrySet()) {
                total.merge(entry.getKey(), entry.getValue(), Float::sum);
            }
        }
        return total;
    }

    private void recalculateSynergies() {
        activeSynergies.clear();
        SynergyManager.detectSynergies(this, activeSynergies);
    }

    public CompoundTag saveToNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("size", size);

        ListTag list = new ListTag();
        for (PlacedRelic relic : placedRelics) {
            CompoundTag entry = new CompoundTag();
            entry.putString("id", relic.data().id());
            entry.putInt("posX", relic.posX());
            entry.putInt("posY", relic.posY());
            entry.putInt("rotation", relic.rotation());

            CompoundTag dataTag = new CompoundTag();
            dataTag.putString("relic_id", relic.data().id());
            dataTag.putString("rarity", relic.data().rarity().getSerializedName());
            dataTag.putInt("shape_w", relic.data().shape().width());
            dataTag.putInt("shape_h", relic.data().shape().height());

            byte[] cells = new byte[relic.data().shape().width() * relic.data().shape().height()];
            for (int i = 0; i < cells.length; i++) {
                cells[i] = (byte) (relic.data().shape().cells().get(i) ? 1 : 0);
            }
            dataTag.putByteArray("shape_cells", cells);
            dataTag.putString("element", relic.data().element().getSerializedName());

            CompoundTag mainEffects = new CompoundTag();
            for (var effect : relic.data().mainEffects().entrySet()) {
                mainEffects.putFloat(effect.getKey().getSerializedName(), effect.getValue());
            }
            dataTag.put("main_effects", mainEffects);

            CompoundTag subEffects = new CompoundTag();
            for (var effect : relic.data().subEffects().entrySet()) {
                subEffects.putFloat(effect.getKey().getSerializedName(), effect.getValue());
            }
            dataTag.put("sub_effects", subEffects);

            entry.put("data", dataTag);
            list.add(entry);
        }
        tag.put("relics", list);
        return tag;
    }

    public static RelicBoard loadFromNBT(CompoundTag tag) {
        int size = tag.getInt("size");
        RelicBoard board = new RelicBoard(size);

        ListTag list = tag.getList("relics", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            CompoundTag dataTag = entry.getCompound("data");

            RelicRarity rarity = RelicRarity.fromName(dataTag.getString("rarity"));
            int sw = dataTag.getInt("shape_w");
            int sh = dataTag.getInt("shape_h");
            byte[] cells = dataTag.getByteArray("shape_cells");
            RelicShape shape = RelicShape.fromByteArray(sw, sh, cells);
            RelicElement element = RelicElement.fromName(dataTag.getString("element"));

            Map<RelicEffectType, Float> mainEffects = new HashMap<>();
            CompoundTag mainTag = dataTag.getCompound("main_effects");
            for (String key : mainTag.getAllKeys()) {
                mainEffects.put(RelicEffectType.fromName(key), mainTag.getFloat(key));
            }

            Map<RelicEffectType, Float> subEffects = new HashMap<>();
            CompoundTag subTag = dataTag.getCompound("sub_effects");
            for (String key : subTag.getAllKeys()) {
                subEffects.put(RelicEffectType.fromName(key), subTag.getFloat(key));
            }

            RelicData data = new RelicData(
                entry.getString("id"), rarity, shape, mainEffects, subEffects, element
            );

            PlacedRelic relic = new PlacedRelic(
                data, entry.getInt("posX"), entry.getInt("posY"), entry.getInt("rotation")
            );
            board.placedRelics.add(relic);
        }

        board.recalculateSynergies();
        return board;
    }
}
