package net.pitan76.itemalchemy.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.pitan76.itemalchemy.tile.base.EMCStorageBlockEntity;
import net.pitan76.mcpitanlib.api.block.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.api.event.block.*;
import net.pitan76.mcpitanlib.api.util.*;
import net.pitan76.mcpitanlib.api.util.world.WorldAccessUtil;
import net.pitan76.mcpitanlib.core.serialization.CompatMapCodec;

public class EMCCable extends EMCRepeater implements IUseableWrench, Waterloggable {

    protected CompatMapCodec<? extends Block> CODEC = CompatMapCodec.createCodecOfExtendBlock(EMCCable::new);
    public static final BooleanProperty WATERLOGGED = PropertyUtil.waterlogged();

    public static final BooleanProperty SIDE1 = PropertyUtil.createBooleanProperty("side1");
    public static final BooleanProperty SIDE2 = PropertyUtil.createBooleanProperty("side2");
    public static final BooleanProperty CONNER = PropertyUtil.createBooleanProperty("conner");
    public static final DirectionProperty FACING = PropertyUtil.createDirectionProperty("facing");

    public static final VoxelShape NONE = VoxelShapeUtil.blockCuboid(6.0D, 6.0D, 6.0D, 10.0D, 10.0D, 10.0D);
    public static final VoxelShape NS_BOTH_CONNECT = VoxelShapeUtil.blockCuboid(0.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);
    public static final VoxelShape EW_BOTH_CONNECT = VoxelShapeUtil.blockCuboid(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 16.0D);

    public static final VoxelShape NS_ONE_CONNECT_SIDE1 = VoxelShapeUtil.blockCuboid(0.0D, 6.0D, 6.0D, 13.0D, 10.0D, 10.0D);
    public static final VoxelShape EW_ONE_CONNECT_SIDE1 = VoxelShapeUtil.blockCuboid(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 13.0D);

    public static final VoxelShape NS_ONE_CONNECT_SIDE2 = VoxelShapeUtil.blockCuboid(3.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);
    public static final VoxelShape EW_ONE_CONNECT_SIDE2 = VoxelShapeUtil.blockCuboid(6.0D, 6.0D, 3.0D, 10.0D, 10.0D, 16.0D);

    public static final VoxelShape NORTH_CONNER_CONNECT = VoxelShapeUtil.union(VoxelShapeUtil.blockCuboid(0.0D, 6.0D, 6.0D, 10.0D, 10.0D, 10.0D), VoxelShapeUtil.blockCuboid(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 10.0D));
    public static final VoxelShape EAST_CONNER_CONNECT = VoxelShapeUtil.union(VoxelShapeUtil.blockCuboid(6.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D), VoxelShapeUtil.blockCuboid(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 10.0D));
    public static final VoxelShape WEST_CONNER_CONNECT = VoxelShapeUtil.union(VoxelShapeUtil.blockCuboid(6.0D, 6.0D, 6.0D, 10.0D, 10.0D, 16.0D), VoxelShapeUtil.blockCuboid(0.0D, 6.0D, 6.0D, 10.0D, 10.0D, 10.0D));
    public static final VoxelShape SOUTH_CONNER_CONNECT = VoxelShapeUtil.union(VoxelShapeUtil.blockCuboid(6.0D, 6.0D, 6.0D, 10.0D, 10.0D, 16.0D), VoxelShapeUtil.blockCuboid(6.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D));

    @Override
    public CompatMapCodec<? extends Block> getCompatCodec() {
        return CODEC;
    }

    public EMCCable(CompatibleBlockSettings settings) {
        super(settings);
        setNewDefaultState(getNewDefaultState().with(SIDE1, false).with(SIDE2, false).with(CONNER, false).with(FACING, Direction.NORTH).with(WATERLOGGED, false));
    }

    public EMCCable() {
        super();
        setNewDefaultState(getNewDefaultState().with(SIDE1, false).with(SIDE2, false).with(CONNER, false).with(FACING, Direction.NORTH).with(WATERLOGGED, false));
    }

    @Override
    public void appendProperties(AppendPropertiesArgs args) {
        super.appendProperties(args);
        args.addProperty(SIDE1, SIDE2, CONNER, FACING, WATERLOGGED);
    }

    @Override
    public boolean canPathfindThrough(CanPathfindThroughArgs args) {
        return false;
    }

    @Override
    public BlockState getPlacementState(PlacementStateArgs args) {
        return super.getPlacementState(args).with(WATERLOGGED, WorldUtil.getFluidState(args.getWorld(), args.getPos()).getFluid() == FluidUtil.water());
    }

    @Override
    public FluidState getFluidState(FluidStateArgs args) {
        return PropertyUtil.get(args.state, WATERLOGGED) ? FluidUtil.getStillWater() : super.getFluidState(args);
    }

    @Override
    public void onStateReplaced(StateReplacedEvent e) {
        super.onStateReplaced(e);
    }

    @Override
    public void onPlaced(BlockPlacedEvent e) {
        super.onPlaced(e);
        WorldUtil.setBlockState(e.world, e.pos ,getStateForNeighborUpdate(new StateForNeighborUpdateArgs(e.state, null, null, e.world, e.pos, null)));
    }

    @Override
    public ActionResult onRightClick(BlockUseEvent e) {
        return super.onRightClick(e);
    }

    @Override
    public void neighborUpdate(NeighborUpdateEvent e) {
        super.neighborUpdate(e);
    }

    @Override
    public BlockState getStateForNeighborUpdate(StateForNeighborUpdateArgs args) {
        BlockState state = args.getState();
        BlockPos pos = args.getPos();

        if (state.get(WATERLOGGED))
            WorldAccessUtil.scheduleFluidTick(args.world, pos, FluidUtil.water(), FluidUtil.getTickRate(FluidUtil.water(), args.world));

        BlockState north = args.getBlockState(pos.north());
        BlockState south = args.getBlockState(pos.south());
        BlockState east = args.getBlockState(pos.east());
        BlockState west = args.getBlockState(pos.west());
        BlockState up = args.getBlockState(pos.up());
        BlockState down = args.getBlockState(pos.down());

        boolean north_only = north.getBlock() == this || args.getBlockEntity(pos.north()) instanceof EMCStorageBlockEntity;
        boolean south_only = south.getBlock() == this || args.getBlockEntity(pos.south()) instanceof EMCStorageBlockEntity;
        boolean east_only = east.getBlock() == this || args.getBlockEntity(pos.east()) instanceof EMCStorageBlockEntity;
        boolean west_only = west.getBlock() == this || args.getBlockEntity(pos.west()) instanceof EMCStorageBlockEntity;
        boolean up_only = up.getBlock() == this || args.getBlockEntity(pos.up()) instanceof EMCStorageBlockEntity;
        boolean down_only = down.getBlock() == this || args.getBlockEntity(pos.down()) instanceof EMCStorageBlockEntity;

        if (north_only || south_only || east_only || west_only || up_only || down_only) {

            boolean both_ns = (north_only && south_only);
            boolean both_ew = (east_only && west_only);
            boolean both_ud = (up_only && down_only);

            if (east_only || west_only) {
                state = state.with(FACING, Direction.NORTH);
            } else if (north_only || south_only) {
                state = state.with(FACING, Direction.EAST);
            } else if (up_only || down_only) {
                state = state.with(FACING, Direction.UP);
            }

            if (both_ns || both_ew || both_ud) {
                return state.with(CONNER, false).with(SIDE1, true).with(SIDE2, true);
            } else if (north_only && east_only || north_only && west_only || south_only && east_only || south_only && west_only) {
                if (north_only && west_only ) {
                    return state.with(CONNER, true).with(SIDE1, false).with(SIDE2, false).with(FACING, Direction.NORTH);
                } else if (north_only && east_only) {
                    return state.with(CONNER, true).with(SIDE1, false).with(SIDE2, false).with(FACING, Direction.EAST);
                } else if (south_only && west_only) {
                    return state.with(CONNER, true).with(SIDE1, false).with(SIDE2, false).with(FACING, Direction.WEST);
                } else if (south_only && east_only) {
                    return state.with(CONNER, true).with(SIDE1, false).with(SIDE2, false).with(FACING, Direction.SOUTH);
                }


            } else if (north_only || west_only) {
                return state.with(CONNER, false).with(SIDE1, true).with(SIDE2, false);
            } else if (south_only || east_only) {
                return state.with(CONNER, false).with(SIDE1, false).with(SIDE2, true);
            }
        }

        return state.with(CONNER, false).with(SIDE1, false).with(SIDE2, false).with(FACING, Direction.NORTH);

    }

    @Override
    public VoxelShape getOutlineShape(OutlineShapeEvent e) {
        Direction direction = e.getState().get(FACING);

        if (e.getState().get(SIDE1) && e.getState().get(SIDE2)) {
            if (direction == Direction.NORTH) {
                return NS_BOTH_CONNECT;
            } else {
                return EW_BOTH_CONNECT;
            }
        } else if (e.getState().get(SIDE1)) {
            if (direction == Direction.NORTH) {
                return NS_ONE_CONNECT_SIDE1;
            } else {
                return EW_ONE_CONNECT_SIDE1;
            }
        } else if (e.getState().get(SIDE2)) {
            if (direction == Direction.NORTH) {
                return NS_ONE_CONNECT_SIDE2;
            } else {
                return EW_ONE_CONNECT_SIDE2;
            }
        } else if (e.getState().get(CONNER)) {
            if (direction == Direction.NORTH) {
                return NORTH_CONNER_CONNECT;
            } else if (direction == Direction.SOUTH) {
                return SOUTH_CONNER_CONNECT;
            } else if (direction == Direction.EAST) {
                return EAST_CONNER_CONNECT;
            } else if (direction == Direction.WEST) {
                return WEST_CONNER_CONNECT;
            }
        }
        return NONE;
    }
}
