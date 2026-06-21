package net.kuina.magitech.relic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class SynergyManager {
    private static final List<Synergy> SYNERGIES = new ArrayList<>();

    static {
        register(new Synergy("fire_synergy", "synergy.magitech.fire",
            new RelicElement[]{RelicElement.FIRE}, 3,
            Map.of(RelicEffectType.FIRE_DAMAGE, 0.20f)));

        register(new Synergy("metal_synergy", "synergy.magitech.metal",
            new RelicElement[]{RelicElement.METAL}, 2,
            Map.of(RelicEffectType.DEFENSE, 0.15f)));

        register(new Synergy("life_synergy", "synergy.magitech.life",
            new RelicElement[]{RelicElement.LIFE}, 3,
            Map.of(RelicEffectType.HEALING, 2.0f)));

        register(new Synergy("water_synergy", "synergy.magitech.water",
            new RelicElement[]{RelicElement.WATER}, 2,
            Map.of(RelicEffectType.MAX_HEALTH, 0.10f)));

        register(new Synergy("earth_synergy", "synergy.magitech.earth",
            new RelicElement[]{RelicElement.EARTH}, 2,
            Map.of(RelicEffectType.DEFENSE, 0.10f, RelicEffectType.MAX_HEALTH, 0.05f)));

        register(new Synergy("air_synergy", "synergy.magitech.air",
            new RelicElement[]{RelicElement.AIR}, 2,
            Map.of(RelicEffectType.MOVEMENT_SPEED, 0.10f, RelicEffectType.DODGE, 0.03f)));

        register(new Synergy("void_synergy", "synergy.magitech.void",
            new RelicElement[]{RelicElement.VOID}, 2,
            Map.of(RelicEffectType.MAGIC_DAMAGE, 0.15f)));

        register(new Synergy("elemental_mix", "synergy.magitech.elemental_mix",
            new RelicElement[]{RelicElement.FIRE, RelicElement.WATER, RelicElement.EARTH, RelicElement.AIR}, 3,
            Map.of(RelicEffectType.ATTACK_DAMAGE, 0.10f, RelicEffectType.DEFENSE, 0.10f)));
    }

    public static void register(Synergy synergy) {
        SYNERGIES.add(synergy);
    }

    public static void detectSynergies(RelicBoard board, List<Synergy> output) {
        List<PlacedRelic> relics = board.getPlacedRelics();
        if (relics.size() < 2) return;

        Set<String> activeIds = new HashSet<>();

        for (Synergy synergy : SYNERGIES) {
            List<Set<PlacedRelic>> groups = findAdjacentGroups(relics, synergy.elements(), synergy.minCount());
            for (Set<PlacedRelic> group : groups) {
                if (group.size() >= synergy.minCount()) {
                    if (activeIds.add(synergy.id())) {
                        output.add(synergy);
                    }
                }
            }
        }
    }

    private static List<Set<PlacedRelic>> findAdjacentGroups(
        List<PlacedRelic> relics, RelicElement[] validElements, int minCount
    ) {
        Set<RelicElement> validSet = Set.of(validElements);
        List<PlacedRelic> candidates = new ArrayList<>();
        for (PlacedRelic relic : relics) {
            if (validSet.contains(relic.data().element())) {
                candidates.add(relic);
            }
        }

        List<Set<PlacedRelic>> groups = new ArrayList<>();
        Set<PlacedRelic> visited = new HashSet<>();

        for (PlacedRelic candidate : candidates) {
            if (!visited.contains(candidate)) {
                Set<PlacedRelic> group = new HashSet<>();
                floodFill(candidate, candidates, visited, group);
                if (!group.isEmpty()) {
                    groups.add(group);
                }
            }
        }
        return groups;
    }

    private static void floodFill(
        PlacedRelic start, List<PlacedRelic> candidates,
        Set<PlacedRelic> visited, Set<PlacedRelic> group
    ) {
        List<PlacedRelic> stack = new ArrayList<>();
        stack.add(start);

        while (!stack.isEmpty()) {
            PlacedRelic current = stack.removeLast();
            if (!visited.add(current)) continue;
            group.add(current);

            for (PlacedRelic other : candidates) {
                if (!visited.contains(other) && areAdjacent(current, other)) {
                    stack.add(other);
                }
            }
        }
    }

    private static boolean areAdjacent(PlacedRelic a, PlacedRelic b) {
        int aMinX = a.posX(), aMaxX = a.posX() + a.getRotatedShape().width() - 1;
        int aMinY = a.posY(), aMaxY = a.posY() + a.getRotatedShape().height() - 1;
        int bMinX = b.posX(), bMaxX = b.posX() + b.getRotatedShape().width() - 1;
        int bMinY = b.posY(), bMaxY = b.posY() + b.getRotatedShape().height() - 1;

        boolean xOverlap = aMinX <= bMaxX && aMaxX >= bMinX;
        boolean yOverlap = aMinY <= bMaxY && aMaxY >= bMinY;

        if (xOverlap && yOverlap) return false;

        boolean xAdjacent = aMaxX + 1 == bMinX || bMaxX + 1 == aMinX;
        boolean yAdjacent = aMaxY + 1 == bMinY || bMaxY + 1 == aMinY;

        if (xAdjacent && yOverlap) return true;
        if (yAdjacent && xOverlap) return true;

        return false;
    }

    private SynergyManager() {}
}
