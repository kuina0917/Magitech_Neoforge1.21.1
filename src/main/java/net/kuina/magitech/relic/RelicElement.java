package net.kuina.magitech.relic;

import net.minecraft.util.StringRepresentable;

public enum RelicElement implements StringRepresentable {
    NEUTRAL("neutral"),
    FIRE("fire"),
    WATER("water"),
    EARTH("earth"),
    AIR("air"),
    METAL("metal"),
    LIFE("life"),
    VOID("void");

    private final String name;

    RelicElement(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public static RelicElement fromName(String name) {
        for (RelicElement e : values()) {
            if (e.name.equals(name)) return e;
        }
        return NEUTRAL;
    }
}
