package com.witherskelcounter;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

public class WitherSkeletonHUD implements HudRenderCallback {
    private static final int HUD_COLOR = 0xDDAA00; // Amber color for the HUD text
    private static final int HUD_X_POS = 10; // X position from left
    private static final int HUD_Y_POS = 10; // Y position from top
    
    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        
        if (client.player == null || client.world == null) {
            return;
        }

        // Count wither skeletons
        int witherCount = WitherSkeletonCounter.countWitherSkeletons();
        
        // Render the text
        String hudText = "Wither Skeletons: " + witherCount;
        drawContext.drawText(
            client.textRenderer, 
            Text.literal(hudText), 
            HUD_X_POS, 
            HUD_Y_POS, 
            HUD_COLOR, 
            true // shadow
        );
    }
}