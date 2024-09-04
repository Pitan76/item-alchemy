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
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.pitan76.itemalchemy.tile.EMCCondenserTile;
import net.pitan76.itemalchemy.tile.Tiles;
import net.pitan76.mcpitanlib.api.block.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.api.block.ExtendBlock;
import net.pitan76.mcpitanlib.api.block.ExtendBlockEntityProvider;
import net.pitan76.mcpitanlib.api.event.block.AppendPropertiesArgs;
import net.pitan76.mcpitanlib.api.event.block.BlockUseEvent;
import net.pitan76.mcpitanlib.api.event.block.PlacementStateArgs;
import net.pitan76.mcpitanlib.api.event.block.StateReplacedEvent;
import net.pitan76.mcpitanlib.api.util.BlockStateUtil;
import net.pitan76.mcpitanlib.api.util.ItemStackUtil;
import net.pitan76.mcpitanlib.api.util.TextUtil;
import org.jetbrains.annotations.Nullable;

public class EMCCondenser extends ExtendBlock implements ExtendBlockEntityProvider {
    public static DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    private static final Text TITLE = TextUtil.translatable("container.itemalchemy.emc_condenser");

    public long maxEMC = 100000;

    public EMCCondenser(CompatibleBlockSettings settings) {
        super(settings);
        BlockStateUtil.getDefaultState(this).with(FACING, Direction.NORTH);
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

        if (e.state.isOf(e.newState.getBlock()))
            return;

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof Inventory) {
            Inventory inventory = (Inventory) blockEntity;
            inventory.setStack(0, ItemStackUtil.empty());
            ItemScatterer.spawn(world, pos, inventory);
            world.updateComparators(pos, this);
        }
        super.onStateReplaced(e);
    }

    @Override
    public @Nullable BlockState getPlacementState(PlacementStateArgs args) {
        return args.withBlockState(FACING, args.getHorizontalPlayerFacing().getOpposite());
    }

    public EMCCondenser() {
        this(CompatibleBlockSettings.copy(Blocks.STONE).mapColor(MapColor.BLACK).strength(2f, 7.0f));
    }

    @Override
    public ActionResult onRightClick(BlockUseEvent e) {
        if (e.isClient())
            return ActionResult.SUCCESS;

        BlockEntity blockEntity = e.getBlockEntity();
        if (blockEntity instanceof EMCCondenserTile) {
            EMCCondenserTile tile = (EMCCondenserTile)blockEntity;
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
        return (BlockEntityType<T>) Tiles.EMC_CONDENSER.getOrNull();
    }

    @Override
    public boolean isTick() {
        return true;
    }
}
