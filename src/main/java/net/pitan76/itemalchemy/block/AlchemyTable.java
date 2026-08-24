package net.pitan76.itemalchemy.block;

import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.itemalchemy.gui.AlchemyTableScreenHandlerFactory;
import net.pitan76.itemalchemy.item.Wrench;
import net.pitan76.mcpitanlib.api.block.args.v2.OutlineShapeEvent;
import net.pitan76.mcpitanlib.api.block.args.v2.PlacementStateArgs;
import net.pitan76.mcpitanlib.api.block.v2.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.api.block.v3.CompatBlock;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.event.block.AppendPropertiesArgs;
import net.pitan76.mcpitanlib.api.event.block.BlockUseEvent;
import net.pitan76.mcpitanlib.api.state.property.CompatProperties;
import net.pitan76.mcpitanlib.api.state.property.DirectionProperty;
import net.pitan76.mcpitanlib.api.util.CompatActionResult;
import net.pitan76.mcpitanlib.api.util.CompatIdentifier;
import net.pitan76.mcpitanlib.api.util.color.CompatMapColor;
import net.pitan76.mcpitanlib.core.serialization.CompatMapCodec;
import net.pitan76.mcpitanlib.core.serialization.codecs.CompatBlockMapCodecUtil;
import net.pitan76.mcpitanlib.midohra.block.BlockState;
import net.pitan76.mcpitanlib.midohra.block.MCBlocks;
import net.pitan76.mcpitanlib.midohra.util.math.Direction;
import net.pitan76.mcpitanlib.midohra.util.shape.VoxelShape;

public class AlchemyTable extends CompatBlock implements IUseableWrench {

    protected CompatMapCodec<? extends CompatBlock> CODEC = CompatBlockMapCodecUtil.createCodec(AlchemyTable::new);

    public static final DirectionProperty FACING = CompatProperties.FACING;

    @Override
    public CompatMapCodec<? extends CompatBlock> getCompatCodec() {
        return CODEC;
    }

    public AlchemyTable(CompatibleBlockSettings settings) {
        super(settings);
        setDefaultState(getDefaultMidohraState().with(FACING, Direction.DOWN));
    }

    public AlchemyTable(CompatIdentifier id) {
        this(CompatibleBlockSettings.copy(id, MCBlocks.STONE.get()).mapColor(CompatMapColor.BLACK).strength(1.5f, 7.0f));
    }

    @Override
    public CompatActionResult onRightClick(BlockUseEvent e) {
        if (e.isClient()) return e.success();
        if (e.getItem() instanceof Wrench)
            return e.pass();

        if (e.player.isServerPlayerEntity())
            EMCManager.syncS2C(e.player);

        Player player = e.player;
        player.openGuiScreen(new AlchemyTableScreenHandlerFactory());
        return e.consume();
    }

    @Override
    public VoxelShape getOutlineShapeM(OutlineShapeEvent e) {
        Direction dir = e.has(FACING) ? e.get(FACING) : Direction.DOWN;

        // DOWN
        if (dir.equals(Direction.UP))
            return VoxelShape.cuboid(0, 0.875, 0, 1, 1, 1);

        if (dir.equals(Direction.NORTH))
            return VoxelShape.cuboid(0, 0, 0, 1, 1, 0.125);

        if (dir.equals(Direction.SOUTH))
            return VoxelShape.cuboid(0, 0, 0.875, 1, 1, 1);

        if (dir.equals(Direction.WEST))
            return VoxelShape.cuboid(0, 0, 0, 0.125, 1, 1);

        if (dir.equals(Direction.EAST))
            return VoxelShape.cuboid(0.875, 0, 0, 1, 1, 1);

        return VoxelShape.cuboid(0, 0, 0, 1, 0.125, 1);
    }

    @Override
    public void appendProperties(AppendPropertiesArgs args) {
        super.appendProperties(args);
        args.addProperty(FACING);
    }

    @Override
    public BlockState getPlacementState(PlacementStateArgs args) {
        BlockState state = super.getPlacementState(args);
        if (state == null)
            state = getDefaultMidohraState();

        return state.with(FACING, args.getSide().getOpposite());
    }
}
