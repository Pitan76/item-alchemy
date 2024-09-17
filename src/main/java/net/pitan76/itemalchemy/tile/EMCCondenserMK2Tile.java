package net.pitan76.itemalchemy.tile;

import net.minecraft.block.entity.BlockEntityType;
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent;

public class EMCCondenserMK2Tile extends EMCCondenserTile {
    public EMCCondenserMK2Tile(BlockEntityType<?> type, TileCreateEvent e) {
        super(type, e);
    }

    public EMCCondenserMK2Tile(TileCreateEvent e) {
        this(Tiles.EMC_CONDENSER.getOrNull(), e);//EMC_CONDENSER_MK2.getOrNull(), e);
    }
}
