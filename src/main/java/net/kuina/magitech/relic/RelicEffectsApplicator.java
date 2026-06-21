package net.kuina.magitech.relic;

import net.kuina.magitech.energy.PlayerEtherEnergy;
import net.kuina.magitech.util.ModUtil;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

public final class RelicEffectsApplicator {

    public static void apply(Player player, Map<RelicEffectType, Float> effects) {
        applyModifier(player, Attributes.ATTACK_DAMAGE,
            RelicEffectType.ATTACK_DAMAGE, effects, AttributeModifier.Operation.ADD_VALUE);

        applyModifier(player, Attributes.ATTACK_SPEED,
            RelicEffectType.ATTACK_SPEED, effects, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

        applyModifier(player, Attributes.MAX_HEALTH,
            RelicEffectType.MAX_HEALTH, effects, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

        applyModifier(player, Attributes.MOVEMENT_SPEED,
            RelicEffectType.MOVEMENT_SPEED, effects, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

        applyModifier(player, Attributes.ARMOR,
            RelicEffectType.DEFENSE, effects, AttributeModifier.Operation.ADD_VALUE);

        applyManaEffects(player, effects);
    }

    private static void applyModifier(Player player, Holder<Attribute> attrHolder,
                                       RelicEffectType type, Map<RelicEffectType, Float> effects,
                                       AttributeModifier.Operation operation) {
        AttributeInstance instance = player.getAttribute(attrHolder);
        if (instance == null) return;

        ResourceLocation id = relicModifierId(type);
        instance.removeModifier(id);

        Float value = effects.get(type);
        if (value == null || value == 0f) return;

        instance.addTransientModifier(new AttributeModifier(id, value, operation));
    }

    private static ResourceLocation relicModifierId(RelicEffectType type) {
        return ModUtil.rl("relic_" + type.getSerializedName());
    }

    private static void applyManaEffects(Player player, Map<RelicEffectType, Float> effects) {
        float manaMaxBonus = effects.getOrDefault(RelicEffectType.MANA_MAX, 0f);

        long previousBonus = getPreviousManaBonus(player);
        long newBonus = (long) manaMaxBonus;

        if (newBonus != previousBonus) {
            long baseCapacity = PlayerEtherEnergy.DEFAULT_CAPACITY;
            long currentCapacity = PlayerEtherEnergy.get(player).getCapacity();
            long adjustedCapacity = currentCapacity - previousBonus + newBonus;
            if (adjustedCapacity < baseCapacity) adjustedCapacity = baseCapacity;
            PlayerEtherEnergy.setCapacity(player, adjustedCapacity);
            storeManaBonus(player, newBonus);
        }
    }

    private static final String MANA_BONUS_KEY = "magitech_relic_mana_bonus";

    private static long getPreviousManaBonus(Player player) {
        return player.getPersistentData().getLong(MANA_BONUS_KEY);
    }

    private static void storeManaBonus(Player player, long bonus) {
        player.getPersistentData().putLong(MANA_BONUS_KEY, bonus);
    }

    public static void clearAll(Player player) {
        for (var type : RelicEffectType.values()) {
            removeModifier(player, type);
        }
        long bonus = getPreviousManaBonus(player);
        if (bonus > 0) {
            PlayerEtherEnergy.setCapacity(player, PlayerEtherEnergy.DEFAULT_CAPACITY);
            storeManaBonus(player, 0);
        }
    }

    private static void removeModifier(Player player, RelicEffectType type) {
        Holder<Attribute> attr = getAttributeForType(type);
        if (attr == null) return;
        AttributeInstance instance = player.getAttribute(attr);
        if (instance == null) return;
        instance.removeModifier(relicModifierId(type));
    }

    private static Holder<Attribute> getAttributeForType(RelicEffectType type) {
        return switch (type) {
            case ATTACK_DAMAGE -> Attributes.ATTACK_DAMAGE;
            case ATTACK_SPEED -> Attributes.ATTACK_SPEED;
            case MAX_HEALTH -> Attributes.MAX_HEALTH;
            case MOVEMENT_SPEED -> Attributes.MOVEMENT_SPEED;
            case DEFENSE -> Attributes.ARMOR;
            default -> null;
        };
    }

    private RelicEffectsApplicator() {}
}
