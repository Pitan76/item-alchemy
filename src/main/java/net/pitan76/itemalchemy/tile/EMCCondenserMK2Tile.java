package net.pitan76.itemalchemy.tile;

import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class EMCCondenserMK2Tile extends EMCCondenserTile {
    public EMCCondenserMK2Tile(BlockEntityType<?> type, TileCreateEvent event) {
        super(type, event);
    }

    public EMCCondenserMK2Tile(BlockPos pos, BlockState state) {
        this(new TileCreateEvent(pos, state));
    }

    public EMCCondenserMK2Tile(BlockView world) {
        this(new TileCreateEvent(world));
    }

    public EMCCondenserMK2Tile(TileCreateEvent event) {
        this(Tiles.EMC_CONDENSER.getOrNull(), event);//EMC_CONDENSER_MK2.getOrNull(), event);
    }
}
