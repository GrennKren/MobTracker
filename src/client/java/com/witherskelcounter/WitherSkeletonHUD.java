package com.witherskelcounter;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

import java.util.Optional;

public class WitherSkeletonHUD implements HudRenderCallback {
    private static final int HUD_COLOR = 0xDDAA00; // Amber color for the HUD text
    private static final int DIRECTION_COLOR = 0xFF5500; // Orange color for direction text
    private static final int HUD_X_POS = 10; // X position from left
    private static final int HUD_Y_POS = 10; // Y position from top
    private static final int LINE_HEIGHT = 12; // Height between lines

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null || client.world == null) {
            return;
        }

        // Count wither skeletons
        int witherCount = WitherSkeletonCounter.countWitherSkeletons();

        // Render the count text
        String hudText = "Wither Skeletons: " + witherCount;
        drawContext.drawText(
                client.textRenderer,
                Text.literal(hudText),
                HUD_X_POS,
                HUD_Y_POS,
                HUD_COLOR,
                true // shadow
        );

        // Only show direction if there are any wither skeletons
        if (witherCount > 0) {
            // Get directional info for the nearest wither skeleton
            Optional<WitherSkeletonCounter.DirectionalInfo> dirInfo =
                    WitherSkeletonCounter.getNearestWitherSkeletonDirection();

            if (dirInfo.isPresent()) {
                WitherSkeletonCounter.DirectionalInfo info = dirInfo.get();

                // Format the direction text
                String directionText = String.format(
                        "Nearest: %s, %s (%.1f blocks)",
                        info.horizontalDirection,
                        info.verticalDirection,
                        info.distance
                );

                // Render the direction text on the second line
                drawContext.drawText(
                        client.textRenderer,
                        Text.literal(directionText),
                        HUD_X_POS,
                        HUD_Y_POS + LINE_HEIGHT,
                        DIRECTION_COLOR,
                        true // shadow
                );
            }
        }
    }
}