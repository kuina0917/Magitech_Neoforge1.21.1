package net.kuina.magitech.integration.jade;

import net.kuina.magitech.block.custom.ManaExtractorCoreBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class ManaComponentProvider implements IBlockComponentProvider {

    public static final ManaComponentProvider INSTANCE = new ManaComponentProvider();
    private static final ResourceLocation UID = ResourceLocation.parse("magitech:mana_data");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        var serverData = accessor.getServerData();
        if (serverData != null) {
            if (serverData.contains("ManaStored")) {
                long stored = serverData.getLong("ManaStored");
                long capacity = serverData.getLong("ManaCapacity");
                tooltip.add(Component.translatable("tooltip.magitech.mana_stored",
                        stored, capacity));
            }

            if (serverData.contains("Progress")) {
                int progress = serverData.getInt("Progress");
                int max = serverData.getInt("MaxProgress");
                int pct = max > 0 ? progress * 100 / max : 0;
                tooltip.add(Component.translatable("tooltip.magitech.progress", pct));
            }

            if (serverData.contains("GenPerSecond")) {
                long gen = serverData.getLong("GenPerSecond");
                long xfer = serverData.getLong("TransferPerSecond");
                tooltip.add(Component.translatable("gui.magitech.extractor.generating", gen));
                tooltip.add(Component.translatable("gui.magitech.extractor.transferring", xfer));
            }
        }

        if (accessor.getBlockState().getBlock() instanceof ManaExtractorCoreBlock) {
            boolean formed = accessor.getBlockState().getValue(ManaExtractorCoreBlock.FORMED);
            String key = formed ? "msg.magitech.multiblock_formed" : "msg.magitech.multiblock_not_formed";
            tooltip.add(Component.translatable(key)
                    .withStyle(formed ? ChatFormatting.GREEN : ChatFormatting.RED));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
