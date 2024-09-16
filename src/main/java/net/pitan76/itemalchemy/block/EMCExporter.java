package net.pitan76.itemalchemy.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pitan76.itemalchemy.tile.EMCExporterTile;
import net.pitan76.itemalchemy.tile.Tiles;
import net.pitan76.mcpitanlib.api.block.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.api.block.ExtendBlock;
import net.pitan76.mcpitanlib.api.block.ExtendBlockEntityProvider;
import net.pitan76.mcpitanlib.api.event.block.*;
import net.pitan76.mcpitanlib.api.util.ItemStackUtil;
import net.pitan76.mcpitanlib.api.util.TextUtil;
import net.pitan76.mcpitanlib.api.util.WorldUtil;
import net.pitan76.mcpitanlib.core.serialization.CompatMapCodec;
import org.jetbrains.annotations.Nullable;

public class EMCExporter extends ExtendBlock implements ExtendBlockEntityProvider {
    private static final Text TITLE = TextUtil.translatable("container.itemalchemy.emc_exporter");

    protected CompatMapCodec<? extends Block> CODEC = CompatMapCodec.createCodecOfExtendBlock(EMCExporter::new);

    @Override
    public CompatMapCodec<? extends Block> getCompatCodec() {
        return CODEC;
    }

    public EMCExporter(CompatibleBlockSettings settings) {
        super(settings);
    }

    public EMCExporter() {
        this(CompatibleBlockSettings.copy(Blocks.STONE).mapColor(MapColor.YELLOW).strength(2f, 7.0f));
    }

    @Override
    public BlockState getPlacementState(PlacementStateArgs args) {
        return super.getPlacementState(args);
    }

    @Override
    public void onStateReplaced(StateReplacedEvent e) {
        World world = e.world;
        BlockPos pos = e.pos;
        if (e.isSameState()) return;

        BlockEntity blockEntity = WorldUtil.getBlockEntity(world, pos);
        if (blockEntity instanceof Inventory) {
            Inventory inventory = (Inventory) blockEntity;
            inventory.setStack(1, ItemStackUtil.empty());
            ItemScattererUtil.spawn(world, pos, inventory);
            e.updateComparators();
        }
        super.onStateReplaced(e);
    }

    @Override
    public ActionResult onRightClick(BlockUseEvent e) {
        if (e.isClient())
            return ActionResult.SUCCESS;

        BlockEntity blockEntity = e.getBlockEntity();
        if (blockEntity instanceof EMCExporterTile) {
            EMCExporterTile tile = (EMCExporterTile)blockEntity;
            e.player.openExtendedMenu(tile);
            return ActionResult.CONSUME;
        }
        return ActionResult.PASS;
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
