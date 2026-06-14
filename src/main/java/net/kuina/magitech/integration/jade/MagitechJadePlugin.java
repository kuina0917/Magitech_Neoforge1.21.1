package net.kuina.magitech.integration.jade;

import net.kuina.magitech.block.custom.ManaExtractorCoreBlock;
import net.kuina.magitech.block.custom.ManaProcessorBlock;
import net.kuina.magitech.block.custom.ManaTankBlock;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class MagitechJadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(ManaDataProvider.INSTANCE, ManaTankBlock.class);
        registration.registerBlockDataProvider(ManaDataProvider.INSTANCE, ManaProcessorBlock.class);
        registration.registerBlockDataProvider(ManaDataProvider.INSTANCE, ManaExtractorCoreBlock.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(ManaComponentProvider.INSTANCE, ManaTankBlock.class);
        registration.registerBlockComponent(ManaComponentProvider.INSTANCE, ManaProcessorBlock.class);
        registration.registerBlockComponent(ManaComponentProvider.INSTANCE, ManaExtractorCoreBlock.class);
    }
}
