package com.witherskelcounter.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfig {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("wither-skeleton-counter.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static ModConfig INSTANCE;

    // Configuration options
    private boolean showCounter = true;
    private boolean showDirections = true;

    // Default constructor for GSON
    public ModConfig() {}

    public static ModConfig getInstance() {
        if (INSTANCE == null) {
            load();
        }
        return INSTANCE;
    }

    public boolean isShowCounter() {
        return showCounter;
    }

    public void setShowCounter(boolean showCounter) {
        this.showCounter = showCounter;
        save();
    }

    public boolean isShowDirections() {
        return showDirections;
    }

    public void setShowDirections(boolean showDirections) {
        this.showDirections = showDirections;
        save();
    }

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                INSTANCE = GSON.fromJson(Files.readString(CONFIG_PATH), ModConfig.class);
            } catch (IOException e) {
                System.err.println("Failed to load config: " + e.getMessage());
                INSTANCE = new ModConfig();
            }
        } else {
            INSTANCE = new ModConfig();
            save();
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(INSTANCE));
        } catch (IOException e) {
            System.err.println("Failed to save config: " + e.getMessage());
        }
    }
}