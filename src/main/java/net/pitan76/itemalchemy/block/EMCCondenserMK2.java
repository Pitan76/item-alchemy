package net.pitan76.itemalchemy.block;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.pitan76.itemalchemy.tile.EMCCondenserMK2Tile;
import net.pitan76.mcpitanlib.api.block.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent;
import net.pitan76.mcpitanlib.core.serialization.CompatMapCodec;
import org.jetbrains.annotations.Nullable;

public class EMCCondenserMK2 extends EMCCondenser {

    protected CompatMapCodec<? extends Block> CODEC = CompatMapCodec.createCodecOfExtendBlock(EMCCondenserMK2::new);

    @Override
    public CompatMapCodec<? extends Block> getCompatCodec() {
        return CODEC;
    }

    public EMCCondenserMK2() {
        super();
    }

    public EMCCondenserMK2(CompatibleBlockSettings settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(TileCreateEvent event) {
        return new EMCCondenserMK2Tile(event);
    }
}
