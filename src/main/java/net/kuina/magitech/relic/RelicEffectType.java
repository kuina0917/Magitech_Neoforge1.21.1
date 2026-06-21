package net.kuina.magitech.relic;

import net.minecraft.util.StringRepresentable;

public enum RelicEffectType implements StringRepresentable {
    ATTACK_DAMAGE("attack_damage"),
    ATTACK_SPEED("attack_speed"),
    CRIT_RATE("crit_rate"),
    CRIT_DAMAGE("crit_damage"),
    MAX_HEALTH("max_health"),
    DEFENSE("defense"),
    MOVEMENT_SPEED("movement_speed"),
    MANA_MAX("mana_max"),
    MANA_REGEN("mana_regen"),
    CAST_SPEED("cast_speed"),
    MAGIC_DAMAGE("magic_damage"),
    RANGE("range"),
    ACCURACY("accuracy"),
    STATUS_EFFECT_CHANCE("status_effect_chance"),
    DODGE("dodge"),
    LIFE_STEAL("life_steal"),
    FIRE_DAMAGE("fire_damage"),
    WATER_DAMAGE("water_damage"),
    EARTH_DAMAGE("earth_damage"),
    AIR_DAMAGE("air_damage"),
    HEALING("healing");

    private final String name;

    RelicEffectType(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public static RelicEffectType fromName(String name) {
        for (RelicEffectType e : values()) {
            if (e.name.equals(name)) return e;
        }
        return ATTACK_DAMAGE;
    }
}
