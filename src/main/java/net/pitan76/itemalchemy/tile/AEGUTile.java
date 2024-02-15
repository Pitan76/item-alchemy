package net.pitan76.itemalchemy.tile;

import net.pitan76.itemalchemy.block.AEGUBlock;
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent;
import net.pitan76.mcpitanlib.api.tile.ExtendBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class AEGUTile extends ExtendBlockEntity implements BlockEntityTicker<AEGUTile> {
    public int coolDown = 0; // tick

    public int getMaxCoolDown() {
        return 5 * 1; // tick
    }

    public AEGUTile(BlockEntityType<?> type, TileCreateEvent event) {
        super(type, event);
    }

    public AEGUTile(BlockPos pos, BlockState state) {
        this(new TileCreateEvent(pos, state));
    }

    public AEGUTile(BlockView world) {
        this(new TileCreateEvent(world));
    }

    public AEGUTile(TileCreateEvent event) {
        this(Tiles.AEGU.getOrNull(), event);
    }

    @Override
    public void tick(World mcWorld, BlockPos pos, BlockState state, AEGUTile blockEntity) {
        if (coolDown == 0) {
            BlockPos targetPos = getNearEMCCondenserPos(mcWorld, pos);
            if (targetPos != null) {
                mcWorld.setBlockState(pos, AEGUBlock.setConnected(state, true));
                EMCCondenserTile tile = (EMCCondenserTile) mcWorld.getBlockEntity(targetPos);
                if (tile == null) return;
                if (tile.storedEMC < tile.maxEMC)
                    tile.storedEMC += ((AEGUBlock) state.getBlock()).emc;
            } else {
                mcWorld.setBlockState(pos, AEGUBlock.setConnected(state, false));
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
            if (world.getBlockEntity(nearPos) instanceof EMCCondenserTile) {
                blockPos = nearPos;
                break;
            }
        }
        return blockPos;
    }
}
