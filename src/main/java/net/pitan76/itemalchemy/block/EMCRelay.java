package net.pitan76.itemalchemy.block;

import net.minecraft.block.Block;
import net.pitan76.mcpitanlib.api.block.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.core.serialization.CompatMapCodec;

public class EMCRelay extends EMCRepeater {

    protected CompatMapCodec<? extends Block> CODEC = CompatMapCodec.createCodecOfExtendBlock(EMCRelay::new);

    @Override
    public CompatMapCodec<? extends Block> getCompatCodec() {
        return CODEC;
    }

    public EMCRelay(CompatibleBlockSettings settings) {
        super(settings);
    }

    public EMCRelay() {
        super();
    }
}
