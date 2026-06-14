package net.kuina.magitech.worldgen;

import net.kuina.magitech.magitech;
import net.kuina.magitech.worldgen.feature.ManaPoolFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class magitechfeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = 
            DeferredRegister.create(Registries.FEATURE, magitech.MOD_ID);

    public static final DeferredHolder<Feature<?>, ManaPoolFeature> MANA_POOL = 
            FEATURES.register("mana_pool", () -> new ManaPoolFeature(NoneFeatureConfiguration.CODEC));

    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);
    }
}
