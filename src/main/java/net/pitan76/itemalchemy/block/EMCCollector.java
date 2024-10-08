package net.pitan76.itemalchemy.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.pitan76.itemalchemy.item.Wrench;
import net.pitan76.itemalchemy.tile.EMCCollectorTile;
import net.pitan76.itemalchemy.tile.Tiles;
import net.pitan76.mcpitanlib.api.block.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.api.block.ExtendBlock;
import net.pitan76.mcpitanlib.api.block.ExtendBlockEntityProvider;
import net.pitan76.mcpitanlib.api.event.block.*;
import net.pitan76.mcpitanlib.api.util.BlockStateUtil;
import net.pitan76.mcpitanlib.api.util.ItemStackUtil;
import net.pitan76.mcpitanlib.api.util.TextUtil;
import net.pitan76.mcpitanlib.api.util.WorldUtil;
import net.pitan76.mcpitanlib.core.serialization.CompatMapCodec;
import org.jetbrains.annotations.Nullable;

public class EMCCollector extends ExtendBlock implements ExtendBlockEntityProvider, IUseableWrench {
    public static DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    private static final Text TITLE = TextUtil.translatable("container.itemalchemy.emc_collector");

    public long maxEMC;

    protected CompatMapCodec<? extends EMCCollector> CODEC = CompatMapCodec.createCodecOfExtendBlock(EMCCollector::new);

    @Override
    public CompatMapCodec<? extends EMCCollector> getCompatCodec() {
        return CODEC;
    }

    public EMCCollector(CompatibleBlockSettings settings, long maxEMC) {
        super(settings);
        setNewDefaultState(BlockStateUtil.getDefaultState(this).with(FACING, Direction.NORTH));
        this.maxEMC = maxEMC;
    }

    public EMCCollector(CompatibleBlockSettings settings) {
        this(settings, 10000);
    }

    public EMCCollector() {
        this(10000);
    }

    public EMCCollector(long maxEMC) {
        this(CompatibleBlockSettings.copy(Blocks.STONE).mapColor(MapColor.YELLOW).strength(2f, 7.0f), maxEMC);
    }

    @Override
    public void appendProperties(AppendPropertiesArgs args) {
        args.addProperty(FACING);
        super.appendProperties(args);
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
    public @Nullable BlockState getPlacementState(PlacementStateArgs args) {
        return args.withBlockState(FACING, args.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public ActionResult onRightClick(BlockUseEvent e) {
        if (e.isClient()) return ActionResult.SUCCESS;
        if (e.stack.getItem() instanceof Wrench)
            return ActionResult.PASS;

        BlockEntity blockEntity = e.getBlockEntity();
        if (blockEntity instanceof EMCCollectorTile) {
            EMCCollectorTile tile = (EMCCollectorTile)blockEntity;
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
    public @Nullable BlockEntity createBlockEntity(TileCreateEvent event) {
        return new EMCCollectorTile(event);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityType<T> getBlockEntityType() {
        return (BlockEntityType<T>) Tiles.EMC_COLLECTOR.getOrNull();
    }

    @Override
    public boolean isTick() {
        return true;
    }
}
