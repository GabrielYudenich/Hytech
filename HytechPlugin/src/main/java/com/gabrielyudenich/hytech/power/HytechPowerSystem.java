package com.gabrielyudenich.hytech.power;

import com.gabrielyudenich.hytech.HytechPlugin;
import com.gabrielyudenich.hytech.interactions.HytechBlockUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.universe.world.World;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class HytechPowerSystem {
    public static final int STEP_DELAY_MS = 80;
    private static final AtomicLong VERSION = new AtomicLong();

    private static final String SWITCH_ID = "Block_Switch_Basic";
    private static final String BATTERY_ID = "Block_Battery_Sulfur";
    private static final String CABLE_ID = "Block_Cable_Copper";
    private static final String LAMP_ID = "Block_Lamp_Basic";
    private static final String PISTON_ID = "Block_Piston_Basic";

    private static final String STATE_ON = "On";
    private static final String STATE_POWERED = "Powered";

    private HytechPowerSystem() {
    }

    public static void recompute(World world, Vector3i start) {
        BlockType startBlock = world.getBlockType(start);
        if (startBlock == null || !isNetworkBlock(startBlock)) {
            return;
        }

        Set<String> visited = new HashSet<>();
        List<Vector3i> component = new ArrayList<>();
        Queue<Vector3i> queue = new ArrayDeque<>();
        queue.add(start);
        visited.add(HytechBlockUtil.key(start));

        while (!queue.isEmpty()) {
            Vector3i pos = queue.poll();
            component.add(pos);
            for (Vector3i dir : Vector3i.CARDINAL_DIRECTIONS) {
                Vector3i next = new Vector3i(pos.x + dir.x, pos.y + dir.y, pos.z + dir.z);
                String key = HytechBlockUtil.key(next);
                if (visited.contains(key)) {
                    continue;
                }
                BlockType blockType = world.getBlockType(next);
                if (blockType == null || !isNetworkBlock(blockType)) {
                    continue;
                }
                visited.add(key);
                queue.add(next);
            }
        }

        List<Vector3i> sources = new ArrayList<>();
        for (Vector3i pos : component) {
            BlockType blockType = world.getBlockType(pos);
            if (blockType != null && isSourceOn(blockType)) {
                sources.add(pos);
            }
        }

        boolean powered = !sources.isEmpty();
        Map<String, Integer> distances = powered
            ? computeDistances(world, component, sources)
            : computeDistances(world, component, List.of(start));

        long version = VERSION.incrementAndGet();
        for (Vector3i pos : component) {
            int delay = distances.getOrDefault(HytechBlockUtil.key(pos), 0) * STEP_DELAY_MS;
            schedule(world, delay, () -> applyState(world, pos, powered, version));
        }
    }

    private static Map<String, Integer> computeDistances(World world, List<Vector3i> component, List<Vector3i> sources) {
        Set<String> componentSet = new HashSet<>();
        for (Vector3i pos : component) {
            componentSet.add(HytechBlockUtil.key(pos));
        }

        Map<String, Integer> distance = new HashMap<>();
        Queue<Vector3i> queue = new ArrayDeque<>();
        for (Vector3i source : sources) {
            String key = HytechBlockUtil.key(source);
            distance.put(key, 0);
            queue.add(source);
        }

        while (!queue.isEmpty()) {
            Vector3i pos = queue.poll();
            int base = distance.getOrDefault(HytechBlockUtil.key(pos), 0);
            for (Vector3i dir : Vector3i.CARDINAL_DIRECTIONS) {
                Vector3i next = new Vector3i(pos.x + dir.x, pos.y + dir.y, pos.z + dir.z);
                String key = HytechBlockUtil.key(next);
                if (!componentSet.contains(key) || distance.containsKey(key)) {
                    continue;
                }
                distance.put(key, base + 1);
                queue.add(next);
            }
        }
        return distance;
    }

    private static void applyState(World world, Vector3i pos, boolean powered, long version) {
        if (VERSION.get() != version) {
            return;
        }
        BlockType blockType = world.getBlockType(pos);
        if (blockType == null || !isNetworkBlock(blockType)) {
            return;
        }

        if (HytechBlockUtil.isBaseBlock(blockType, CABLE_ID)) {
            HytechBlockUtil.setState(world, pos, blockType, powered ? STATE_POWERED : "default");
            return;
        }
        if (HytechBlockUtil.isBaseBlock(blockType, LAMP_ID)) {
            HytechBlockUtil.setState(world, pos, blockType, powered ? STATE_ON : "default");
            return;
        }
        if (HytechBlockUtil.isBaseBlock(blockType, PISTON_ID)) {
            HytechPistonLogic.setPowered(world, pos, powered);
        }
    }

    private static boolean isNetworkBlock(BlockType blockType) {
        return HytechBlockUtil.isBaseBlock(blockType, SWITCH_ID)
            || HytechBlockUtil.isBaseBlock(blockType, BATTERY_ID)
            || HytechBlockUtil.isBaseBlock(blockType, CABLE_ID)
            || HytechBlockUtil.isBaseBlock(blockType, LAMP_ID)
            || HytechBlockUtil.isBaseBlock(blockType, PISTON_ID);
    }

    private static boolean isSourceOn(BlockType blockType) {
        if (HytechBlockUtil.isBaseBlock(blockType, SWITCH_ID)) {
            return HytechBlockUtil.isState(blockType, STATE_ON);
        }
        if (HytechBlockUtil.isBaseBlock(blockType, BATTERY_ID)) {
            return HytechBlockUtil.isState(blockType, STATE_ON);
        }
        return false;
    }

    private static void schedule(World world, int delayMs, Runnable action) {
        Executor delayed = CompletableFuture.delayedExecutor(delayMs, TimeUnit.MILLISECONDS, world);
        CompletableFuture<Void> task = CompletableFuture.runAsync(action, delayed);
        HytechPlugin plugin = HytechPlugin.getInstance();
        if (plugin != null) {
            plugin.getTaskRegistry().registerTask(task);
        }
    }
}
