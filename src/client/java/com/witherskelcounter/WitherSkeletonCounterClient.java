package com.witherskelcounter;

import com.witherskelcounter.config.ModConfig;
import com.witherskelcounter.keybind.KeybindManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WitherSkeletonCounterClient implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("wither-skeleton-counter-client");

    @Override
    public void onInitializeClient() {
        // Load config
        ModConfig.load();

        // Register keybindings
        KeybindManager.registerKeybindings();

        // Register our HUD renderer
        HudRenderCallback.EVENT.register(new WitherSkeletonHUD());

        LOGGER.info("Wither Skeleton Counter HUD initialized!");
    }
}