package com.mobtracker.ui;

import com.mobtracker.config.ModConfig;
import com.mobtracker.tracking.MobTracker;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Map;

public class MobTrackerHUD implements HudRenderCallback {
    private static final int HUD_COLOR = 0xDDAA00; // Amber color for the HUD text
    private static final int DIRECTION_COLOR = 0xFF5500; // Orange color for direction text
    private static final int HUD_X_POS = 10; // X position from left
    private static final int HUD_Y_POS = 10; // Y position from top
    private static final int LINE_HEIGHT = 12; // Height between lines

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        ModConfig config = ModConfig.getInstance();

        // If both display options are disabled, don't render anything
        if (!config.isShowCounter() && !config.isShowDirections()) {
            return;
        }

        if (client.player == null || client.world == null) {
            return;
        }

        int currentY = HUD_Y_POS;

        // Show counter if enabled
        if (config.isShowCounter()) {
            // Count tracked mobs
            Map<String, Integer> mobCounts = MobTracker.countTrackedMobs();

            if (!mobCounts.isEmpty()) {
                // Render the title
                drawContext.drawText(
                        client.textRenderer,
                        Text.literal("Tracked Mobs:"),
                        HUD_X_POS,
                        currentY,
                        HUD_COLOR,
                        true // shadow
                );

                currentY += LINE_HEIGHT;

                // Render each mob count
                for (Map.Entry<String, Integer> entry : mobCounts.entrySet()) {
                    if (entry.getValue() <= 0) {
                        continue;
                    }

                    String mobId = entry.getKey();

                    // Fix: Menggunakan method of() yang merupakan factory method untuk membuat Identifier
                    Identifier id = Identifier.of(mobId);

                    // Jika mobId tidak memiliki namespace, gunakan approach alternatif
                    if (!mobId.contains(":")) {
                        id = Identifier.of("minecraft", mobId);
                    }

                    String mobName = id.getPath().replace("_", " ");
                    mobName = mobName.substring(0, 1).toUpperCase() + mobName.substring(1);

                    String countText = "- " + mobName + ": " + entry.getValue();
                    drawContext.drawText(
                            client.textRenderer,
                            Text.literal(countText),
                            HUD_X_POS + 5, // indent
                            currentY,
                            HUD_COLOR,
                            true // shadow
                    );

                    currentY += LINE_HEIGHT;
                }
            }
        }

        // Show directions if enabled
        if (config.isShowDirections()) {
            // Get directional info for all tracked mobs
            Map<String, MobTracker.DirectionalInfo> directions = MobTracker.getNearestMobsDirections();

            if (!directions.isEmpty()) {
                // Only add header if we haven't already displayed counters
                if (!config.isShowCounter()) {
                    drawContext.drawText(
                            client.textRenderer,
                            Text.literal("Tracked Mobs:"),
                            HUD_X_POS,
                            currentY,
                            HUD_COLOR,
                            true // shadow
                    );

                    currentY += LINE_HEIGHT;
                }

                // Render direction for each mob
                for (MobTracker.DirectionalInfo info : directions.values()) {
                    // Format the direction text
                    String directionText = String.format(
                            "- %s: %s, %s (%.1f blocks)",
                            info.entityName(),
                            info.horizontalDirection(),
                            info.verticalDirection(),
                            info.distance()
                    );

                    // Render the direction text
                    drawContext.drawText(
                            client.textRenderer,
                            Text.literal(directionText),
                            HUD_X_POS + 5, // indent
                            currentY,
                            DIRECTION_COLOR,
                            true // shadow
                    );

                    currentY += LINE_HEIGHT;
                }
            }
        }
    }
}