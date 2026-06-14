package net.kuina.magitech.integration.jade;

import net.kuina.magitech.block.base.ManaContainerBlockEntity;
import net.kuina.magitech.block.custom.ManaExtractorCoreBlockEntity;
import net.kuina.magitech.block.custom.ManaProcessorBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

public class ManaDataProvider implements IServerDataProvider<BlockAccessor> {

    public static final ManaDataProvider INSTANCE = new ManaDataProvider();
    private static final ResourceLocation UID = ResourceLocation.parse("magitech:mana_data");

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof ManaContainerBlockEntity mana)) return;

        data.putLong("ManaStored", mana.getManaPort().getManaStored());
        data.putLong("ManaCapacity", mana.getManaPort().getMaxMana());

        if (mana instanceof ManaProcessorBlockEntity processor) {
            data.putInt("Progress", processor.getData().get(0));
            data.putInt("MaxProgress", processor.getData().get(1));
        }

        if (mana instanceof ManaExtractorCoreBlockEntity extractor) {
            data.putLong("GenPerSecond", extractor.getManaGenPerSecond());
            data.putLong("TransferPerSecond", extractor.getManaTransferPerSecond());
        }
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
