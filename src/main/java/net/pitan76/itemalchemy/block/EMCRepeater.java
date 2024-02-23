package net.pitan76.itemalchemy.block;

import net.minecraft.block.MapColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pitan76.mcpitanlib.api.block.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.api.block.ExtendBlock;

import java.util.ArrayList;
import java.util.List;

public class EMCRepeater extends ExtendBlock {

    public EMCRepeater(CompatibleBlockSettings settings) {
        super(settings);
    }

    public EMCRepeater() {
        this(CompatibleBlockSettings.copy(net.minecraft.block.Blocks.STONE).mapColor(MapColor.YELLOW).strength(2f, 7.0f));
    }

    private static List<BlockPos> getNearPoses(World world, BlockPos[] blockPoses, List<BlockPos> emcRepeaterPosList) {
        List<BlockPos> blockPosList = new ArrayList<>();
        for (BlockPos pos : blockPoses) {
            if (emcRepeaterPosList.contains(pos)) continue;

            if (world.getBlockState(pos).getBlock() instanceof EMCRepeater) {
                emcRepeaterPosList.add(pos);
                BlockPos[] nearPoses = {pos.up(), pos.down(), pos.north(), pos.south(), pos.east(), pos.west()};
                blockPosList.addAll(getNearPoses(world, nearPoses, emcRepeaterPosList));
            } else {
                if (!blockPosList.contains(pos)) blockPosList.add(pos);
            }
        }

        return blockPosList;
    }

    public static List<BlockPos> getNearPoses(World world, BlockPos[] blockPoses) {
        return getNearPoses(world, blockPoses, new ArrayList<>());
    }
}
