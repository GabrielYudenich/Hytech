package com.gabrielyudenich.hytech.interactions;

import com.gabrielyudenich.hytech.power.HytechPistonLogic;
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
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public class HytechPistonInteraction extends SimpleInstantInteraction {
    public static final BuilderCodec<HytechPistonInteraction> CODEC = BuilderCodec.builder(
        HytechPistonInteraction.class,
        HytechPistonInteraction::new,
        SimpleInstantInteraction.CODEC
    ).build();

    private static final String PISTON_ID = "Block_Piston_Basic";

    public HytechPistonInteraction() {
        super();
    }

    public HytechPistonInteraction(String id) {
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
        BlockType pistonBlock = world.getBlockType(pos);
        if (pistonBlock == null || !HytechBlockUtil.isBaseBlock(pistonBlock, PISTON_ID)) {
            return;
        }

        Vector3i manualDir = null;
        if (context.getEntity() != null && context.getEntity().isValid()) {
            TransformComponent transform = commandBuffer.getComponent(context.getEntity(), TransformComponent.getComponentType());
            if (transform != null) {
                manualDir = HytechPistonLogic.directionFromYaw(transform.getRotation().getYaw());
            }
        }

        HytechPistonLogic.toggle(world, pos, pistonBlock, manualDir);
    }
}
