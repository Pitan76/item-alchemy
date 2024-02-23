package net.pitan76.itemalchemy.block;

import net.minecraft.block.entity.BlockEntity;
import net.pitan76.itemalchemy.tile.EMCCondenserMK2Tile;
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent;
import org.jetbrains.annotations.Nullable;

public class EMCCondenserMK2 extends EMCCondenser {

    public EMCCondenserMK2() {
        super();
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(TileCreateEvent event) {
        return new EMCCondenserMK2Tile(event);
    }
}
