package net.pitan76.itemalchemy.block;

import net.minecraft.block.Block;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
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
    public static final VoxelShape BOTH_CONNECT = VoxelShapeUtil.blockCuboid(0.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);
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
    public VoxelShape getOutlineShape(OutlineShapeEvent e) {
        if (e.getState().get(SIDE1) && e.getState().get(SIDE2)) {
            return BOTH_CONNECT;
        } else if (e.getState().get(SIDE1) || e.getState().get(SIDE2)) {
            return ONE_CONNECT;
        } else if (e.getState().get(CONNER)) {
            return CONNER_CONNECT;
        }
        return NONE;
    }
}
