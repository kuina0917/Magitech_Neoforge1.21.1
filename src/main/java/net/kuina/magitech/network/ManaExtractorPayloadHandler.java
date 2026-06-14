package net.kuina.magitech.network;

import net.kuina.magitech.client.screen.ManaExtractorScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ManaExtractorPayloadHandler {
    public static void handleSyncTargets(final SyncManaTargetsPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().screen instanceof ManaExtractorScreen screen) {
                screen.updateSyncData(payload.foundPositions(), payload.enabledStates());
            }
        });
    }
}
