package com.mobtracker.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.MultiElementListEntry;
import me.shedaniel.clothconfig2.gui.entries.NestedListListEntry;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return this::createConfigScreen;
    }

    private Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("title.mob-tracker.config"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("category.mob-tracker.general"));

        ModConfig config = ModConfig.getInstance();

        // Add toggle for counter
        general.addEntry(entryBuilder
                .startBooleanToggle(Text.translatable("option.mob-tracker.show_counter"), config.isShowCounter())
                .setDefaultValue(true)
                .setTooltip(Text.translatable("option.mob-tracker.show_counter.tooltip"))
                .setSaveConsumer(config::setShowCounter)
                .build());

        // Add toggle for directions
        general.addEntry(entryBuilder
                .startBooleanToggle(Text.translatable("option.mob-tracker.show_directions"), config.isShowDirections())
                .setDefaultValue(true)
                .setTooltip(Text.translatable("option.mob-tracker.show_directions.tooltip"))
                .setSaveConsumer(config::setShowDirections)
                .build());

        // Add mob tracking section
        ConfigCategory mobTracking = builder.getOrCreateCategory(Text.translatable("category.mob-tracker.mob_tracking"));

        // Get all possible hostile/monster mobs to track
        List<EntityType<?>> trackableMobs = new ArrayList<>();
        for (EntityType<?> entityType : Registries.ENTITY_TYPE) {
            if (isTrackableHostileMob(entityType)) {
                trackableMobs.add(entityType);
            }
        }

        // Sort them alphabetically
        trackableMobs.sort(Comparator.comparing(entityType ->
                Registries.ENTITY_TYPE.getId(entityType).toString()));

        // Add entries for each trackable mob
        for (EntityType<?> entityType : trackableMobs) {
            String mobId = Registries.ENTITY_TYPE.getId(entityType).toString();
            String mobName = getDisplayName(entityType);

            mobTracking.addEntry(entryBuilder
                    .startBooleanToggle(Text.literal(mobName), config.getTrackedMobs().contains(mobId))
                    .setDefaultValue(false)
                    .setSaveConsumer(tracked -> {
                        if (tracked && !config.getTrackedMobs().contains(mobId)) {
                            config.toggleTracking(mobId);
                        } else if (!tracked && config.getTrackedMobs().contains(mobId)) {
                            config.toggleTracking(mobId);
                        }
                    })
                    .build());
        }

        builder.setSavingRunnable(ModConfig::save);

        return builder.build();
    }

    private String getDisplayName(EntityType<?> entityType) {
        // Convert entity_type to Entity Type (capitalized)
        String name = Registries.ENTITY_TYPE.getId(entityType).getPath();
        name = name.replace('_', ' ');

        // Capitalize each word
        StringBuilder result = new StringBuilder();
        for (String word : name.split(" ")) {
            if (word.length() > 0) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }

        return result.toString().trim();
    }

    private boolean isTrackableHostileMob(EntityType<?> entityType) {
        // This is a simplified check - in reality, you'd want a more robust check
        // or perhaps a predefined list of mob types that make sense to track
        String id = Registries.ENTITY_TYPE.getId(entityType).toString();

        // Common hostile mobs (you can expand this list)
        return id.contains("zombie") ||
                id.contains("skeleton") ||
                id.contains("creeper") ||
                id.contains("spider") ||
                id.contains("enderman") ||
                id.contains("slime") ||
                id.contains("witch") ||
                id.contains("phantom") ||
                id.contains("blaze") ||
                id.contains("ghast") ||
                id.contains("magma_cube") ||
                id.contains("hoglin") ||
                id.contains("piglin") ||
                id.contains("zoglin") ||
                id.contains("guardian") ||
                id.contains("shulker") ||
                id.contains("vex") ||
                id.contains("vindicator") ||
                id.contains("ravager") ||
                id.contains("evoker") ||
                id.contains("pillager");
    }
}