package net.pitan76.itemalchemy.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.itemalchemy.gui.AlchemyTableScreenHandlerFactory;
import net.pitan76.mcpitanlib.api.block.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.api.block.ExtendBlock;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.event.block.AppendPropertiesArgs;
import net.pitan76.mcpitanlib.api.event.block.BlockUseEvent;
import net.pitan76.mcpitanlib.api.event.block.OutlineShapeEvent;
import net.pitan76.mcpitanlib.api.event.block.PlacementStateArgs;
import net.pitan76.mcpitanlib.api.util.PropertyUtil;
import net.pitan76.mcpitanlib.api.util.VoxelShapeUtil;
import net.pitan76.mcpitanlib.core.serialization.CompatMapCodec;

public class AlchemyTable extends ExtendBlock implements IUseableWrench {

    protected CompatMapCodec<? extends Block> CODEC = CompatMapCodec.createCodecOfExtendBlock(AlchemyTable::new);

    public static final DirectionProperty FACING = PropertyUtil.facing();

    @Override
    public CompatMapCodec<? extends Block> getCompatCodec() {
        return CODEC;
    }

    public AlchemyTable(CompatibleBlockSettings settings) {
        super(settings);
        setNewDefaultState(getNewDefaultState().with(FACING, Direction.DOWN));
    }

    public AlchemyTable() {
        this(CompatibleBlockSettings.copy(Blocks.STONE).mapColor(MapColor.BLACK).strength(1.5f, 7.0f));
    }

    @Override
    public ActionResult onRightClick(BlockUseEvent e) {
        if (e.isClient()) return ActionResult.SUCCESS;

        if (e.player.isServerPlayerEntity())
            EMCManager.syncS2C(e.player);

        Player player = e.player;
        player.openGuiScreen(new AlchemyTableScreenHandlerFactory());
        return ActionResult.CONSUME;
    }

    @Override
    public VoxelShape getOutlineShape(OutlineShapeEvent e) {
        Direction dir = e.state.contains(FACING) ? e.getProperty(FACING) : Direction.DOWN;

        switch (dir) {
            case UP:
                return VoxelShapeUtil.cuboid(0, 0.875, 0, 1, 1, 1);
            case NORTH:
                return VoxelShapeUtil.cuboid(0, 0, 0, 1, 1, 0.125);
            case SOUTH:
                return VoxelShapeUtil.cuboid(0, 0, 0.875, 1, 1, 1);
            case WEST:
                return VoxelShapeUtil.cuboid(0, 0, 0, 0.125, 1, 1);
            case EAST:
                return VoxelShapeUtil.cuboid(0.875, 0, 0, 1, 1, 1);
            default: // DOWN
                return VoxelShapeUtil.cuboid(0, 0, 0, 1, 0.125, 1);
        }
    }

    @Override
    public void appendProperties(AppendPropertiesArgs args) {
        super.appendProperties(args);
        args.addProperty(FACING);
    }

    @Override
    public BlockState getPlacementState(PlacementStateArgs args) {

        return args.withBlockState(FACING, args.getSide().getOpposite());
    }
}
