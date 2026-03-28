package com.gabrielyudenich.hytech.interactions;

import com.gabrielyudenich.hytech.HytechPlugin;
import com.gabrielyudenich.hytech.power.HytechPowerSystem;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.WaitForDataFrom;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class HytechBatteryInteraction extends SimpleInstantInteraction {
    public static final BuilderCodec<HytechBatteryInteraction> CODEC = BuilderCodec.builder(
        HytechBatteryInteraction.class,
        HytechBatteryInteraction::new,
        SimpleInstantInteraction.CODEC
    ).build();

    private static final String BATTERY_ID = "Block_Battery_Sulfur";
    private static final String STATE_ON = "On";
    private static final long DISCHARGE_MS = 15000;
    private static final ConcurrentHashMap<String, CompletableFuture<?>> DISCHARGE_TASKS = new ConcurrentHashMap<>();

    public HytechBatteryInteraction() {
        super();
    }

    public HytechBatteryInteraction(String id) {
        super(id);
    }

    @Nonnull
    @Override
    public WaitForDataFrom getWaitForDataFrom() {
        return WaitForDataFrom.Server;
    }

    @Override
    protected void firstRun(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
        BlockPosition target = context.getTargetBlock();
        if (target == null) {
            return;
        }
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        if (commandBuffer == null) {
            return;
        }
        EntityStore store = commandBuffer.getExternalData();
        World world = store.getWorld();

        Vector3i pos = new Vector3i(target.x, target.y, target.z);
        BlockType batteryBlock = world.getBlockType(pos);
        if (batteryBlock == null || !HytechBlockUtil.isBaseBlock(batteryBlock, BATTERY_ID)) {
            return;
        }

        boolean wasOn = HytechBlockUtil.isState(batteryBlock, STATE_ON);
        String key = HytechBlockUtil.key(pos);

        if (wasOn) {
            cancelDischarge(key);
            HytechBlockUtil.setState(world, pos, batteryBlock, "default");
            HytechPowerSystem.recompute(world, pos);
            return;
        }

        HytechBlockUtil.setState(world, pos, batteryBlock, STATE_ON);
        HytechPowerSystem.recompute(world, pos);

        Executor delayed = CompletableFuture.delayedExecutor(DISCHARGE_MS, TimeUnit.MILLISECONDS, world);
        CompletableFuture<Void> task = CompletableFuture.runAsync(() -> {
            BlockType current = world.getBlockType(pos);
            if (current == null || !HytechBlockUtil.isBaseBlock(current, BATTERY_ID)) {
                return;
            }
            HytechBlockUtil.setState(world, pos, current, "default");
            HytechPowerSystem.recompute(world, pos);
        }, delayed);

        DISCHARGE_TASKS.put(key, task);
        HytechPlugin plugin = HytechPlugin.getInstance();
        if (plugin != null) {
            plugin.getTaskRegistry().registerTask(task);
        }
    }

    private static void cancelDischarge(String key) {
        CompletableFuture<?> existing = DISCHARGE_TASKS.remove(key);
        if (existing != null) {
            existing.cancel(false);
        }
    }
}
