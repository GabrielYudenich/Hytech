package com.gabrielyudenich.hytech.power;

import com.gabrielyudenich.hytech.interactions.HytechBlockUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
import com.hypixel.hytale.server.core.universe.world.World;

import java.util.concurrent.ConcurrentHashMap;

public final class HytechPistonLogic {
    public static final String PISTON_ID = "Block_Piston_Basic";
    public static final String STATE_EXTENDED = "Extended";
    private static final ConcurrentHashMap<String, Vector3i> LAST_DIR = new ConcurrentHashMap<>();

    private HytechPistonLogic() {
    }

    public static void toggle(World world, Vector3i pos, BlockType pistonBlock, Vector3i manualDir) {
        if (pistonBlock == null || !HytechBlockUtil.isBaseBlock(pistonBlock, PISTON_ID)) {
            return;
        }
        if (HytechBlockUtil.isState(pistonBlock, STATE_EXTENDED)) {
            retract(world, pos, pistonBlock);
        } else {
            extend(world, pos, pistonBlock, manualDir);
        }
    }

    public static void setPowered(World world, Vector3i pos, boolean powered) {
        BlockType pistonBlock = world.getBlockType(pos);
        if (pistonBlock == null || !HytechBlockUtil.isBaseBlock(pistonBlock, PISTON_ID)) {
            return;
        }
        boolean isExtended = HytechBlockUtil.isState(pistonBlock, STATE_EXTENDED);
        if (powered && !isExtended) {
            extend(world, pos, pistonBlock, null);
        } else if (!powered && isExtended) {
            retract(world, pos, pistonBlock);
        }
    }

    public static Vector3i directionFromYaw(float yawDegrees) {
        Rotation rot = Rotation.closestOfDegrees(yawDegrees);
        Vector3i axis = rot.getAxisDirection();
        return new Vector3i(axis);
    }

    private static void extend(World world, Vector3i pos, BlockType pistonBlock, Vector3i manualDir) {
        Vector3i dir = manualDir != null ? manualDir : resolveDirection(world, pos);
        dir = normalize(dir);
        LAST_DIR.put(HytechBlockUtil.key(pos), dir);

        Vector3i front = new Vector3i(pos.x + dir.x, pos.y + dir.y, pos.z + dir.z);
        BlockType frontType = world.getBlockType(front);
        if (!HytechBlockUtil.isEmpty(frontType)) {
            Vector3i forward = new Vector3i(front.x + dir.x, front.y + dir.y, front.z + dir.z);
            BlockType forwardType = world.getBlockType(forward);
            if (HytechBlockUtil.isEmpty(forwardType)) {
                HytechBlockUtil.setBlock(world, forward, frontType);
                HytechBlockUtil.setBlock(world, front, BlockType.EMPTY);
            }
        }

        HytechBlockUtil.setState(world, pos, pistonBlock, STATE_EXTENDED);
    }

    private static void retract(World world, Vector3i pos, BlockType pistonBlock) {
        Vector3i dir = LAST_DIR.remove(HytechBlockUtil.key(pos));
        if (dir == null) {
            dir = resolveDirection(world, pos);
        }
        dir = normalize(dir);

        Vector3i front = new Vector3i(pos.x + dir.x, pos.y + dir.y, pos.z + dir.z);
        Vector3i forward = new Vector3i(front.x + dir.x, front.y + dir.y, front.z + dir.z);
        BlockType frontType = world.getBlockType(front);
        BlockType forwardType = world.getBlockType(forward);
        if (HytechBlockUtil.isEmpty(frontType) && !HytechBlockUtil.isEmpty(forwardType)) {
            HytechBlockUtil.setBlock(world, front, forwardType);
            HytechBlockUtil.setBlock(world, forward, BlockType.EMPTY);
        }

        HytechBlockUtil.setState(world, pos, pistonBlock, "default");
    }

    private static Vector3i resolveDirection(World world, Vector3i pos) {
        int rotationIndex = HytechBlockUtil.getRotationIndex(world, pos);
        RotationTuple tuple = RotationTuple.get(rotationIndex);
        Vector3i axis = tuple.yaw().getAxisDirection();
        return new Vector3i(axis);
    }

    private static Vector3i normalize(Vector3i dir) {
        int x = Integer.compare(dir.x, 0);
        int y = Integer.compare(dir.y, 0);
        int z = Integer.compare(dir.z, 0);
        if (x == 0 && y == 0 && z == 0) {
            return new Vector3i(0, 0, 1);
        }
        return new Vector3i(x, y, z);
    }
}
