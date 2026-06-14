package net.kuina.magitech.item;

import net.kuina.magitech.magitech;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Magitech 専用のクリエイティブタブ。
 * この mod のアイテムはすべてここにまとめて表示する。
 * アイテムを追加したら {@code displayItems} の中に 1 行足すこと。
 */
public class magitechtabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, magitech.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAGITECH_TAB =
            CREATIVE_MODE_TABS.register("magitech", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.magitech"))                 // タブ名（lang参照）
                    .icon(() -> new ItemStack(magitechitems.MANA_CRYSTAL.get()))         // タブのアイコン
                    .displayItems((params, output) -> {
                        // 素材
                        output.accept(magitechitems.LOW_MANA_INGOT.get());
                        output.accept(magitechitems.MIDDLE_MANA_INGOT.get());
                        output.accept(magitechitems.HIGH_MANA_INGOT.get());
                        output.accept(magitechitems.MANA_CRYSTAL.get());

                        // 道具・装備
                        output.accept(magitechitems.MAGITECH_GUIDE.get());
                        output.accept(magitechitems.CRYSTAL_ROD.get());
                        output.accept(magitechitems.LOW_MANA_PICKAXE.get());

                        // マナ関連
                        output.accept(magitechitems.MANA_BUCKET.get());
                        output.accept(magitechitems.PORTABLE_MANA_TANK.get());
                        output.accept(magitechitems.MANA_TANK.get());
                        output.accept(magitechitems.MANA_PROCESSOR.get());
                        output.accept(magitechitems.MANA_EXTRACTOR_CASING.get());
                        output.accept(magitechitems.MANA_EXTRACTOR_CORE.get());
                        output.accept(magitechitems.CREATIVE_ETHER_ENERGY_BLOCK.get());

                        // ブロック
                        output.accept(magitechitems.MANA_STONE.get());
                        output.accept(magitechitems.MANA_COBBLESTONE.get());
                        output.accept(magitechitems.ACTIVE_MAGITECH_BLOCK.get());
                        output.accept(magitechitems.TESTBLOCK.get());
                    })
                    .build());

    public static void register(IEventBus bus) {
        CREATIVE_MODE_TABS.register(bus);
    }
}
