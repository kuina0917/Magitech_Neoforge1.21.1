package net.kuina.magitech.menu;

import net.kuina.magitech.magitech;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * GUI（コンテナ画面）の MenuType 登録。
 * メニューを追加したら、ここに 1 件登録し、クライアント側で
 * {@code MenuScreens.register} に対応する Screen を結び付ける。
 */
public class magitechmenus {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, magitech.MOD_ID);

    /** マナ加工機の GUI。 */
    public static final DeferredHolder<MenuType<?>, MenuType<net.kuina.magitech.menu.ManaProcessorMenu>> MANA_PROCESSOR_MENU =
            MENUS.register("mana_processor",
                    () -> IMenuTypeExtension.create(net.kuina.magitech.menu.ManaProcessorMenu::new));

    public static void register(IEventBus bus) {
        MENUS.register(bus);
    }
}
