package com.witherskelcounter;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.Collectors;

public class WitherSkeletonCounter {
    private static final int DETECTION_RANGE = 50; // Range in blocks to detect wither skeletons

    /**
     * Counts the number of Wither Skeletons in range of the player.
     * @return The number of Wither Skeletons within DETECTION_RANGE blocks of the player.
     */
    public static int countWitherSkeletons() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        World world = client.world;
        
        if (player == null || world == null) {
            return 0;
        }
        
        // Get all entities within range of player that are wither skeletons
        List<WitherSkeletonEntity> witherSkeletons = world.getEntitiesByType(
            EntityType.WITHER_SKELETON,
            player.getBoundingBox().expand(DETECTION_RANGE),
            entity -> true
        );
        
        return witherSkeletons.size();
    }
}