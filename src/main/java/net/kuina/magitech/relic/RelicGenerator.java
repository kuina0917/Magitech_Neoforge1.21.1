package net.kuina.magitech.relic;

import java.util.*;

public final class RelicGenerator {

    private static final Random RANDOM = new Random();

    private static final RelicShape[] COMMON_SHAPES = {
        RelicShape.single(), RelicShape.horizontal(2), RelicShape.vertical(2)
    };

    private static final RelicShape[] UNCOMMON_SHAPES = {
        RelicShape.square(2), RelicShape.LShape()
    };

    private static final RelicShape[] RARE_SHAPES = {
        RelicShape.horizontal(3), RelicShape.vertical(3), RelicShape.TShape()
    };

    private static final RelicShape[] EPIC_SHAPES = {
        RelicShape.cross(), RelicShape.square(3)
    };

    private static final RelicShape[] LEGENDARY_SHAPES = {
        RelicShape.cross(), RelicShape.TShape(), RelicShape.square(3)
    };

    private static final Map<RelicElement, RelicEffectType[]> ELEMENT_MAIN_EFFECTS = new HashMap<>();
    private static final RelicEffectType[] SUB_EFFECTS = {
        RelicEffectType.CRIT_RATE, RelicEffectType.CRIT_DAMAGE, RelicEffectType.DODGE,
        RelicEffectType.LIFE_STEAL, RelicEffectType.ATTACK_SPEED, RelicEffectType.ACCURACY
    };

    static {
        ELEMENT_MAIN_EFFECTS.put(RelicElement.FIRE, new RelicEffectType[]{RelicEffectType.ATTACK_DAMAGE, RelicEffectType.FIRE_DAMAGE});
        ELEMENT_MAIN_EFFECTS.put(RelicElement.WATER, new RelicEffectType[]{RelicEffectType.MAX_HEALTH, RelicEffectType.WATER_DAMAGE});
        ELEMENT_MAIN_EFFECTS.put(RelicElement.EARTH, new RelicEffectType[]{RelicEffectType.DEFENSE, RelicEffectType.EARTH_DAMAGE});
        ELEMENT_MAIN_EFFECTS.put(RelicElement.AIR, new RelicEffectType[]{RelicEffectType.MOVEMENT_SPEED, RelicEffectType.AIR_DAMAGE});
        ELEMENT_MAIN_EFFECTS.put(RelicElement.METAL, new RelicEffectType[]{RelicEffectType.ATTACK_DAMAGE, RelicEffectType.DEFENSE});
        ELEMENT_MAIN_EFFECTS.put(RelicElement.LIFE, new RelicEffectType[]{RelicEffectType.MAX_HEALTH, RelicEffectType.HEALING});
        ELEMENT_MAIN_EFFECTS.put(RelicElement.VOID, new RelicEffectType[]{RelicEffectType.MAGIC_DAMAGE, RelicEffectType.MANA_MAX});
        ELEMENT_MAIN_EFFECTS.put(RelicElement.NEUTRAL, new RelicEffectType[]{RelicEffectType.ATTACK_DAMAGE, RelicEffectType.DEFENSE, RelicEffectType.MAX_HEALTH});
    }

    public static RelicData generateRandom() {
        RelicRarity rarity = rollRarity();
        return generate(rarity, rollElement(), nextId());
    }

    public static RelicData generateRandom(RelicRarity rarity) {
        return generate(rarity, rollElement(), nextId());
    }

    public static RelicData generateRandom(RelicRarity rarity, RelicElement element) {
        return generate(rarity, element, nextId());
    }

    private static RelicData generate(RelicRarity rarity, RelicElement element, String id) {
        RelicShape shape = rollShape(rarity);
        Map<RelicEffectType, Float> mainEffects = generateMainEffects(rarity, element);
        Map<RelicEffectType, Float> subEffects = generateSubEffects(rarity);
        return new RelicData(id, rarity, shape, mainEffects, subEffects, element);
    }

    private static String nextId() {
        return "generated_" + UUID.randomUUID().toString().substring(0, 8);
    }

    public static RelicRarity rollRarity() {
        float roll = RANDOM.nextFloat();
        if (roll < 0.40f) return RelicRarity.COMMON;
        if (roll < 0.70f) return RelicRarity.UNCOMMON;
        if (roll < 0.88f) return RelicRarity.RARE;
        if (roll < 0.97f) return RelicRarity.EPIC;
        return RelicRarity.LEGENDARY;
    }

    public static RelicElement rollElement() {
        RelicElement[] values = RelicElement.values();
        return values[RANDOM.nextInt(values.length)];
    }

    public static RelicShape rollShape(RelicRarity rarity) {
        RelicShape[] pool;
        switch (rarity) {
            case COMMON: pool = COMMON_SHAPES; break;
            case UNCOMMON: pool = UNCOMMON_SHAPES; break;
            case RARE: pool = RARE_SHAPES; break;
            case EPIC: pool = EPIC_SHAPES; break;
            case LEGENDARY: pool = LEGENDARY_SHAPES; break;
            default: pool = COMMON_SHAPES;
        }
        return pool[RANDOM.nextInt(pool.length)];
    }

    private static Map<RelicEffectType, Float> generateMainEffects(RelicRarity rarity, RelicElement element) {
        Map<RelicEffectType, Float> effects = new HashMap<>();
        RelicEffectType[] pool = ELEMENT_MAIN_EFFECTS.get(element);
        float mult = rarity.getMultiplier();
        int count = rarity.getTier() >= 3 ? 2 : 1;
        Set<Integer> used = new HashSet<>();
        for (int i = 0; i < count && used.size() < pool.length; i++) {
            int idx;
            do {
                idx = RANDOM.nextInt(pool.length);
            } while (used.contains(idx));
            used.add(idx);
            RelicEffectType type = pool[idx];
            float value = getMainEffectValue(type, mult);
            effects.put(type, value);
        }
        return effects;
    }

    private static Map<RelicEffectType, Float> generateSubEffects(RelicRarity rarity) {
        Map<RelicEffectType, Float> effects = new HashMap<>();
        int count = rarity.getTier() >= 1 ? RANDOM.nextInt(Math.min(rarity.getTier(), 3)) : 0;
        Set<Integer> used = new HashSet<>();
        for (int i = 0; i < count; i++) {
            int idx;
            do {
                idx = RANDOM.nextInt(SUB_EFFECTS.length);
            } while (used.contains(idx));
            used.add(idx);
            RelicEffectType type = SUB_EFFECTS[idx];
            float value = switch (type) {
                case CRIT_RATE, DODGE, LIFE_STEAL, ACCURACY -> (0.01f + RANDOM.nextFloat() * 0.04f) * rarity.getMultiplier();
                case CRIT_DAMAGE -> (0.03f + RANDOM.nextFloat() * 0.07f) * rarity.getMultiplier();
                case ATTACK_SPEED -> (0.02f + RANDOM.nextFloat() * 0.03f) * rarity.getMultiplier();
                default -> 1.0f * rarity.getMultiplier();
            };
            effects.put(type, Math.round(value * 100) / 100.0f);
        }
        return effects;
    }

    private static float getMainEffectValue(RelicEffectType type, float mult) {
        float base = switch (type) {
            case ATTACK_DAMAGE -> 2.0f + RANDOM.nextFloat() * 4.0f;
            case MAX_HEALTH -> 0.05f + RANDOM.nextFloat() * 0.15f;
            case DEFENSE -> 1.0f + RANDOM.nextFloat() * 3.0f;
            case MOVEMENT_SPEED -> 0.03f + RANDOM.nextFloat() * 0.07f;
            case MANA_MAX -> 20.0f + RANDOM.nextFloat() * 40.0f;
            case MANA_REGEN -> 0.5f + RANDOM.nextFloat() * 1.5f;
            case HEALING -> 0.5f + RANDOM.nextFloat() * 1.5f;
            case FIRE_DAMAGE, WATER_DAMAGE, EARTH_DAMAGE, AIR_DAMAGE -> 0.05f + RANDOM.nextFloat() * 0.15f;
            case MAGIC_DAMAGE -> 0.05f + RANDOM.nextFloat() * 0.15f;
            default -> 1.0f;
        };
        return Math.round(base * mult * 100) / 100.0f;
    }

    private RelicGenerator() {}
}
