package net.pitan76.itemalchemy.block;

import net.minecraft.block.Block;
import net.minecraft.block.Waterloggable;
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
import net.pitan76.mcpitanlib.midohra.util.math.BlockPos;
import net.pitan76.mcpitanlib.midohra.util.math.Direction;
import net.pitan76.mcpitanlib.midohra.world.IWorldView;
import net.pitan76.mcpitanlib.midohra.world.World;

public class EMCCable extends EMCRepeater implements IUseableWrench, Waterloggable {

    protected CompatMapCodec<? extends Block> CODEC = CompatBlockMapCodecUtil.createCodec(EMCCable::new);
    public static final BooleanProperty WATERLOGGED = CompatProperties.WATERLOGGED;

    public static final BooleanProperty SIDE1 = BooleanProperty.of("side1");
    public static final BooleanProperty SIDE2 = BooleanProperty.of("side2");
    public static final BooleanProperty CONNER = BooleanProperty.of("conner");
    public static final BooleanProperty T_CHAR = BooleanProperty.of("tchar");
    public static final BooleanProperty CROSS = BooleanProperty.of("cross");

    public static final DirectionProperty FACING = CompatProperties.FACING;

    public static final VoxelShape NONE = VoxelShapeUtil.blockCuboid(6.0D, 6.0D, 6.0D, 10.0D, 10.0D, 10.0D);
    public static final VoxelShape NS_BOTH_CONNECT = VoxelShapeUtil.blockCuboid(0.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);
    public static final VoxelShape EW_BOTH_CONNECT = VoxelShapeUtil.blockCuboid(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 16.0D);
    public static final VoxelShape UD_BOTH_CONNECT = VoxelShapeUtil.blockCuboid(6.0D, 0.0D, 6.0D, 10.0D, 16.0D, 10.0D);

    public static final VoxelShape NS_ONE_CONNECT_SIDE1 = VoxelShapeUtil.blockCuboid(0.0D, 6.0D, 6.0D, 13.0D, 10.0D, 10.0D);
    public static final VoxelShape EW_ONE_CONNECT_SIDE1 = VoxelShapeUtil.blockCuboid(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 13.0D);
    public static final VoxelShape UD_ONE_CONNECT_SIDE1 = VoxelShapeUtil.blockCuboid(6.0D, 0.0D, 6.0D, 10.0D, 13.0D, 10.0D);

    public static final VoxelShape NS_ONE_CONNECT_SIDE2 = VoxelShapeUtil.blockCuboid(3.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);
    public static final VoxelShape EW_ONE_CONNECT_SIDE2 = VoxelShapeUtil.blockCuboid(6.0D, 6.0D, 3.0D, 10.0D, 10.0D, 16.0D);
    public static final VoxelShape UD_ONE_CONNECT_SIDE2 = VoxelShapeUtil.blockCuboid(6.0D, 3.0D, 6.0D, 10.0D, 16.0D, 10.0D);

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
        setDefaultState(getDefaultMidohraState().with(SIDE1, false).with(SIDE2, false).with(CONNER, false)
                .with(T_CHAR, false).with(CROSS, false)
                .with(FACING, Direction.NORTH).with(WATERLOGGED, false));
    }

    public EMCCable(CompatIdentifier id) {
        super(id);
        setDefaultState(getDefaultMidohraState().with(SIDE1, false).with(SIDE2, false).with(CONNER, false)
                .with(T_CHAR, false).with(CROSS, false)
                .with(FACING, Direction.NORTH).with(WATERLOGGED, false));
    }

    @Override
    public void appendProperties(AppendPropertiesArgs args) {
        super.appendProperties(args);
        args.addProperty(SIDE1, SIDE2, CONNER, T_CHAR, CROSS, FACING, WATERLOGGED);
    }

    @Override
    public boolean canPathfindThrough(CanPathfindThroughArgs args) {
        return false;
    }

    @Override
    public BlockState getPlacementState(PlacementStateArgs args) {
        return args.with(WATERLOGGED, args.getWorld().getFluidState(args.getPos().toMinecraft()).getFluid() == FluidUtil.water());
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

        world.setBlockState(pos ,getStateForNeighborUpdate(new StateForNeighborUpdateArgs(e.state, null, null, e.world, e.pos, null)));
    }

    @Override
    public CompatActionResult onRightClick(BlockUseEvent e) {

        if (PlatformUtil.isDevelopmentEnvironment()) {
            if (e.isClient()) return e.success();
            if (e.isSneaking()) return e.pass();
            if (e.stack.getItem() instanceof Wrench)
                return e.pass();

            BlockState state = e.getMidohraState();

            e.player.sendMessage(TextUtil.literal(
                    "facing: " + state.get(FACING).toString() +
                    " side1: " + state.get(SIDE1).toString() +
                    " side2: " + state.get(SIDE2).toString() +
                    " conner: " + state.get(CONNER).toString() +
                    " tchar: " + state.get(T_CHAR).toString() +
                    " cross: " + state.get(CROSS).toString()));

        }
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

        if (state.get(WATERLOGGED))
            args.getTickView().scheduleFluidTick(pos, FluidWrapper.of(FluidUtil.water()), FluidUtil.getTickRate(FluidUtil.water(), args.world));

        BlockState north = world.getBlockState(pos.north());
        BlockState south = world.getBlockState(pos.south());
        BlockState east = world.getBlockState(pos.east());
        BlockState west = world.getBlockState(pos.west());
        BlockState up = world.getBlockState(pos.up());
        BlockState down = world.getBlockState(pos.down());

        boolean north_only = north.getBlock().get() == this || world.getBlockEntity(pos.north()).get() instanceof EMCStorageBlockEntity || north.getBlock().get() instanceof EMCRepeater;
        boolean south_only = south.getBlock().get() == this || world.getBlockEntity(pos.south()).get() instanceof EMCStorageBlockEntity || south.getBlock().get() instanceof EMCRepeater;
        boolean east_only = east.getBlock().get() == this || world.getBlockEntity(pos.east()).get() instanceof EMCStorageBlockEntity || east.getBlock().get() instanceof EMCRepeater;
        boolean west_only = west.getBlock().get() == this || world.getBlockEntity(pos.west()).get() instanceof EMCStorageBlockEntity || west.getBlock().get() instanceof EMCRepeater;
        boolean up_only = up.getBlock().get() == this || world.getBlockEntity(pos.up()).get() instanceof EMCStorageBlockEntity || up.getBlock().get() instanceof EMCRepeater;
        boolean down_only = down.getBlock().get() == this || world.getBlockEntity(pos.down()).get() instanceof EMCStorageBlockEntity || down.getBlock().get() instanceof EMCRepeater;

        if (north_only || south_only || east_only || west_only || up_only || down_only) {

            state = state.with(SIDE1, false).with(SIDE2, false).with(CONNER, false).with(T_CHAR, false).with(CROSS, false);

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

            // 交差
            if (both_ns && both_ew || both_ns && both_ud || both_ew && both_ud) {
                return state.with(CROSS, true);
            }

            // T字
            if (both_ns && east_only) {
                return state.with(T_CHAR, true).with(FACING, Direction.SOUTH);
            } else if (both_ns && west_only) {
                return state.with(T_CHAR, true).with(FACING, Direction.NORTH);
            } else if (both_ew && north_only) {
                return state.with(T_CHAR, true).with(FACING, Direction.EAST);
            } else if (both_ew && south_only) {
                return state.with(T_CHAR, true).with(FACING, Direction.WEST);
            }

            if (both_ns || both_ew || both_ud) {
                return state.with(SIDE1, true).with(SIDE2, true);
            }

            // 角 東西南北
            if (north_only && east_only || north_only && west_only || south_only && east_only || south_only && west_only) {
                if (north_only && west_only ) {
                    return state.with(CONNER, true).with(FACING, Direction.NORTH);
                }

                if (north_only) {
                    return state.with(CONNER, true).with(FACING, Direction.EAST);
                }

                if (west_only) {
                    return state.with(CONNER, true).with(FACING, Direction.WEST);
                }

                return state.with(CONNER, true).with(FACING, Direction.SOUTH);
            }

            // 角 上下
            if (up_only && east_only || up_only && west_only || down_only && east_only || down_only && west_only ||
                    up_only && north_only || up_only && south_only || down_only && north_only || down_only && south_only) {

                if (up_only && west_only) {
                    return state.with(CONNER, true).with(FACING, Direction.UP);
                }
                if (up_only && north_only) {
                    return state.with(CONNER, true).with(FACING, Direction.UP).with(SIDE1, true);
                }
                if (up_only && south_only) {
                    return state.with(CONNER, true).with(FACING, Direction.UP).with(SIDE2, true);
                }
                if (up_only) {
                    return state.with(CONNER, true).with(FACING, Direction.UP).with(SIDE1, true).with(SIDE2, true);
                }

                if (west_only) {
                    return state.with(CONNER, true).with(FACING, Direction.DOWN);
                }
                if (north_only) {
                    return state.with(CONNER, true).with(FACING, Direction.DOWN).with(SIDE1, true);
                }
                if (south_only) {
                    return state.with(CONNER, true).with(FACING, Direction.DOWN).with(SIDE2, true);
                }
                return state.with(CONNER, true).with(FACING, Direction.DOWN).with(SIDE1, true).with(SIDE2, true);
            }


            if (north_only || west_only || up_only) {
                return state.with(SIDE1, true);
            }

            return state.with(SIDE2, true);
        }

        return state.with(CONNER, false).with(SIDE1, false).with(SIDE2, false)
                .with(T_CHAR, false).with(CROSS, false).with(FACING, Direction.NORTH);

    }

    @Override
    public VoxelShape getOutlineShape(OutlineShapeEvent e) {
        Direction direction = e.get(FACING);

        if (e.get(SIDE1) && e.get(SIDE2)) {
            // 角
            if (e.get(CONNER)) {
                if (direction == Direction.UP) {
                    return VoxelShapeUtil.union(
                            VoxelShapeUtil.blockCuboid(6.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D),
                            VoxelShapeUtil.blockCuboid(6.0D, 6.0D, 6.0D, 10.0D, 16.0D, 10.0D)
                    );
                }
                if (direction == Direction.DOWN) {
                    return VoxelShapeUtil.union(
                            VoxelShapeUtil.blockCuboid(6.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D),
                            VoxelShapeUtil.blockCuboid(6.0D, 0D, 6.0D, 10.0D, 10.0D, 10.0D)
                    );
                }
            }

            // 両端
            if (direction == Direction.NORTH) {
                return NS_BOTH_CONNECT;
            } else if (direction == Direction.EAST) {
                return EW_BOTH_CONNECT;
            } else {
                return UD_BOTH_CONNECT;
            }
        } else if (e.get(SIDE1)) {
            // 角
            if (e.get(CONNER)) {
                if (direction == Direction.UP) {
                    return VoxelShapeUtil.union(
                            VoxelShapeUtil.blockCuboid(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 10.0D),
                            VoxelShapeUtil.blockCuboid(6.0D, 6.0D, 6.0D, 10.0D, 16.0D, 10.0D)
                    );
                }
                if (direction == Direction.DOWN) {
                    return VoxelShapeUtil.union(
                            VoxelShapeUtil.blockCuboid(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 10.0D),
                            VoxelShapeUtil.blockCuboid(6.0D, 0.0D, 6.0D, 10.0D, 10.0D, 10.0D)
                    );
                }
            }

            // 一端
            if (direction == Direction.NORTH) {
                return NS_ONE_CONNECT_SIDE1;
            } else if (direction == Direction.EAST) {
                return EW_ONE_CONNECT_SIDE1;
            } else {
                return UD_ONE_CONNECT_SIDE2;
            }
        } else if (e.get(SIDE2)) {
            // 角
            if (e.get(CONNER)) {
                if (direction == Direction.UP) {
                    return VoxelShapeUtil.union(
                            VoxelShapeUtil.blockCuboid(6.0D, 6.0D, 6.0D, 10.0D, 10.0D, 16.0D),
                            VoxelShapeUtil.blockCuboid(6.0D, 6.0D, 6.0D, 10.0D, 16.0D, 10.0D)
                    );
                }
                if (direction == Direction.DOWN) {
                    return VoxelShapeUtil.union(
                            VoxelShapeUtil.blockCuboid(6.0D, 6.0D, 6.0D, 10.0D, 10.0D, 16.0D),
                            VoxelShapeUtil.blockCuboid(6.0D, 0.0D, 6.0D, 10.0D, 10.0D, 10.0D)
                    );
                }
            }

            // 一端
            if (direction == Direction.NORTH) {
                return NS_ONE_CONNECT_SIDE2;
            } else if (direction == Direction.EAST) {
                return EW_ONE_CONNECT_SIDE2;
            } else {
                return UD_ONE_CONNECT_SIDE1;
            }
        } else if (e.get(CONNER)) {
            // 角上下
            if (direction == Direction.UP) {
                return VoxelShapeUtil.union(
                        VoxelShapeUtil.blockCuboid(0.0D, 6.0D, 6.0D, 10.0D, 10.0D, 10.0D),
                        VoxelShapeUtil.blockCuboid(6.0D, 6.0D, 6.0D, 10.0D, 16.0D, 10.0D)
                );
            }
            if (direction == Direction.DOWN) {
                return VoxelShapeUtil.union(
                        VoxelShapeUtil.blockCuboid(0.0D, 6.0D, 6.0D, 10.0D, 10.0D, 10.0D),
                        VoxelShapeUtil.blockCuboid(6.0D, 0.0D, 6.0D, 10.0D, 10.0D, 10.0D)
                );
            }

            if (direction == Direction.NORTH) {
                return NORTH_CONNER_CONNECT;
            }
            if (direction == Direction.SOUTH) {
                return SOUTH_CONNER_CONNECT;
            }
            if (direction == Direction.EAST) {
                return EAST_CONNER_CONNECT;
            }
            if (direction == Direction.WEST) {
                return WEST_CONNER_CONNECT;
            }
        } else if (e.get(T_CHAR)) {
            if (direction == Direction.NORTH) {
                return NORTH_CONNER_CONNECT;
            } else if (direction == Direction.SOUTH) {
                return SOUTH_CONNER_CONNECT;
            } else if (direction == Direction.EAST) {
                return EAST_CONNER_CONNECT;
            } else if (direction == Direction.WEST) {
                return WEST_CONNER_CONNECT;
            }
        } else if (e.get(CROSS)) {
            return VoxelShapeUtil.union(NS_BOTH_CONNECT, EW_BOTH_CONNECT);
        }

        return NONE;
    }
}
