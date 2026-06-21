package net.kuina.magitech.relic;

import net.minecraft.util.StringRepresentable;

public enum RelicRarity implements StringRepresentable {
    COMMON("common", 0),
    UNCOMMON("uncommon", 1),
    RARE("rare", 2),
    EPIC("epic", 3),
    LEGENDARY("legendary", 4);

    private final String name;
    private final int tier;

    RelicRarity(String name, int tier) {
        this.name = name;
        this.tier = tier;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public int getTier() {
        return tier;
    }

    public float getMultiplier() {
        return 1.0f + tier * 0.25f;
    }

    public static RelicRarity fromName(String name) {
        for (RelicRarity r : values()) {
            if (r.name.equals(name)) return r;
        }
        return COMMON;
    }
}
