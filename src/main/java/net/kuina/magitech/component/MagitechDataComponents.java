package net.kuina.magitech.component;

import com.mojang.serialization.Codec;
import net.kuina.magitech.Magitech;
import net.kuina.magitech.relic.RelicData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;



public class MagitechDataComponents {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, Magitech.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> ZOLTRAK_MODE =
            DATA_COMPONENT_TYPES.register("zoltrak_mode", () ->
                    DataComponentType.<Boolean>builder()
                            .persistent(Codec.BOOL)
                            .networkSynchronized(ByteBufCodecs.BOOL)
                            .build()
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> MANA =
            DATA_COMPONENT_TYPES.register("mana", () ->
                    DataComponentType.<Long>builder()
                            .persistent(Codec.LONG)
                            .networkSynchronized(ByteBufCodecs.VAR_LONG)
                            .build()
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<RelicData>> RELIC_DATA =
            DATA_COMPONENT_TYPES.register("relic_data", () ->
                    DataComponentType.<RelicData>builder()
                            .persistent(RelicData.CODEC)
                            .networkSynchronized(RelicData.STREAM_CODEC)
                            .build()
            );

    public static void register(IEventBus bus) {
        DATA_COMPONENT_TYPES.register(bus);
    }
}