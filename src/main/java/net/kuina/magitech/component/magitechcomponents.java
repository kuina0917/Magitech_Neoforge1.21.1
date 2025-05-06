package net.kuina.magitech.component;

import com.mojang.serialization.Codec;
import net.kuina.magitech.magitech;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.NeoForgeRegistries;



public class magitechcomponents {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, magitech.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> ZOLTRAK_MODE =
            DATA_COMPONENT_TYPES.register("zoltrak_mode", () ->
                    DataComponentType.<Boolean>builder()
                            .persistent(Codec.BOOL)              // NBT保存用Codec
                            .networkSynchronized(ByteBufCodecs.BOOL) // ネットワーク同期用Codec
                            .build()
            );


    public static void register(IEventBus bus) {
        DATA_COMPONENT_TYPES.register(bus);
    }
}