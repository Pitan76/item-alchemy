package net.pitan76.itemalchemy.util;

import net.minecraft.block.entity.BlockEntity;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.util.BlockEntityUtil;
import net.pitan76.mcpitanlib.midohra.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class MenuUtils {

    /**
     * Like the vanilla container check: the screen of a block entity stays valid only while that block entity is
     * still in the world and the player is within 8 blocks of it.
     */
    public static boolean canPlayerUse(@Nullable BlockEntity tile, Player player) {
        if (tile == null || player == null) return true;

        net.minecraft.world.World world = BlockEntityUtil.getWorld(tile);
        net.minecraft.util.math.BlockPos rawPos = BlockEntityUtil.getPos(tile);
        if (BlockEntityUtil.isRemoved(tile) || world == null || BlockEntityUtil.getBlockEntity(world, rawPos) != tile)
            return false;

        BlockPos pos = BlockPos.of(rawPos);
        double dx = player.getX() - (pos.getX() + 0.5);
        double dy = player.getY() - (pos.getY() + 0.5);
        double dz = player.getZ() - (pos.getZ() + 0.5);
        return dx * dx + dy * dy + dz * dz <= 64.0;
    }
}
