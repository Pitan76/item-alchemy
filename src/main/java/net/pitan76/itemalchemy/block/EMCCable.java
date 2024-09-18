package net.pitan76.itemalchemy.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.WorldAccess;
import net.pitan76.itemalchemy.tile.base.EMCStorageBlockEntity;
import net.pitan76.mcpitanlib.api.block.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.api.event.block.AppendPropertiesArgs;
import net.pitan76.mcpitanlib.api.event.block.OutlineShapeEvent;
import net.pitan76.mcpitanlib.api.util.PropertyUtil;
import net.pitan76.mcpitanlib.api.util.VoxelShapeUtil;
import net.pitan76.mcpitanlib.core.serialization.CompatMapCodec;

public class EMCCable extends EMCRepeater implements IUseableWrench {

    protected CompatMapCodec<? extends Block> CODEC = CompatMapCodec.createCodecOfExtendBlock(EMCCable::new);

    public static final BooleanProperty SIDE1 = PropertyUtil.createBooleanProperty("side1");
    public static final BooleanProperty SIDE2 = PropertyUtil.createBooleanProperty("side2");
    public static final BooleanProperty CONNER = PropertyUtil.createBooleanProperty("conner");
    public static final DirectionProperty FACING = PropertyUtil.createDirectionProperty("facing");

    public static final VoxelShape NONE = VoxelShapeUtil.blockCuboid(6.0D, 6.0D, 6.0D, 10.0D, 10.0D, 10.0D);
    public static final VoxelShape NS_BOTH_CONNECT = VoxelShapeUtil.blockCuboid(0.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);
    public static final VoxelShape EW_BOTH_CONNECT = VoxelShapeUtil.blockCuboid(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 16.0D);

    public static final VoxelShape ONE_CONNECT = VoxelShapeUtil.blockCuboid(0.0D, 6.0D, 6.0D, 13.0D, 10.0D, 10.0D);
    public static final VoxelShape CONNER_CONNECT = VoxelShapeUtil.union(VoxelShapeUtil.blockCuboid(0.0D, 6.0D, 6.0D, 10.0D, 10.0D, 10.0D), VoxelShapeUtil.blockCuboid(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 10.0D));

    @Override
    public CompatMapCodec<? extends Block> getCompatCodec() {
        return CODEC;
    }

    public EMCCable(CompatibleBlockSettings settings) {
        super(settings);
        setNewDefaultState(getNewDefaultState().with(SIDE1, false).with(SIDE2, false).with(CONNER, false).with(FACING, Direction.NORTH));
    }

    public EMCCable() {
        super();
        setNewDefaultState(getNewDefaultState().with(SIDE1, false).with(SIDE2, false).with(CONNER, false).with(FACING, Direction.NORTH));
    }

    @Override
    public void appendProperties(AppendPropertiesArgs args) {
        super.appendProperties(args);
        args.addProperty(SIDE1, SIDE2, CONNER, FACING);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        // Cableを繋げる処理、同じ方向にCableがある場合はCONNERをfalseとして繋げる
        // SIDE1とSIDE2は片方、両方の場合はSIDE1、SIDE2をtrueにする
        //BlockEntity blockEntity = world.getBlockEntity(neighborPos);

        BlockState north = world.getBlockState(pos.north());
        BlockState south = world.getBlockState(pos.south());
        BlockState east = world.getBlockState(pos.east());
        BlockState west = world.getBlockState(pos.west());
        BlockState up = world.getBlockState(pos.up());
        BlockState down = world.getBlockState(pos.down());

        if (neighborState.getBlock() == this || world.getBlockEntity(neighborPos) instanceof EMCStorageBlockEntity) {
            boolean north_only = north.getBlock() == this || world.getBlockEntity(pos.north()) instanceof EMCStorageBlockEntity;
            boolean south_only = south.getBlock() == this || world.getBlockEntity(pos.south()) instanceof EMCStorageBlockEntity;
            boolean east_only = east.getBlock() == this || world.getBlockEntity(pos.east()) instanceof EMCStorageBlockEntity;
            boolean west_only = west.getBlock() == this || world.getBlockEntity(pos.west()) instanceof EMCStorageBlockEntity;
            boolean up_only = up.getBlock() == this || world.getBlockEntity(pos.up()) instanceof EMCStorageBlockEntity;
            boolean down_only = down.getBlock() == this || world.getBlockEntity(pos.down()) instanceof EMCStorageBlockEntity;

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
                return state.with(CONNER, true).with(SIDE1, false).with(SIDE2, false);
            } else if (north_only || west_only) {
                return state.with(CONNER, false).with(SIDE1, true).with(SIDE2, false);
            } else if (south_only || east_only) {
                return state.with(CONNER, false).with(SIDE1, false).with(SIDE2, true);
            }
        }

        return state.with(CONNER, false).with(SIDE1, false).with(SIDE2, false);

    }

    @Override
    public VoxelShape getOutlineShape(OutlineShapeEvent e) {
        if (e.getState().get(SIDE1) && e.getState().get(SIDE2)) {
            if (e.getState().get(FACING) == Direction.NORTH || e.getState().get(FACING) == Direction.SOUTH) {
                return NS_BOTH_CONNECT;
            } else {
                return EW_BOTH_CONNECT;
            }
        } else if (e.getState().get(SIDE1) || e.getState().get(SIDE2)) {
            return ONE_CONNECT;
        } else if (e.getState().get(CONNER)) {
            return CONNER_CONNECT;
        }
        return NONE;
    }
}
