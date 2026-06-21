package net.kuina.magitech.relic;

import java.util.Map;

public record Synergy(
    String id,
    String displayName,
    RelicElement[] elements,
    int minCount,
    Map<RelicEffectType, Float> effects
) {
}
