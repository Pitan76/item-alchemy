package net.pitan76.itemalchemy.block;

import net.minecraft.block.Block;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.shape.VoxelShape;
import net.pitan76.itemalchemy.item.Wrench;
import net.pitan76.itemalchemy.tile.base.EMCStorageBlockEntity;
import net.pitan76.mcpitanlib.api.block.v2.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.api.event.block.*;
import net.pitan76.mcpitanlib.api.state.property.BooleanProperty;
import net.pitan76.mcpitanlib.api.state.property.CompatProperties;
import net.pitan76.mcpitanlib.api.state.property.DirectionProperty;
import net.pitan76.mcpitanlib.api.util.*;
import net.pitan76.mcpitanlib.api.block.args.v2.PlacementStateArgs;
import net.pitan76.mcpitanlib.api.block.args.v2.StateForNeighborUpdateArgs;
import net.pitan76.mcpitanlib.api.block.args.v2.OutlineShapeEvent;
import net.pitan76.mcpitanlib.core.serialization.CompatMapCodec;
import net.pitan76.mcpitanlib.core.serialization.codecs.CompatBlockMapCodecUtil;
import net.pitan76.mcpitanlib.midohra.block.BlockState;
import net.pitan76.mcpitanlib.midohra.fluid.FluidWrapper;
import net.pitan76.mcpitanlib.midohra.fluid.Fluids;
import net.pitan76.mcpitanlib.midohra.util.math.BlockPos;
import net.pitan76.mcpitanlib.midohra.util.math.Direction;
import net.pitan76.mcpitanlib.midohra.world.IWorldView;
import net.pitan76.mcpitanlib.midohra.world.World;

public class EMCCable extends EMCRepeater implements IUseableWrench, Waterloggable {

    protected CompatMapCodec<? extends Block> CODEC = CompatBlockMapCodecUtil.createCodec(EMCCable::new);
    public static final BooleanProperty WATERLOGGED = CompatProperties.WATERLOGGED;

    public static final BooleanProperty NORTH = CompatProperties.NORTH;
    public static final BooleanProperty EAST = CompatProperties.EAST;
    public static final BooleanProperty SOUTH = CompatProperties.SOUTH;
    public static final BooleanProperty WEST = CompatProperties.WEST;
    public static final BooleanProperty UP = CompatProperties.UP;
    public static final BooleanProperty DOWN = CompatProperties.DOWN;

    @Override
    public CompatMapCodec<? extends Block> getCompatCodec() {
        return CODEC;
    }

    public EMCCable(CompatibleBlockSettings settings) {
        super(settings);
        setDefaultState(DirectionBoolPropertyUtil.withAll(getDefaultMidohraState(), false)
                .with(WATERLOGGED, false));
    }

    public EMCCable(CompatIdentifier id) {
        super(id);
        setDefaultState(DirectionBoolPropertyUtil.withAll(getDefaultMidohraState(), false)
                .with(WATERLOGGED, false));
    }

    @Override
    public void appendProperties(AppendPropertiesArgs args) {
        super.appendProperties(args);
        args.addAllDirectionBoolProperties();
        args.addProperty(WATERLOGGED);
    }

    @Override
    public boolean canPathfindThrough(CanPathfindThroughArgs args) {
        return false;
    }

    @Override
    public BlockState getPlacementState(PlacementStateArgs args) {
        BlockState state = super.getPlacementState(args);
        if (state == null) return null;

        FluidWrapper fluid = args.getWorldView().getFluid(args.getPos());
        return WATERLOGGED.with(state, fluid == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(FluidStateArgs args) {
        return WATERLOGGED.get(args.state) ? FluidUtil.getStillWater() : super.getFluidState(args);
    }

    @Override
    public void onStateReplaced(StateReplacedEvent e) {
        super.onStateReplaced(e);
    }

    @Override
    public void onPlaced(BlockPlacedEvent e) {
        super.onPlaced(e);

        BlockPos pos = e.getMidohraPos();
        World world = e.getMidohraWorld();

        if (world.getBlockState(pos.north()).getBlock().get() == this ||
                world.getBlockState(pos.south()).getBlock().get() == this ||
                world.getBlockState(pos.east()).getBlock().get() == this ||
                world.getBlockState(pos.west()).getBlock().get() == this ||
                world.getBlockState(pos.up()).getBlock().get() == this ||
                world.getBlockState(pos.down()).getBlock().get() == this)
            return;

        world.setBlockState(pos, getStateForNeighborUpdate(new StateForNeighborUpdateArgs(e.state, null, null, e.world, e.pos, null)));
    }

    @Override
    public CompatActionResult onRightClick(BlockUseEvent e) {
        return super.onRightClick(e);
    }

    @Override
    public void neighborUpdate(NeighborUpdateEvent e) {
        super.neighborUpdate(e);
    }

    @Override
    public BlockState getStateForNeighborUpdate(StateForNeighborUpdateArgs args) {
        BlockState state = args.getBlockState();
        BlockPos pos = args.getPos();
        IWorldView world = args.getWorldView();

        if (args.get(WATERLOGGED))
            args.getTickView().scheduleFluidTick(pos, Fluids.WATER, FluidUtil.getTickRate(FluidUtil.water(), args.world));

        if (DirectionBoolPropertyUtil.hasAll(world.getBlockState(pos))) {
            for (Direction dir : Direction.values()) {
                BlockPos neighborPos = pos.offset(dir);
                BlockState neighborState = world.getBlockState(neighborPos);
                boolean hasCable = neighborState.getBlock().get() == this
                        || neighborState.getBlock().get() instanceof EMCRepeater
                        || world.getBlockEntity(neighborPos).get() instanceof EMCStorageBlockEntity;

                if (dir == Direction.UP && state.contains(UP)) {
                    state = state.with(UP, hasCable);
                } else if (dir == Direction.DOWN && state.contains(DOWN)) {
                    state = state.with(DOWN, hasCable);
                } else if (dir == Direction.NORTH && state.contains(NORTH)) {
                    state = state.with(NORTH, hasCable);
                } else if (dir == Direction.SOUTH && state.contains(SOUTH)) {
                    state = state.with(SOUTH, hasCable);
                } else if (dir == Direction.WEST && state.contains(WEST)) {
                    state = state.with(WEST, hasCable);
                } else if (dir == Direction.EAST && state.contains(EAST)) {
                    state = state.with(EAST, hasCable);
                }
            }
        }

        return state;
    }

    @Override
    public VoxelShape getOutlineShape(OutlineShapeEvent e) {
        VoxelShape shape = VoxelShapeUtil.blockCuboid(6.0D, 6.0D, 6.0D, 10.0D, 10.0D, 10.0D);

        if (e.get(UP))
            shape = VoxelShapeUtil.union(shape, VoxelShapeUtil.blockCuboid(6.0D, 10.0D, 6.0D, 10.0D, 16.0D, 10.0D));

        if (e.get(DOWN))
            shape = VoxelShapeUtil.union(shape, VoxelShapeUtil.blockCuboid(6.0D, 0.0D, 6.0D, 10.0D, 6.0D, 10.0D));

        if (e.get(NORTH))
            shape = VoxelShapeUtil.union(shape, VoxelShapeUtil.blockCuboid(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 6.0D));

        if (e.get(EAST))
            shape = VoxelShapeUtil.union(shape, VoxelShapeUtil.blockCuboid(10.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D));

        if (e.get(SOUTH))
            shape = VoxelShapeUtil.union(shape, VoxelShapeUtil.blockCuboid(6.0D, 6.0D, 10.0D, 10.0D, 10.0D, 16.0D));

        if (e.get(WEST))
            shape = VoxelShapeUtil.union(shape, VoxelShapeUtil.blockCuboid(0.0D, 6.0D, 6.0D, 6.0D, 10.0D, 10.0D));

        return shape;
    }
}
