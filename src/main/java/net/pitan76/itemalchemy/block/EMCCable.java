package net.pitan76.itemalchemy.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.pitan76.itemalchemy.item.Wrench;
import net.pitan76.itemalchemy.tile.base.EMCStorageBlockEntity;
import net.pitan76.mcpitanlib.api.block.v2.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.api.event.block.*;
import net.pitan76.mcpitanlib.api.state.property.BooleanProperty;
import net.pitan76.mcpitanlib.api.state.property.CompatProperties;
import net.pitan76.mcpitanlib.api.state.property.DirectionProperty;
import net.pitan76.mcpitanlib.api.util.*;
import net.pitan76.mcpitanlib.api.util.world.WorldAccessUtil;
import net.pitan76.mcpitanlib.core.serialization.CompatMapCodec;
import net.pitan76.mcpitanlib.core.serialization.codecs.CompatBlockMapCodecUtil;

public class EMCCable extends EMCRepeater implements IUseableWrench, Waterloggable {

    protected CompatMapCodec<? extends Block> CODEC = CompatBlockMapCodecUtil.createCodec(EMCCable::new);
    public static final BooleanProperty WATERLOGGED = CompatProperties.WATERLOGGED;

    public static final BooleanProperty SIDE1 = BooleanProperty.of("side1");
    public static final BooleanProperty SIDE2 = BooleanProperty.of("side2");
    public static final BooleanProperty CONNER = BooleanProperty.of("conner");
    public static final BooleanProperty T_CHAR = BooleanProperty.of("tchar");
    public static final BooleanProperty CROSS = BooleanProperty.of("cross");

    public static final DirectionProperty FACING = PropertyUtil.createDirectionProperty("facing");

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
        setNewDefaultState(getNewDefaultState().with(SIDE1.getProperty(), false).with(SIDE2.getProperty(), false).with(CONNER.getProperty(), false)
                .with(T_CHAR.getProperty(), false).with(CROSS.getProperty(), false)
                .with(FACING.getProperty(), Direction.NORTH).with(WATERLOGGED.getProperty(), false));
    }

    public EMCCable(CompatIdentifier id) {
        super(id);
        setNewDefaultState(getNewDefaultState().with(SIDE1.getProperty(), false).with(SIDE2.getProperty(), false).with(CONNER.getProperty(), false)
                .with(T_CHAR.getProperty(), false).with(CROSS.getProperty(), false)
                .with(FACING.getProperty(), Direction.NORTH).with(WATERLOGGED.getProperty(), false));
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
        return super.getPlacementState(args).with(WATERLOGGED.getProperty(), WorldUtil.getFluidState(args.getWorld(), args.getPos()).getFluid() == FluidUtil.water());
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

        if (WorldUtil.getBlockState(e.world, e.pos.north()).getBlock() == this ||
                WorldUtil.getBlockState(e.world, e.pos.south()).getBlock() == this ||
                WorldUtil.getBlockState(e.world, e.pos.east()).getBlock() == this ||
                WorldUtil.getBlockState(e.world, e.pos.west()).getBlock() == this ||
                WorldUtil.getBlockState(e.world, e.pos.up()).getBlock() == this ||
                WorldUtil.getBlockState(e.world, e.pos.down()).getBlock() == this)
            return;

        WorldUtil.setBlockState(e.world, e.pos ,getStateForNeighborUpdate(new StateForNeighborUpdateArgs(e.state, null, null, e.world, e.pos, null)));
    }

    @Override
    public CompatActionResult onRightClick(BlockUseEvent e) {

        if (PlatformUtil.isDevelopmentEnvironment()) {
            if (e.isClient()) return e.success();
            if (e.isSneaking()) return e.pass();
            if (e.stack.getItem() instanceof Wrench)
                return e.pass();

            e.player.sendMessage(TextUtil.literal("facing: " + FACING.get(e.state).toString() +
                    " side1: " + SIDE1.get(e.state).toString() +
                    " side2: " + SIDE2.get(e.state).toString() +
                    " conner: " + CONNER.get(e.state).toString() +
                    " tchar: " + T_CHAR.get(e.state).toString() +
                    " cross: " + CROSS.get(e.state).toString()));

        }
        return super.onRightClick(e);
    }

    @Override
    public void neighborUpdate(NeighborUpdateEvent e) {
        super.neighborUpdate(e);
    }

    @Override
    public BlockState getStateForNeighborUpdate(StateForNeighborUpdateArgs args) {
        args.state = super.getStateForNeighborUpdate(args);

        BlockState state = args.getState();
        BlockPos pos = args.getPos();

        if (WATERLOGGED.get(state))
            WorldAccessUtil.scheduleFluidTick(args.world, pos, FluidUtil.water(), FluidUtil.getTickRate(FluidUtil.water(), args.world));

        BlockState north = args.getBlockState(pos.north());
        BlockState south = args.getBlockState(pos.south());
        BlockState east = args.getBlockState(pos.east());
        BlockState west = args.getBlockState(pos.west());
        BlockState up = args.getBlockState(pos.up());
        BlockState down = args.getBlockState(pos.down());

        boolean north_only = north.getBlock() == this || args.getBlockEntity(pos.north()) instanceof EMCStorageBlockEntity || north.getBlock() instanceof EMCRepeater;
        boolean south_only = south.getBlock() == this || args.getBlockEntity(pos.south()) instanceof EMCStorageBlockEntity || south.getBlock() instanceof EMCRepeater;
        boolean east_only = east.getBlock() == this || args.getBlockEntity(pos.east()) instanceof EMCStorageBlockEntity || east.getBlock() instanceof EMCRepeater;
        boolean west_only = west.getBlock() == this || args.getBlockEntity(pos.west()) instanceof EMCStorageBlockEntity || west.getBlock() instanceof EMCRepeater;
        boolean up_only = up.getBlock() == this || args.getBlockEntity(pos.up()) instanceof EMCStorageBlockEntity || up.getBlock() instanceof EMCRepeater;
        boolean down_only = down.getBlock() == this || args.getBlockEntity(pos.down()) instanceof EMCStorageBlockEntity || down.getBlock() instanceof EMCRepeater;

        if (north_only || south_only || east_only || west_only || up_only || down_only) {

            state = state.with(SIDE1.getProperty(), false).with(SIDE2.getProperty(), false).with(CONNER.getProperty(), false).with(T_CHAR.getProperty(), false).with(CROSS.getProperty(), false);

            boolean both_ns = (north_only && south_only);
            boolean both_ew = (east_only && west_only);
            boolean both_ud = (up_only && down_only);

            if (east_only || west_only) {
                state = FACING.with(state, Direction.NORTH);
            } else if (north_only || south_only) {
                state = FACING.with(state, Direction.EAST);
            } else if (up_only || down_only) {
                state = FACING.with(state, Direction.UP);
            }

            // 交差
            if (both_ns && both_ew || both_ns && both_ud || both_ew && both_ud) {
                return CROSS.with(state, true);
            }

            // T字
            if (both_ns && east_only) {
                return T_CHAR.with(state, true).with(FACING.getProperty(), Direction.SOUTH);
            } else if (both_ns && west_only) {
                return T_CHAR.with(state, true).with(FACING.getProperty(), Direction.NORTH);
            } else if (both_ew && north_only) {
                return T_CHAR.with(state, true).with(FACING.getProperty(), Direction.EAST);
            } else if (both_ew && south_only) {
                return T_CHAR.with(state, true).with(FACING.getProperty(), Direction.WEST);
            }

            if (both_ns || both_ew || both_ud) {
                return SIDE1.with(state, true).with(SIDE2.getProperty(), true);
            }

            // 角 東西南北
            if (north_only && east_only || north_only && west_only || south_only && east_only || south_only && west_only) {
                if (north_only && west_only ) {
                    return CONNER.with(state, true).with(FACING.getProperty(), Direction.NORTH);
                }

                if (north_only) {
                    return CONNER.with(state, true).with(FACING.getProperty(), Direction.EAST);
                }

                if (west_only) {
                    return CONNER.with(state, true).with(FACING.getProperty(), Direction.WEST);
                }

                return CONNER.with(state, true).with(FACING.getProperty(), Direction.SOUTH);
            }

            // 角 上下
            if (up_only && east_only || up_only && west_only || down_only && east_only || down_only && west_only ||
                    up_only && north_only || up_only && south_only || down_only && north_only || down_only && south_only) {

                if (up_only && west_only) {
                    return CONNER.with(state, true).with(FACING.getProperty(), Direction.UP);
                }
                if (up_only && north_only) {
                    return CONNER.with(state, true).with(FACING.getProperty(), Direction.UP).with(SIDE1.getProperty(), true);
                }
                if (up_only && south_only) {
                    return CONNER.with(state, true).with(FACING.getProperty(), Direction.UP).with(SIDE2.getProperty(), true);
                }
                if (up_only) {
                    return CONNER.with(state, true).with(FACING.getProperty(), Direction.UP).with(SIDE1.getProperty(), true).with(SIDE2.getProperty(), true);
                }

                if (west_only) {
                    return CONNER.with(state, true).with(FACING.getProperty(), Direction.DOWN);
                }
                if (north_only) {
                    return CONNER.with(state, true).with(FACING.getProperty(), Direction.DOWN).with(SIDE1.getProperty(), true);
                }
                if (south_only) {
                    return CONNER.with(state, true).with(FACING.getProperty(), Direction.DOWN).with(SIDE2.getProperty(), true);
                }
                return CONNER.with(state, true).with(FACING.getProperty(), Direction.DOWN).with(SIDE1.getProperty(), true).with(SIDE2.getProperty(), true);
            }


            if (north_only || west_only || up_only) {
                return SIDE1.with(state, true);
            }

            return SIDE2.with(state, true);
        }

        return CONNER.with(state, false).with(SIDE1.getProperty(), false).with(SIDE2.getProperty(), false)
                .with(T_CHAR.getProperty(), false).with(CROSS.getProperty(), false).with(FACING.getProperty(), Direction.NORTH);

    }

    @Override
    public VoxelShape getOutlineShape(OutlineShapeEvent e) {
        Direction direction = e.getProperty(FACING.getProperty());

        if (e.getProperty(SIDE1.getProperty()) && e.getProperty(SIDE2.getProperty())) {
            // 角
            if (e.getProperty(CONNER.getProperty())) {
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
        } else if (e.getProperty(SIDE1.getProperty())) {
            // 角
            if (e.getProperty(CONNER.getProperty())) {
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
        } else if (e.getProperty(SIDE2.getProperty())) {
            // 角
            if (e.getProperty(CONNER.getProperty())) {
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
        } else if (e.getProperty(CONNER.getProperty())) {
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
        } else if (e.getProperty(T_CHAR.getProperty())) {
            if (direction == Direction.NORTH) {
                return NORTH_CONNER_CONNECT;
            } else if (direction == Direction.SOUTH) {
                return SOUTH_CONNER_CONNECT;
            } else if (direction == Direction.EAST) {
                return EAST_CONNER_CONNECT;
            } else if (direction == Direction.WEST) {
                return WEST_CONNER_CONNECT;
            }
        } else if (e.getProperty(CROSS.getProperty())) {
            return VoxelShapeUtil.union(NS_BOTH_CONNECT, EW_BOTH_CONNECT);
        }

        return NONE;
    }
}
