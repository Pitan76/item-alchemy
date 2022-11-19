package ml.pkom.itemalchemy.blocks;

import ml.pkom.mcpitanlibarch.api.block.ExtendBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class EMCRepeater extends ExtendBlock {

    public EMCRepeater(Settings settings) {
        super(settings);
    }

    public EMCRepeater() {
        this(FabricBlockSettings.of(Material.STONE, MapColor.YELLOW).strength(2f, 7.0f));
    }

    private static List<BlockPos> getNearPoses(World world, BlockPos[] blockPoses, List<BlockPos> emcRepeaterPosList) {
        List<BlockPos> blockPosList = new ArrayList<>();
        for (BlockPos pos : blockPoses) {
            if (emcRepeaterPosList.contains(pos)) continue;
            if (world.getBlockState(pos).getBlock() instanceof EMCRepeater) {
                emcRepeaterPosList.add(pos);
                BlockPos[] nearPoses = {pos.up(), pos.down(), pos.north(), pos.south(), pos.east(), pos.west()};
                blockPosList.addAll(getNearPoses(world, nearPoses));
            } else {
                blockPosList.add(pos);
            }
        }

        return blockPosList;
    }

    public static List<BlockPos> getNearPoses(World world, BlockPos[] blockPoses) {
        return getNearPoses(world, blockPoses, new ArrayList<>());
    }
}
