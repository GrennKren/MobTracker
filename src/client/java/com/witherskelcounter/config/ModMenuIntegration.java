package com.witherskelcounter.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return this::createConfigScreen;
    }

    private Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("title.wither-skeleton-counter.config"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("category.wither-skeleton-counter.general"));

        ModConfig config = ModConfig.getInstance();

        // Add toggle for counter
        general.addEntry(entryBuilder
                .startBooleanToggle(Text.translatable("option.wither-skeleton-counter.show_counter"), config.isShowCounter())
                .setDefaultValue(true)
                .setTooltip(Text.translatable("option.wither-skeleton-counter.show_counter.tooltip"))
                .setSaveConsumer(config::setShowCounter)
                .build());

        // Add toggle for directions
        general.addEntry(entryBuilder
                .startBooleanToggle(Text.translatable("option.wither-skeleton-counter.show_directions"), config.isShowDirections())
                .setDefaultValue(true)
                .setTooltip(Text.translatable("option.wither-skeleton-counter.show_directions.tooltip"))
                .setSaveConsumer(config::setShowDirections)
                .build());

        builder.setSavingRunnable(ModConfig::save);

        return builder.build();
    }
}