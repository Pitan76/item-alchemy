package net.pitan76.itemalchemy.block;

import net.minecraft.block.Block;
import net.pitan76.mcpitanlib.api.block.v2.CompatBlock;
import net.pitan76.mcpitanlib.api.block.v2.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.api.util.CompatIdentifier;
import net.pitan76.mcpitanlib.api.util.color.CompatMapColor;
import net.pitan76.mcpitanlib.core.serialization.CompatMapCodec;
import net.pitan76.mcpitanlib.core.serialization.codecs.CompatBlockMapCodecUtil;

public class EMCRepeater extends CompatBlock implements IUseableWrench {

    protected CompatMapCodec<? extends Block> CODEC = CompatBlockMapCodecUtil.createCodec(EMCRepeater::new);

    @Override
    public CompatMapCodec<? extends Block> getCompatCodec() {
        return CODEC;
    }

    public EMCRepeater(CompatibleBlockSettings settings) {
        super(settings);
    }

    public EMCRepeater(CompatIdentifier id) {
        this(CompatibleBlockSettings.copy(id, net.minecraft.block.Blocks.STONE).mapColor(CompatMapColor.YELLOW).strength(2f, 7.0f));
    }
}
