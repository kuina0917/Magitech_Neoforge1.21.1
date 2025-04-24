package net.kuina.magitech.fluidtype.custom;

import net.kuina.magitech.fluidtype.magitechfluidtypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ManaFluidType extends FluidType {

    public ManaFluidType() {
        super(FluidType.Properties.create()
                .fallDistanceModifier(0F)
                .canExtinguish(true)
                .supportsBoating(true)
                .canHydrate(true)
                .motionScale(0.007D)
                .lightLevel(8)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.LILY_PAD_PLACE)
                .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
        );
    }

    @SubscribeEvent
    public static void registerFluidTypeExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            private static final ResourceLocation STILL_TEXTURE = ResourceLocation.parse("magitech:block/mana_still");
            private static final ResourceLocation FLOWING_TEXTURE = ResourceLocation.parse("magitech:block/mana_flowing");

            @Override
            public ResourceLocation getStillTexture() {return STILL_TEXTURE;}

            @Override
            public ResourceLocation getFlowingTexture() {return FLOWING_TEXTURE;}
        }, magitechfluidtypes.MANA_FLUID_TYPE.get());
    }}
