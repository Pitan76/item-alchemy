package ml.pkom.itemalchemy.blocks;

import ml.pkom.itemalchemy.tiles.EMCCondenserMK2Tile;
import ml.pkom.mcpitanlibarch.api.event.block.TileCreateEvent;
import net.minecraft.block.entity.BlockEntity;
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
