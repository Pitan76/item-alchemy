package net.pitan76.itemalchemy.api;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pitan76.itemalchemy.block.EMCBattery;
import net.pitan76.itemalchemy.block.EMCRepeater;
import net.pitan76.itemalchemy.tile.base.EMCStorageBlockEntity;
import net.pitan76.mcpitanlib.api.util.BlockEntityUtil;
import net.pitan76.mcpitanlib.api.util.WorldUtil;

import java.util.ArrayList;
import java.util.List;

public class EMCStorageUtil {

    private static List<BlockPos> getNearPoses(World world, BlockPos[] blockPoses, List<BlockPos> emcRepeaterPosList) {
        List<BlockPos> blockPosList = new ArrayList<>();
        for (BlockPos pos : blockPoses) {
            if (emcRepeaterPosList.contains(pos)) continue;

            if (WorldUtil.getBlockState(world, pos).getBlock() instanceof EMCRepeater) {
                if (WorldUtil.getBlockState(world, pos).getBlock() instanceof EMCBattery && !blockPosList.contains(pos))
                    blockPosList.add(pos);

                emcRepeaterPosList.add(pos);
                BlockPos[] nearPoses = {pos.up(), pos.down(), pos.north(), pos.south(), pos.east(), pos.west()};
                blockPosList.addAll(getNearPoses(world, nearPoses, emcRepeaterPosList));
            } else {
                if (!blockPosList.contains(pos))
                    blockPosList.add(pos);
            }
        }

        return blockPosList;
    }

    public static List<BlockPos> getNearPoses(World world, BlockPos[] blockPoses) {
        return getNearPoses(world, blockPoses, new ArrayList<>());
    }

    /**
     * Get EMCStorageBlockEntity near EMCStorageBlockEntity
     *
     * @param world World
     * @param pos   BlockPos
     * @return List of EMCStorageBlockEntity
     */
    public static List<EMCStorageBlockEntity> getNearEMCStorages(World world, BlockPos pos) {
        List<EMCStorageBlockEntity> emcStorageBlockEntities = new ArrayList<>();

        BlockPos[] nearPoses = {pos.up(), pos.down(), pos.north(), pos.south(), pos.east(), pos.west()};
        for (BlockPos nearPos : getNearPoses(world, nearPoses)) {
            if (WorldUtil.hasBlockEntity(world, nearPos)) continue;

            BlockEntity nearTile = WorldUtil.getBlockEntity(world, nearPos);
            if (nearTile instanceof EMCStorageBlockEntity) {
                emcStorageBlockEntities.add((EMCStorageBlockEntity) nearTile);
            }
        }

        return emcStorageBlockEntities;
    }

    /**
     * Transfer EMC from one EMCStorageBlockEntity to another EMCStorageBlockEntity
     *
     * @param from EMCStorageBlockEntity to transfer EMC "from"
     * @param to   EMCStorageBlockEntity to transfer EMC "to"
     * @param emc  EMC to transfer, if -1, transfer all EMC from "from"
     * @return true if EMC is transferred successfully
     */
    public static boolean transferEMC(EMCStorageBlockEntity from, EMCStorageBlockEntity to, long emc) {
        if (!to.canInsert() || !from.canExtract()) return false;

        if (emc == -1) emc = from.storedEMC;

        // toのEMCがオーバーする場合、オーバー分を減らす
        if (to.getMaxEMC() < to.storedEMC + emc)
            emc = to.getMaxEMC() - to.storedEMC;

        if (from.storedEMC < emc) return false;

        from.storedEMC -= emc;
        to.storedEMC += emc;

        return true;
    }

    /**
     * Transfer EMC from one EMCStorageBlockEntity to another EMCStorageBlockEntity
     *
     * @param emcStorage EMCStorageBlockEntity to receive EMC
     * @param emc        EMC to transfer, if -1, transfer all EMC from around EMCStorageBlockEntity
     * @return true if EMC is transferred successfully
     */
    public static boolean transferEMC(EMCStorageBlockEntity emcStorage, long emc, boolean ignoreActive) {
        if (emcStorage == null) return false;
        if (!emcStorage.canInsert()) return false;

        World world = BlockEntityUtil.getWorld(emcStorage);
        BlockPos pos = BlockEntityUtil.getPos(emcStorage);

        boolean result = false;

        List<EMCStorageBlockEntity> emcStorageBlockEntities = getNearEMCStorages(world, pos);
        for (EMCStorageBlockEntity nearTile : emcStorageBlockEntities) {
            if (emcStorage == nearTile) continue;
            if (ignoreActive && nearTile.isActive()) continue;

            if (transferEMC(nearTile, emcStorage, emc))
                result = true;
        }

        return result;
    }

    /**
     * Transfer all EMC from one EMCStorageBlockEntity to another EMCStorageBlockEntity
     *
     * @param emcStorage EMCStorageBlockEntity to transfer EMC
     * @param ignoreActive ignore active state
     * @return true if EMC is transferred successfully
     */
    public static boolean transferAllEMC(EMCStorageBlockEntity emcStorage, boolean ignoreActive) {
        return transferEMC(emcStorage, -1, ignoreActive);
    }

    /**
     * Transfer all EMC from one EMCStorageBlockEntity to another EMCStorageBlockEntity
     *
     * @param emcStorage EMCStorageBlockEntity to transfer EMC
     * @return true if EMC is transferred successfully
     */
    public static boolean transferAllEMC(EMCStorageBlockEntity emcStorage) {
        return transferAllEMC(emcStorage, false);
    }
}
