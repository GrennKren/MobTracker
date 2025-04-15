package com.mobtracker.keybind;

import com.mobtracker.config.ModConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeybindManager {
    public static final String KEYBIND_CATEGORY = "key.category.mob-tracker";

    private static KeyBinding toggleHudBinding;
    private static KeyBinding toggleCounterBinding;
    private static KeyBinding toggleDirectionsBinding;

    public static void registerKeybindings() {
        // Register the toggle HUD keybinding (default: M key)
        toggleHudBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mob-tracker.toggle_hud",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                KEYBIND_CATEGORY
        ));

        // Register the toggle counter keybinding (default: J key)
        toggleCounterBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mob-tracker.toggle_counter",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_J,
                KEYBIND_CATEGORY
        ));

        // Register the toggle directions keybinding (default: K key)
        toggleDirectionsBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.mob-tracker.toggle_directions",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                KEYBIND_CATEGORY
        ));

        // Register the tick event to check for key presses
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ModConfig config = ModConfig.getInstance();

            if (toggleHudBinding.wasPressed()) {
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

            if (toggleCounterBinding.wasPressed()) {
                config.setShowCounter(!config.isShowCounter());
            }

            if (toggleDirectionsBinding.wasPressed()) {
                config.setShowDirections(!config.isShowDirections());
            }
        });
    }
}