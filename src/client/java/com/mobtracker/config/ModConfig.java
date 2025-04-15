package com.mobtracker.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ModConfig {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("mob-tracker.json");
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(EntityType.class, new EntityTypeAdapter())
            .create();

    private static ModConfig INSTANCE;

    // Configuration options
    private boolean showCounter = true;
    private boolean showDirections = true;
    private Boolean previousCounterState = null;
    private Boolean previousDirectionsState = null;

    private final Set<String> trackedMobs = new HashSet<>();



    // Default constructor for GSON
    public ModConfig() {
        // Add default mobs to track
        trackedMobs.add("minecraft:wither_skeleton");
    }

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

    public Set<String> getTrackedMobs() {
        return trackedMobs;
    }

    public void toggleTracking(String mobId) {
        if (trackedMobs.contains(mobId)) {
            trackedMobs.remove(mobId);
        } else {
            trackedMobs.add(mobId);
        }
        save();
    }

    public Boolean getPreviousCounterState() {
        return previousCounterState;
    }

    public void setPreviousCounterState(boolean state) {
        this.previousCounterState = state;
    }

    public Boolean getPreviousDirectionsState() {
        return previousDirectionsState;
    }

    public void setPreviousDirectionsState(boolean state) {
        this.previousDirectionsState = state;
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

    // Adapter class to handle EntityType serialization
    private static class EntityTypeAdapter implements com.google.gson.JsonSerializer<EntityType<?>>, com.google.gson.JsonDeserializer<EntityType<?>> {
        @Override
        public com.google.gson.JsonElement serialize(EntityType<?> src, Type typeOfSrc, com.google.gson.JsonSerializationContext context) {
            Identifier id = Registries.ENTITY_TYPE.getId(src);
            return new com.google.gson.JsonPrimitive(id.toString());
        }

        @Override
        public EntityType<?> deserialize(com.google.gson.JsonElement json, Type typeOfT, com.google.gson.JsonDeserializationContext context) {
            String id = json.getAsString();
            // Use Identifier.tryParse instead of direct constructor
            Identifier identifier = id.contains(":")
                    ? Identifier.tryParse(id)
                    : Identifier.tryParse("minecraft:" + id);

            if (identifier == null) {
                // Fallback to a default entity if identifier is invalid
                System.err.println("Invalid entity identifier: " + id);
                return EntityType.PIG; // Default fallback
            }

            return Registries.ENTITY_TYPE.get(identifier);
        }
    }
}