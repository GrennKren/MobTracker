package com.witherskelcounter;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Optional;
import java.util.Comparator;

public class WitherSkeletonCounter {
    private static final int DETECTION_RANGE = 50; // Range in blocks to detect wither skeletons

    /**
     * Represents directional information to the nearest Wither Skeleton
     */
    public static class DirectionalInfo {
        public final String horizontalDirection;
        public final String verticalDirection;
        public final double distance;

        public DirectionalInfo(String horizontalDirection, String verticalDirection, double distance) {
            this.horizontalDirection = horizontalDirection;
            this.verticalDirection = verticalDirection;
            this.distance = distance;
        }
    }

    /**
     * Counts the number of Wither Skeletons in range of the player.
     * @return The number of Wither Skeletons within DETECTION_RANGE blocks of the player.
     */
    public static int countWitherSkeletons() {
        List<WitherSkeletonEntity> witherSkeletons = getWitherSkeletons();
        return witherSkeletons.size();
    }

    /**
     * Gets directional information to the nearest Wither Skeleton
     * @return Optional containing directional info if a Wither Skeleton is found, otherwise empty
     */
    public static Optional<DirectionalInfo> getNearestWitherSkeletonDirection() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;

        if (player == null) {
            return Optional.empty();
        }

        List<WitherSkeletonEntity> witherSkeletons = getWitherSkeletons();

        if (witherSkeletons.isEmpty()) {
            return Optional.empty();
        }

        // Find nearest wither skeleton
        WitherSkeletonEntity nearest = witherSkeletons.stream()
                .min(Comparator.comparingDouble(skeleton ->
                        skeleton.squaredDistanceTo(player)))
                .orElse(null);

        if (nearest == null) {
            return Optional.empty();
        }

        // Calculate relative position
        Vec3d playerPos = player.getPos();
        Vec3d skeletonPos = nearest.getPos();

        double dx = skeletonPos.x - playerPos.x;
        double dy = skeletonPos.y - playerPos.y;
        double dz = skeletonPos.z - playerPos.z;

        // Get player's rotation (yaw)
        float yaw = player.getYaw();

        // Convert to radians and calculate direction
        double yawRad = Math.toRadians(yaw);

        // Forward vector components based on player rotation
        double forwardX = -Math.sin(yawRad);
        double forwardZ = Math.cos(yawRad);

        // Calculate dot product to determine if skeleton is in front or behind
        double dotProduct = dx * forwardX + dz * forwardZ;

        // Calculate horizontal direction
        String horizontalDir;

        // First check front/back
        if (dotProduct > 0) {
            // In front, now check left/right
            // Cross product to determine left or right
            double crossProduct = dx * forwardZ - dz * forwardX;
            if (crossProduct > 0) {
                horizontalDir = "Front-Left";
            } else {
                horizontalDir = "Front-Right";
            }
        } else {
            // Behind, now check left/right
            double crossProduct = dx * forwardZ - dz * forwardX;
            if (crossProduct > 0) {
                horizontalDir = "Back-Left";
            } else {
                horizontalDir = "Back-Right";
            }
        }

        // Vertical direction is simpler
        String verticalDir = (dy > 1) ? "Above" : (dy < -1) ? "Below" : "Same level";

        // Calculate distance
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

        return Optional.of(new DirectionalInfo(horizontalDir, verticalDir, distance));
    }

    /**
     * Gets all Wither Skeletons in range of the player
     */
    private static List<WitherSkeletonEntity> getWitherSkeletons() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        World world = client.world;

        if (player == null || world == null) {
            return List.of();
        }

        // Get all entities within range of player that are wither skeletons
        return world.getEntitiesByType(
                EntityType.WITHER_SKELETON,
                player.getBoundingBox().expand(DETECTION_RANGE),
                entity -> true
        );
    }
}