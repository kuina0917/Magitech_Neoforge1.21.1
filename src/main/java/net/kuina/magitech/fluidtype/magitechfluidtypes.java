package net.kuina.magitech.fluidtype;

import net.kuina.magitech.magitech;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import net.kuina.magitech.fluidtype.custom.ManaFluidType;

public class magitechfluidtypes {

    public static final DeferredRegister<FluidType> FLUID_TYPE = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, magitech.MOD_ID);
    public static final DeferredHolder<FluidType, FluidType> MANA_FLUID_TYPE = FLUID_TYPE.register("mana", ()-> new ManaFluidType());



    }