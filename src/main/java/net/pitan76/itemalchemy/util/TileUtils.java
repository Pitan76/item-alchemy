package net.pitan76.itemalchemy.util;

import net.pitan76.mcpitanlib.api.tile.CompatBlockEntity;
import net.pitan76.mcpitanlib.api.util.BlockStateUtil;
import net.pitan76.mcpitanlib.api.util.WorldUtil;

public class TileUtils {

    /**
     * Whether the block at the tile's position is no longer the tile's block. Since 1.21.5 vanilla drops a
     * container's contents (BlockEntity#preRemoveSideEffects) after the new block state is set and before the block
     * entity is removed, so this is true exactly while the contents are being dropped.
     */
    public static boolean isBlockGone(CompatBlockEntity tile) {
        net.minecraft.world.World world = tile.callGetWorld();
        if (world == null) return false;

        return BlockStateUtil.getBlock(WorldUtil.getBlockState(world, tile.callGetPos()))
                != BlockStateUtil.getBlock(tile.callGetCachedState());
    }
}
