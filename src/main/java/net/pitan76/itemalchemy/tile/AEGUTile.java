package net.pitan76.itemalchemy.tile;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pitan76.itemalchemy.block.AEGUBlock;
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent;
import net.pitan76.mcpitanlib.api.event.tile.TileTickEvent;
import net.pitan76.mcpitanlib.api.tile.CompatBlockEntity;
import net.pitan76.mcpitanlib.api.tile.ExtendBlockEntityTicker;
import net.pitan76.mcpitanlib.api.util.WorldUtil;

public class AEGUTile extends CompatBlockEntity implements ExtendBlockEntityTicker<AEGUTile> {
    public int coolDown = 0; // tick

    public int getMaxCoolDown() {
        return 5 * 1; // tick
    }

    public AEGUTile(BlockEntityType<?> type, TileCreateEvent e) {
        super(type, e);
    }

    public AEGUTile(TileCreateEvent e) {
        this(Tiles.AEGU.getOrNull(), e);
    }

    @Override
    public void tick(TileTickEvent<AEGUTile> e) {
        World world = e.world;
        BlockState state = e.state;

        if (coolDown == 0) {
            BlockPos targetPos = getNearEMCCondenserPos(world, pos);
            if (targetPos != null) {
                WorldUtil.setBlockState(world, pos, AEGUBlock.setConnected(state, true));
                EMCCondenserTile tile = (EMCCondenserTile) WorldUtil.getBlockEntity(world, targetPos);
                if (tile == null) return;
                if (tile.storedEMC < tile.maxEMC)
                    tile.storedEMC += ((AEGUBlock) state.getBlock()).emc;
            } else {
                WorldUtil.setBlockState(world, pos, AEGUBlock.setConnected(state, false));
            }
        }

        if (coolDown >= getMaxCoolDown()) {
            coolDown = 0;
        }
    }

    public static BlockPos getNearEMCCondenserPos(World world, BlockPos pos) {
        BlockPos blockPos = null;
        BlockPos[] nearPoses = {pos.up(), pos.down(), pos.north(), pos.south(), pos.east(), pos.west(),
                pos.up().north(), pos.up().south(), pos.up().east(), pos.up().west(), pos.up().north().east(), pos.up().south().east(), pos.up().north().west(), pos.up().south().west(),
                pos.down().north(), pos.down().south(), pos.down().east(), pos.down().west(), pos.down().north().east(), pos.down().south().east(), pos.down().north().west(), pos.down().south().west(),
                pos.north().west(), pos.north().east(), pos.south().west(), pos.south().east()
        };
        for (BlockPos nearPos : nearPoses) {
            if (WorldUtil.getBlockEntity(world, nearPos) instanceof EMCCondenserTile) {
                blockPos = nearPos;
                break;
            }
        }
        return blockPos;
    }
}
