package com.gabrielyudenich.hytech.interactions;

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
import com.gabrielyudenich.hytech.power.HytechPowerSystem;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public class HytechSwitchInteraction extends SimpleInstantInteraction {
    public static final BuilderCodec<HytechSwitchInteraction> CODEC = BuilderCodec.builder(
        HytechSwitchInteraction.class,
        HytechSwitchInteraction::new,
        SimpleInstantInteraction.CODEC
    ).build();

    private static final String SWITCH_ID = "Block_Switch_Basic";
    private static final String STATE_ON = "On";

    public HytechSwitchInteraction() {
        super();
    }

    public HytechSwitchInteraction(String id) {
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
        BlockType switchBlock = world.getBlockType(pos);
        if (switchBlock == null || !HytechBlockUtil.isBaseBlock(switchBlock, SWITCH_ID)) {
            return;
        }

        boolean wasOn = HytechBlockUtil.isState(switchBlock, STATE_ON);
        HytechBlockUtil.setState(world, pos, switchBlock, wasOn ? "default" : STATE_ON);
        HytechPowerSystem.recompute(world, pos);
    }
}
