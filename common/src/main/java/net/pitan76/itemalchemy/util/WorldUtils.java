package net.pitan76.itemalchemy.util;

import com.google.common.collect.Lists;
import net.pitan76.mcpitanlib.midohra.block.BlockState;
import net.pitan76.mcpitanlib.midohra.util.math.BlockPos;
import net.pitan76.mcpitanlib.midohra.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WorldUtils {
    public static List<BlockPos> getTargetBlocks(World world, BlockPos pos, int range, boolean isHeightScan, boolean isSameBlock) {
        BlockState baseBlockState = world.getBlockState(pos);

        if (range <= 0)
            return Lists.newArrayList(pos);

        List<BlockPos> blocks = new ArrayList<>();

        for (int y = 0; y < 1 + (isHeightScan ? range * 2 : 0); y++) {
            for (int x = 0; x < 1 + range * 2; x++) {
                for (int z = 0; z < 1 + range * 2; z++) {
                    int offsetX = range - x;
                    int offsetY = isHeightScan ? range - y : 0;
                    int offsetZ = range - z;

                    BlockPos targetPos = pos.add(offsetX, offsetY, offsetZ);
                    BlockState state = world.getBlockState(targetPos);

                    if (state.isAir()) continue;

                    if (!state.getBlock().equals(baseBlockState.getBlock()) && isSameBlock) continue;

                    blocks.add(targetPos);
                }
            }
        }

        return blocks;
    }

    public static List<net.minecraft.util.math.BlockPos> getTargetBlocks(net.minecraft.world.World world, net.minecraft.util.math.BlockPos pos, int range, boolean isHeightScan, boolean isSameBlock) {
        return getTargetBlocks(World.of(world), BlockPos.of(pos), range, isHeightScan, isSameBlock).stream().map(BlockPos::toMinecraft).collect(Collectors.toList());
    }
}
