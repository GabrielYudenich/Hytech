package com.gabrielyudenich.hytech.interactions;

import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockMaterial;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.universe.world.accessor.BlockAccessor;

public final class HytechBlockUtil {
    private HytechBlockUtil() {
    }

    public static String getBaseKey(BlockType blockType) {
        String defaultKey = blockType.getDefaultStateKey();
        return defaultKey != null ? defaultKey : blockType.getId();
    }

    public static boolean isBaseBlock(BlockType blockType, String baseId) {
        return baseId.equals(getBaseKey(blockType));
    }

    public static boolean isState(BlockType blockType, String state) {
        String current = BlockAccessor.getCurrentInteractionState(blockType);
        return state.equals(current);
    }

    public static boolean isEmpty(BlockType blockType) {
        return blockType == null || blockType == BlockType.EMPTY || blockType.getMaterial() == BlockMaterial.Empty;
    }

    public static void setState(BlockAccessor accessor, Vector3i position, BlockType current, String state) {
        accessor.setBlockInteractionState(position, current, state);
    }

    public static String key(Vector3i position) {
        return position.x + \":\" + position.y + \":\" + position.z;
    }
}
