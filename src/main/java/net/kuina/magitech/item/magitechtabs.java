package net.kuina.magitech.item;

import net.kuina.magitech.Magitech;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MagitechTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Magitech.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAGITECH_TAB =
            CREATIVE_MODE_TABS.register("magitech", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.magitech"))
                    .icon(() -> new ItemStack(MagitechItems.MANA_CRYSTAL.get()))
                    .displayItems((params, output) -> {
                        output.accept(MagitechItems.LOW_MANA_INGOT.get());
                        output.accept(MagitechItems.MIDDLE_MANA_INGOT.get());
                        output.accept(MagitechItems.HIGH_MANA_INGOT.get());
                        output.accept(MagitechItems.MANA_CRYSTAL.get());

                        output.accept(MagitechItems.MAGITECH_GUIDE.get());
                        output.accept(MagitechItems.CRYSTAL_ROD.get());
                        output.accept(MagitechItems.LOW_MANA_PICKAXE.get());

                        output.accept(MagitechItems.MANA_BUCKET.get());
                        output.accept(MagitechItems.PORTABLE_MANA_TANK.get());
                        output.accept(MagitechItems.MANA_TANK.get());
                        output.accept(MagitechItems.MANA_PROCESSOR.get());
                        output.accept(MagitechItems.MANA_EXTRACTOR_CASING.get());
                        output.accept(MagitechItems.MANA_EXTRACTOR_CORE.get());
                        output.accept(MagitechItems.CREATIVE_ETHER_ENERGY_BLOCK.get());

                        output.accept(MagitechItems.MANA_STONE.get());
                        output.accept(MagitechItems.MANA_COBBLESTONE.get());
                        output.accept(MagitechItems.ACTIVE_MAGITECH_BLOCK.get());
                        output.accept(MagitechItems.TESTBLOCK.get());
                    })
                    .build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> RELIC_TAB =
            CREATIVE_MODE_TABS.register("magitech_relics", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.magitech_relics"))
                    .icon(() -> MagitechItems.createElementalCore())
                    .displayItems((params, output) -> {
                        output.accept(MagitechItems.RELIC_BOARD.get());
                        output.accept(MagitechItems.createDemonSteelHeart());
                        output.accept(MagitechItems.createGiantMuscleFiber());
                        output.accept(MagitechItems.createSmithingGodCore());
                        output.accept(MagitechItems.createWorldTreeSprout());
                        output.accept(MagitechItems.createResonanceSpores());
                        output.accept(MagitechItems.createFloatingPollenSac());
                        output.accept(MagitechItems.createElementalCore());
                        output.accept(MagitechItems.createPhaseCrystal());
                        output.accept(MagitechItems.createImaginaryOperator());
                    })
                    .build());

    public static void register(IEventBus bus) {
        CREATIVE_MODE_TABS.register(bus);
    }
}
