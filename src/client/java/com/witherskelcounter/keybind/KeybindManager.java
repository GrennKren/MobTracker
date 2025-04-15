package com.witherskelcounter.keybind;

import com.witherskelcounter.config.ModConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeybindManager {
    public static final String KEYBIND_CATEGORY = "key.category.wither-skeleton-counter";

    private static KeyBinding toggleHudBinding;

    public static void registerKeybindings() {
        // Register the toggle HUD keybinding (default: H key)
        toggleHudBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.wither-skeleton-counter.toggle_hud",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                KEYBIND_CATEGORY
        ));

        // Register the tick event to check for key presses
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (toggleHudBinding.wasPressed()) {
                ModConfig config = ModConfig.getInstance();
                if (config.isShowCounter() || config.isShowDirections()) {
                    // If any display is enabled, disable both
                    config.setShowCounter(false);
                    config.setShowDirections(false);
                } else {
                    // If all displays are disabled, enable both
                    config.setShowCounter(true);
                    config.setShowDirections(true);
                }
            }
        });
    }
}