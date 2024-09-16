package net.pitan76.itemalchemy.block;

import net.minecraft.block.Block;
import net.minecraft.util.shape.VoxelShape;
import net.pitan76.mcpitanlib.api.block.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.api.event.block.OutlineShapeEvent;
import net.pitan76.mcpitanlib.core.serialization.CompatMapCodec;

public class EMCCable extends EMCRepeater {

    protected CompatMapCodec<? extends Block> CODEC = CompatMapCodec.createCodecOfExtendBlock(EMCCable::new);

    public static final VoxelShape SHAPE = Block.createCuboidShape(6.0D, 6.0D, 6.0D, 10.0D, 10.0D, 10.0D);

    @Override
    public CompatMapCodec<? extends Block> getCompatCodec() {
        return CODEC;
    }

    public EMCCable(CompatibleBlockSettings settings) {
        super(settings);
    }

    public EMCCable() {
        super();
    }

    @Override
    public VoxelShape getOutlineShape(OutlineShapeEvent e) {
        return SHAPE;
    }
}
