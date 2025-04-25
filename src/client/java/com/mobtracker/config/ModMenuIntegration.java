package com.mobtracker.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

import java.util.*;

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
            if (isTrackableMob(entityType)) {
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
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }

        return result.toString().trim();
    }

    private boolean isTrackableMob(EntityType<?> entityType) {
        String id = Registries.ENTITY_TYPE.getId(entityType).toString();
        return id.equals("minecraft:allay") ||
                id.equals("minecraft:armadillo") ||
                id.equals("minecraft:axolotl") ||
                id.equals("minecraft:bat") ||
                id.equals("minecraft:bee") ||
                id.equals("minecraft:blaze") ||
                id.equals("minecraft:bogged") ||
                id.equals("minecraft:breeze") ||
                id.equals("minecraft:camel") ||
                id.equals("minecraft:cat") ||
                id.equals("minecraft:cave_spider") ||
                id.equals("minecraft:chicken") ||
                id.equals("minecraft:cod") ||
                id.equals("minecraft:cow") ||
                id.equals("minecraft:creaking") ||
                id.equals("minecraft:creeper") ||
                id.equals("minecraft:dolphin") ||
                id.equals("minecraft:donkey") ||
                id.equals("minecraft:drowned") ||
                id.equals("minecraft:elder_guardian") ||
                id.equals("minecraft:ender_dragon") ||
                id.equals("minecraft:enderman") ||
                id.equals("minecraft:endermite") ||
                id.equals("minecraft:evoker") || // Corrected from evocation_illager
                id.equals("minecraft:fox") ||
                id.equals("minecraft:frog") ||
                id.equals("minecraft:ghast") ||
                id.equals("minecraft:giant") ||
                id.equals("minecraft:glow_squid") ||
                id.equals("minecraft:goat") ||
                id.equals("minecraft:guardian") ||
                id.equals("minecraft:hoglin") ||
                id.equals("minecraft:horse") ||
                id.equals("minecraft:husk") ||
                id.equals("minecraft:illusioner") || // Illusioner is in the game data, though rarely used
                id.equals("minecraft:iron_golem") ||
                id.equals("minecraft:llama") ||
                id.equals("minecraft:magma_cube") ||
                id.equals("minecraft:mooshroom") ||
                id.equals("minecraft:mule") ||
                id.equals("minecraft:ocelot") ||
                id.equals("minecraft:panda") ||
                id.equals("minecraft:parrot") ||
                id.equals("minecraft:phantom") ||
                id.equals("minecraft:pig") ||
                id.equals("minecraft:piglin") ||
                id.equals("minecraft:piglin_brute") ||
                id.equals("minecraft:pillager") ||
                id.equals("minecraft:polar_bear") ||
                id.equals("minecraft:pufferfish") ||
                id.equals("minecraft:rabbit") ||
                id.equals("minecraft:ravager") ||
                id.equals("minecraft:salmon") ||
                id.equals("minecraft:sheep") ||
                id.equals("minecraft:shulker") ||
                id.equals("minecraft:silverfish") ||
                id.equals("minecraft:skeleton") ||
                id.equals("minecraft:skeleton_horse") ||
                id.equals("minecraft:slime") ||
                id.equals("minecraft:sniffer") ||
                id.equals("minecraft:snow_golem") ||
                id.equals("minecraft:spider") ||
                id.equals("minecraft:squid") ||
                id.equals("minecraft:stray") ||
                id.equals("minecraft:strider") ||
                id.equals("minecraft:tadpole") ||
                id.equals("minecraft:trader_llama") ||
                id.equals("minecraft:tropical_fish") ||
                id.equals("minecraft:turtle") ||
                id.equals("minecraft:vex") ||
                id.equals("minecraft:villager") ||
                id.equals("minecraft:vindicator") ||
                id.equals("minecraft:wandering_trader") ||
                id.equals("minecraft:warden") ||
                id.equals("minecraft:witch") ||
                id.equals("minecraft:wither") ||
                id.equals("minecraft:wither_skeleton") ||
                id.equals("minecraft:wolf") ||
                id.equals("minecraft:zoglin") ||
                id.equals("minecraft:zombie") ||
                id.equals("minecraft:zombie_horse") || // Zombie Horse is in the game data
                id.equals("minecraft:zombie_villager") ||
                id.equals("minecraft:zombified_piglin"); // Corrected from zombie_pigman
    }
}