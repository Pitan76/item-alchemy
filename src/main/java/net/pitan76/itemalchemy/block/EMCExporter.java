package net.pitan76.itemalchemy.block;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.text.Text;
import net.pitan76.itemalchemy.item.Wrench;
import net.pitan76.itemalchemy.tile.EMCExporterTile;
import net.pitan76.itemalchemy.tile.Tiles;
import net.pitan76.mcpitanlib.api.block.ExtendBlockEntityProvider;
import net.pitan76.mcpitanlib.api.block.v2.CompatBlock;
import net.pitan76.mcpitanlib.api.block.v2.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.api.event.block.BlockUseEvent;
import net.pitan76.mcpitanlib.api.event.block.StateReplacedEvent;
import net.pitan76.mcpitanlib.api.util.CompatActionResult;
import net.pitan76.mcpitanlib.api.util.CompatIdentifier;
import net.pitan76.mcpitanlib.api.util.TextUtil;
import net.pitan76.mcpitanlib.api.util.color.CompatMapColor;
import net.pitan76.mcpitanlib.core.serialization.CompatMapCodec;
import net.pitan76.mcpitanlib.core.serialization.codecs.CompatBlockMapCodecUtil;
import org.jetbrains.annotations.Nullable;

public class EMCExporter extends CompatBlock implements ExtendBlockEntityProvider, IUseableWrench {
    private static final Text TITLE = TextUtil.translatable("container.itemalchemy.emc_exporter");

    protected CompatMapCodec<? extends Block> CODEC = CompatBlockMapCodecUtil.createCodec(EMCExporter::new);

    @Override
    public CompatMapCodec<? extends Block> getCompatCodec() {
        return CODEC;
    }

    public EMCExporter(CompatibleBlockSettings settings) {
        super(settings);
    }

    public EMCExporter(CompatIdentifier id) {
        this(CompatibleBlockSettings.copy(id, Blocks.STONE).mapColor(CompatMapColor.YELLOW).strength(2f, 7.0f));
    }

    @Override
    public void onStateReplaced(StateReplacedEvent e) {
        super.onStateReplaced(e);
    }

    @Override
    public CompatActionResult onRightClick(BlockUseEvent e) {
        if (e.isClient()) return e.success();
        if (e.stack.getItem() instanceof Wrench)
            return e.pass();

        BlockEntity blockEntity = e.getBlockEntity();
        if (blockEntity instanceof EMCExporterTile) {
            EMCExporterTile tile = (EMCExporterTile)blockEntity;

            if (!tile.hasTeam()) {
                boolean isSuccess = tile.setTeam(e.player);
                if (!isSuccess) {
                    e.player.sendMessage(TextUtil.translatable("message.itemalchemy.failed_to_set_team"));
                    return e.fail();
                }
            }

            e.player.openExtendedMenu(tile);
            return e.consume();
        }

        return e.pass();
    }

    @Nullable
    @Override
    public Text getScreenTitle() {
        return TITLE;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityType<T> getBlockEntityType() {
        return (BlockEntityType<T>) Tiles.EMC_EXPORTER.getOrNull();
    }

    @Override
    public boolean isTick() {
        return true;
    }
}
