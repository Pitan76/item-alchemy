package net.pitan76.itemalchemy.block;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.shape.VoxelShape;
import net.pitan76.itemalchemy.tile.Tiles;
import net.pitan76.mcpitanlib.api.block.CompatBlocks;
import net.pitan76.mcpitanlib.api.block.ExtendBlockEntityProvider;
import net.pitan76.mcpitanlib.api.block.args.v2.OutlineShapeEvent;
import net.pitan76.mcpitanlib.api.block.args.v2.PlacementStateArgs;
import net.pitan76.mcpitanlib.api.block.args.v2.StateForNeighborUpdateArgs;
import net.pitan76.mcpitanlib.api.block.v2.CompatBlock;
import net.pitan76.mcpitanlib.api.block.v2.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.api.event.block.AppendPropertiesArgs;
import net.pitan76.mcpitanlib.api.state.property.CompatProperties;
import net.pitan76.mcpitanlib.api.state.property.DirectionProperty;
import net.pitan76.mcpitanlib.api.util.BlockStateUtil;
import net.pitan76.mcpitanlib.api.util.CompatIdentifier;
import net.pitan76.mcpitanlib.api.util.VoxelShapeUtil;
import net.pitan76.mcpitanlib.core.serialization.CompatMapCodec;
import net.pitan76.mcpitanlib.core.serialization.codecs.CompatBlockMapCodecUtil;
import net.pitan76.mcpitanlib.midohra.block.BlockState;
import net.pitan76.mcpitanlib.midohra.util.math.BlockPos;
import net.pitan76.mcpitanlib.midohra.util.math.Direction;
import net.pitan76.mcpitanlib.midohra.world.IWorldView;
import org.jetbrains.annotations.Nullable;

public class InterdictionTorch extends CompatBlock implements ExtendBlockEntityProvider {

    public static final DirectionProperty FACING = CompatProperties.FACING;

    protected static final VoxelShape FLOOR_SHAPE = VoxelShapeUtil.blockCuboid(6.0, 0.0, 6.0, 10.0, 10.0, 10.0);
    protected static final VoxelShape NORTH_SHAPE = VoxelShapeUtil.blockCuboid(5.5, 3.0, 11.0, 10.5, 13.0, 16.0);
    protected static final VoxelShape SOUTH_SHAPE = VoxelShapeUtil.blockCuboid(5.5, 3.0, 0.0, 10.5, 13.0, 5.0);
    protected static final VoxelShape EAST_SHAPE = VoxelShapeUtil.blockCuboid(0.0, 3.0, 5.5, 5.0, 13.0, 10.5);
    protected static final VoxelShape WEST_SHAPE = VoxelShapeUtil.blockCuboid(11.0, 3.0, 5.5, 16.0, 13.0, 10.5);

    protected CompatMapCodec<? extends Block> CODEC = CompatBlockMapCodecUtil.createCodec(InterdictionTorch::new);

    @Override
    public CompatMapCodec<? extends Block> getCompatCodec() {
        return CODEC;
    }

    public InterdictionTorch(CompatibleBlockSettings settings) {
        super(settings);
        setDefaultState(getDefaultMidohraState().with(FACING, Direction.UP));
    }

    public InterdictionTorch(CompatIdentifier id) {
        this(CompatibleBlockSettings.copy(id, CompatBlocks.TORCH));
    }

    @Override
    public void appendProperties(AppendPropertiesArgs args) {
        super.appendProperties(args);
        args.addProperty(FACING);
    }

    @Override
    public VoxelShape getOutlineShape(OutlineShapeEvent e) {
        Direction dir = e.has(FACING) ? e.get(FACING) : Direction.UP;
        if (dir.equals(Direction.NORTH)) return NORTH_SHAPE;
        if (dir.equals(Direction.SOUTH)) return SOUTH_SHAPE;
        if (dir.equals(Direction.EAST)) return EAST_SHAPE;
        if (dir.equals(Direction.WEST)) return WEST_SHAPE;
        return FLOOR_SHAPE;
    }

    @Override
    public @Nullable BlockState getPlacementState(PlacementStateArgs args) {
        Direction side = args.getSide();
        BlockState state = getDefaultMidohraState();
        IWorldView world = args.getWorldView();
        BlockPos pos = args.getPos();

        if (side.equals(Direction.DOWN)) {
            return findSupportedPlacement(state, world, pos,
                    Direction.UP,
                    Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);
        }

        if (!isSolid(world, getSupportPos(pos, side))) {
            if (side.equals(Direction.UP)) {
                return findSupportedPlacement(state, world, pos,
                        Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);
            } else {
                return findSupportedPlacement(state, world, pos,
                        Direction.UP,
                        Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);
            }
        }

        return state.with(FACING, side);
    }

    private BlockState findSupportedPlacement(BlockState state, IWorldView world,
                                               BlockPos pos, Direction... candidates) {
        for (Direction candidate : candidates) {
            if (!candidate.equals(Direction.DOWN) && isSolid(world, getSupportPos(pos, candidate))) {
                return state.with(FACING, candidate);
            }
        }
        return BlockState.of((net.minecraft.block.BlockState) null); // no valid support — cancel placement
    }

    private boolean isSolid(IWorldView world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state != null && !state.isAir() && state.isOpaque();
    }

    @Override
    public BlockState getStateForNeighborUpdate(StateForNeighborUpdateArgs args) {
        BlockState blockState = args.getBlockState();
        Direction facing = blockState.get(FACING);

        IWorldView world = args.getWorldView();
        BlockPos pos = args.getPos();
        BlockPos supportPos = getSupportPos(pos, facing);

        if (world.getBlockState(supportPos).isAir()) {
            return BlockStateUtil.getMidohraDefaultState(CompatBlocks.AIR);
        }

        return super.getStateForNeighborUpdate(args);
    }

    private BlockPos getSupportPos(BlockPos pos, Direction facing) {
        if (facing.equals(Direction.NORTH)) return pos.south();
        if (facing.equals(Direction.SOUTH)) return pos.north();
        if (facing.equals(Direction.EAST)) return pos.west();
        if (facing.equals(Direction.WEST)) return pos.east();
        return pos.down();
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityType<T> getBlockEntityType() {
        return (BlockEntityType<T>) Tiles.INTERDICTION_TORCH.getOrNull();
    }

    @Override
    public boolean isTick() {
        return true;
    }
}
