package com.witherskelcounter;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WitherSkeletonCounterClient implements ClientModInitializer {
    // Membuat logger khusus untuk client
    private static final Logger LOGGER = LoggerFactory.getLogger("wither-skeleton-counter-client");

    @Override
    public void onInitializeClient() {
        // Register our HUD renderer
        HudRenderCallback.EVENT.register(new WitherSkeletonHUD());

        // Menggunakan logger lokal, bukan dari WitherSkeletonCounter
        LOGGER.info("Wither Skeleton Counter HUD initialized!");
    }
}