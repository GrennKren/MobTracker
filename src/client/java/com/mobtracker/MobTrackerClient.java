package com.mobtracker;

import com.mobtracker.config.ModConfig;
import com.mobtracker.keybind.KeybindManager;
import com.mobtracker.ui.MobTrackerHUD;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MobTrackerClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("mob-tracker");

    @Override
    public void onInitializeClient() {
        // Load config
        ModConfig.load();

        // Register keybindings
        KeybindManager.registerKeybindings();

        // Register our HUD renderer
        HudRenderCallback.EVENT.register(new MobTrackerHUD());

        LOGGER.info("Mob Tracker initialized!");
    }
}