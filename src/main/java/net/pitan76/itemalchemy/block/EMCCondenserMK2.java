package net.pitan76.itemalchemy.block;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.ActionResult;
import net.pitan76.itemalchemy.tile.EMCCondenserMK2Tile;
import net.pitan76.itemalchemy.tile.Tiles;
import net.pitan76.mcpitanlib.api.block.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.api.event.block.BlockUseEvent;
import net.pitan76.mcpitanlib.core.serialization.CompatMapCodec;
import org.jetbrains.annotations.Nullable;

public class EMCCondenserMK2 extends EMCCondenser {

    public long maxEMC = 300000;

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
    public long getMaxEMC() {
        return maxEMC;
    }

    @Override
    public ActionResult onRightClick(BlockUseEvent e) {
        if (e.isClient())
            return ActionResult.SUCCESS;

        BlockEntity blockEntity = e.getBlockEntity();
        if (blockEntity instanceof EMCCondenserMK2Tile) {
            EMCCondenserMK2Tile tile = (EMCCondenserMK2Tile)blockEntity;
            e.player.openExtendedMenu(tile);
            return ActionResult.CONSUME;
        }
        return ActionResult.PASS;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityType<T> getBlockEntityType() {
        return (BlockEntityType<T>) Tiles.EMC_CONDENSER_MK2.getOrNull();
    }
}
