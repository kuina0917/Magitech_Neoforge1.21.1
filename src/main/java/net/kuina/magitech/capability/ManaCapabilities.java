package net.kuina.magitech.capability;

import net.kuina.magitech.block.magitechblockentities;
import net.kuina.magitech.energy.IManaStorage;
import net.kuina.magitech.item.custom.PortableManaTankItem;
import net.kuina.magitech.item.magitechitems;
import net.kuina.magitech.magitech;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;

/**
 * マナの受け渡し規格（Capability）。
 *
 * <p>ブロック・アイテムが「マナ貯蔵窓口（{@link IManaStorage}）」を公開するための
 * 共通の鍵を定義し、ここで各対象に紐付ける。これにより、装置・タンク・携帯アイテムを
 * 同じ方法で繋げられる。今後マナを扱うブロック／アイテムを追加するときは、
 * このクラスの {@link #onRegisterCapabilities} に登録を 1 行足すだけでよい。</p>
 */
@EventBusSubscriber(modid = magitech.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class ManaCapabilities {

    private ManaCapabilities() {
    }

    /** ブロック向けのマナ窓口（向きを指定して取得できる）。 */
    public static final BlockCapability<IManaStorage, Direction> MANA_BLOCK =
            BlockCapability.createSided(
                    ResourceLocation.fromNamespaceAndPath(magitech.MOD_ID, "mana"),
                    IManaStorage.class);

    /** アイテム向けのマナ窓口。 */
    public static final ItemCapability<IManaStorage, Void> MANA_ITEM =
            ItemCapability.createVoid(
                    ResourceLocation.fromNamespaceAndPath(magitech.MOD_ID, "mana"),
                    IManaStorage.class);

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        // マナ貯蔵タンク（ブロック）：どの面からでも内部ストレージを公開
        event.registerBlockEntity(
                MANA_BLOCK,
                magitechblockentities.MANA_TANK_BLOCK_ENTITY.get(),
                (blockEntity, side) -> blockEntity.getManaStorage());

        // 携帯マナタンク（アイテム）：スタックに保存されたマナを公開
        event.registerItem(
                MANA_ITEM,
                (stack, context) -> PortableManaTankItem.createStorage(stack),
                magitechitems.PORTABLE_MANA_TANK.get());

        // マナ加工機（ブロック）：受け取り専用のマナ窓口を公開
        event.registerBlockEntity(
                MANA_BLOCK,
                magitechblockentities.MANA_PROCESSOR_BLOCK_ENTITY.get(),
                (blockEntity, side) -> blockEntity.getManaPort());

        // マナ抽出機コア（ブロック）：双方向のマナ窓口を公開
        event.registerBlockEntity(
                MANA_BLOCK,
                magitechblockentities.MANA_EXTRACTOR_CORE_ENTITY.get(),
                (blockEntity, side) -> blockEntity.getManaPort());

        // マナ加工機（ブロック）：アイテムの自動搬入／搬出（ホッパー等）用に在庫を公開
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                magitechblockentities.MANA_PROCESSOR_BLOCK_ENTITY.get(),
                (blockEntity, side) -> blockEntity.getInventory());

        // --- IEnergyStorage 互換窓口（Jade / Jade Addons が自動表示する） ---

        // マナタンク
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                magitechblockentities.MANA_TANK_BLOCK_ENTITY.get(),
                (blockEntity, side) -> energyWrapper(blockEntity.getManaStorage()));

        // マナ加工機（受け取り専用: extract不可）
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                magitechblockentities.MANA_PROCESSOR_BLOCK_ENTITY.get(),
                (blockEntity, side) -> energyWrapper(blockEntity.getManaPort()));

        // マナ抽出機コア
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                magitechblockentities.MANA_EXTRACTOR_CORE_ENTITY.get(),
                (blockEntity, side) -> energyWrapper(blockEntity.getManaPort()));
    }

    /** IManaStorage → IEnergyStorage 変換 */
    private static IEnergyStorage energyWrapper(IManaStorage mana) {
        return new IEnergyStorage() {
            @Override public int getEnergyStored() { return (int) mana.getManaStored(); }
            @Override public int getMaxEnergyStored() { return (int) mana.getMaxMana(); }
            @Override public int extractEnergy(int maxExtract, boolean simulate) {
                return mana.canExtract() ? (int) mana.extractMana(maxExtract, simulate) : 0;
            }
            @Override public int receiveEnergy(int maxReceive, boolean simulate) {
                return mana.canReceive() ? (int) mana.insertMana(maxReceive, simulate) : 0;
            }
            @Override public boolean canExtract() { return mana.canExtract(); }
            @Override public boolean canReceive() { return mana.canReceive(); }
        };
    }
}
