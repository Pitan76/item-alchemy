package net.pitan76.itemalchemy.block;

import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.pitan76.mcpitanlib.api.block.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.api.block.ExtendBlock;
import net.pitan76.mcpitanlib.core.serialization.CompatMapCodec;

public class EMCRepeater extends ExtendBlock implements IUseableWrench {

    protected CompatMapCodec<? extends Block> CODEC = CompatMapCodec.createCodecOfExtendBlock(EMCRepeater::new);

    @Override
    public CompatMapCodec<? extends Block> getCompatCodec() {
        return CODEC;
    }

    public EMCRepeater(CompatibleBlockSettings settings) {
        super(settings);
    }

    public EMCRepeater() {
        this(CompatibleBlockSettings.copy(net.minecraft.block.Blocks.STONE).mapColor(MapColor.YELLOW).strength(2f, 7.0f));
    }
}
