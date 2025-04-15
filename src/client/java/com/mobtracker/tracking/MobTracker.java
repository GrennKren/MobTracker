package com.mobtracker.tracking;

import com.mobtracker.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;

public class MobTracker {
    private static final int DETECTION_RANGE = 50; // Range in blocks to detect mobs

    /**
         * Represents directional information to the nearest tracked mob
         */
        public record DirectionalInfo(String entityName, String horizontalDirection, String verticalDirection,
                                      double distance) {
    }

    /**
     * Counts all tracked mobs in range of the player.
     * @return Map of mob name to count
     */
    public static Map<String, Integer> countTrackedMobs() {
        Map<String, Integer> mobCounts = new HashMap<>();
        Map<String, List<LivingEntity>> trackedMobs = getTrackedMobs();

        for (Map.Entry<String, List<LivingEntity>> entry : trackedMobs.entrySet()) {
            mobCounts.put(entry.getKey(), entry.getValue().size());
        }

        return mobCounts;
    }

    /**
     * Gets directional information to the nearest tracked mob of each type
     * @return Map of mob name to directional information
     */
    public static Map<String, DirectionalInfo> getNearestMobsDirections() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;

        if (player == null) {
            return Map.of();
        }

        Map<String, List<LivingEntity>> trackedMobs = getTrackedMobs();
        Map<String, DirectionalInfo> directionsMap = new HashMap<>();

        for (Map.Entry<String, List<LivingEntity>> entry : trackedMobs.entrySet()) {
            if (entry.getValue().isEmpty()) {
                continue;
            }

            // Find nearest mob of this type
            LivingEntity nearest = entry.getValue().stream()
                    .min(Comparator.comparingDouble(mob -> mob.squaredDistanceTo(player)))
                    .orElse(null);

            if (nearest == null) {
                continue;
            }

            // Get user-friendly name for entity type
            String entityName = Registries.ENTITY_TYPE.getId(nearest.getType()).getPath()
                    .replace("_", " ");
            entityName = entityName.substring(0, 1).toUpperCase() + entityName.substring(1);

            // Calculate relative position
            Vec3d playerPos = player.getPos();
            Vec3d mobPos = nearest.getPos();

            double dx = mobPos.x - playerPos.x;
            double dy = mobPos.y - playerPos.y;
            double dz = mobPos.z - playerPos.z;

            // Get player's rotation (yaw)
            float yaw = player.getYaw();

            // Convert to radians and calculate direction
            double yawRad = Math.toRadians(yaw);

            // Forward vector components based on player rotation
            double forwardX = -Math.sin(yawRad);
            double forwardZ = Math.cos(yawRad);

            // Calculate dot product to determine if mob is in front or behind
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

            directionsMap.put(entry.getKey(), new DirectionalInfo(entityName, horizontalDir, verticalDir, distance));
        }

        return directionsMap;
    }

    /**
     * Gets all tracked mobs in range of the player
     * @return Map of mob identifier to list of entities
     */
    private static Map<String, List<LivingEntity>> getTrackedMobs() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        World world = client.world;
        ModConfig config = ModConfig.getInstance();

        if (player == null || world == null) {
            return Map.of();
        }

        Map<String, List<LivingEntity>> trackedMobs = new HashMap<>();

        // Get all entities within range of player
        List<Entity> nearbyEntities = world.getOtherEntities(player,
                player.getBoundingBox().expand(DETECTION_RANGE));

        // Filter for tracked living entities
        for (Entity entity : nearbyEntities) {
            if (!(entity instanceof LivingEntity living)) {
                continue;
            }

            String id = Registries.ENTITY_TYPE.getId(entity.getType()).toString();

            // Check if this entity type is being tracked
            if (config.getTrackedMobs().contains(id)) {
                trackedMobs.computeIfAbsent(id, k -> new ArrayList<>()).add(living);
            }
        }

        return trackedMobs;
    }
}