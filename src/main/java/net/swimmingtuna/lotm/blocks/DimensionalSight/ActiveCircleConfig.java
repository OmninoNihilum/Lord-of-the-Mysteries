package net.swimmingtuna.lotm.blocks.DimensionalSight;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.ForgeRegistries;
import org.antlr.v4.runtime.misc.MultiMap;

import javax.annotation.Nonnull;
import java.util.*;

public class ActiveCircleConfig {
    private static final int TICKS_TO_BE_REPLACED = 201;

    // Default restricted circles - can be expanded as needed
    private static final Map<String, Integer> DEFAULT_LIMITATIONS = Map.of(
            "mahoutsukai:mahoujin_drain_life_barrier", 10,
            "mahoutsukai:scrying_mahoujin", 5
    );

    private static HashMap<String, Integer> limitationMap = null;
    private static HashMap<UUID, MultiMap<String, ActiveMahoujin>> currentActiveMap = null;

    public static String getRegistryName(Block block) {
        ResourceLocation key = getRegistryKey(block);
        return key != null ? key.toString() : "unknown";
    }

    public static ResourceLocation getRegistryKey(Block block) {
        return ForgeRegistries.BLOCKS.containsValue(block) ? ForgeRegistries.BLOCKS.getKey(block) : null;
    }

    public static boolean tryToOperate(@Nonnull BlockEntity blockEntity, @Nonnull UUID casterUUID) {
        buildLimitationMap();
        String blockName = getRegistryName(blockEntity.getBlockState().getBlock());

        if (!limitationMap.containsKey(blockName)) {
            return true; // No limitations for this block type
        }

        if (currentActiveMap == null) {
            currentActiveMap = new HashMap<>();
        }

        MultiMap<String, ActiveMahoujin> casterMap = currentActiveMap.computeIfAbsent(casterUUID, k -> new MultiMap<>());
        List<ActiveMahoujin> activeCircles = (List<ActiveMahoujin>) casterMap.computeIfAbsent(blockName, k -> new ArrayList<>());

        long currentTime = blockEntity.getLevel().getGameTime();
        int maxAllowed = limitationMap.get(blockName);

        // Check if this position is already tracked
        Optional<ActiveMahoujin> existingCircle = activeCircles.stream()
                .filter(active -> active.pos.equals(blockEntity.getBlockPos()))
                .findFirst();

        if (existingCircle.isPresent()) {
            existingCircle.get().lastActive = currentTime;
            return true;
        }

        // Remove expired circles
        activeCircles.removeIf(active -> currentTime - active.lastActive > TICKS_TO_BE_REPLACED);

        // Check if we can add a new circle
        if (activeCircles.size() < maxAllowed) {
            ActiveMahoujin newCircle = new ActiveMahoujin();
            newCircle.pos = blockEntity.getBlockPos();
            newCircle.lastActive = currentTime;
            activeCircles.add(newCircle);
            return true;
        }

        return false; // At limit and no expired circles to replace
    }

    private static void buildLimitationMap() {
        if (limitationMap == null) {
            limitationMap = new HashMap<>(DEFAULT_LIMITATIONS);
        }
    }

    /**
     * Add a custom limitation for a specific block type
     */
    public static void addLimitation(String blockName, int limit) {
        buildLimitationMap();
        limitationMap.put(blockName, limit);
    }

    /**
     * Remove limitation for a specific block type
     */
    public static void removeLimitation(String blockName) {
        if (limitationMap != null) {
            limitationMap.remove(blockName);
        }
    }

    /**
     * Get current limitations map (read-only)
     */
    public static Map<String, Integer> getLimitations() {
        buildLimitationMap();
        return Collections.unmodifiableMap(limitationMap);
    }

    public static class ActiveMahoujin {
        public BlockPos pos = null;
        public long lastActive = -1L;

        public ActiveMahoujin() {
        }
    }
}