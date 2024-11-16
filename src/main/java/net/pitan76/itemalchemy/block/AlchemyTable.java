package net.pitan76.itemalchemy.block;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.shape.VoxelShape;
import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.itemalchemy.gui.AlchemyTableScreenHandlerFactory;
import net.pitan76.itemalchemy.item.Wrench;
import net.pitan76.mcpitanlib.api.block.args.v2.OutlineShapeEvent;
import net.pitan76.mcpitanlib.api.block.args.v2.PlacementStateArgs;
import net.pitan76.mcpitanlib.api.block.v2.CompatBlock;
import net.pitan76.mcpitanlib.api.block.v2.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.event.block.AppendPropertiesArgs;
import net.pitan76.mcpitanlib.api.event.block.BlockUseEvent;
import net.pitan76.mcpitanlib.api.state.property.CompatProperties;
import net.pitan76.mcpitanlib.api.state.property.DirectionProperty;
import net.pitan76.mcpitanlib.api.util.CompatActionResult;
import net.pitan76.mcpitanlib.api.util.CompatIdentifier;
import net.pitan76.mcpitanlib.api.util.VoxelShapeUtil;
import net.pitan76.mcpitanlib.api.util.color.CompatMapColor;
import net.pitan76.mcpitanlib.core.serialization.CompatMapCodec;
import net.pitan76.mcpitanlib.core.serialization.codecs.CompatBlockMapCodecUtil;
import net.pitan76.mcpitanlib.midohra.block.BlockState;
import net.pitan76.mcpitanlib.midohra.util.math.Direction;

public class AlchemyTable extends CompatBlock implements IUseableWrench {

    protected CompatMapCodec<? extends Block> CODEC = CompatBlockMapCodecUtil.createCodec(AlchemyTable::new);

    public static final DirectionProperty FACING = CompatProperties.FACING;

    @Override
    public CompatMapCodec<? extends Block> getCompatCodec() {
        return CODEC;
    }

    public AlchemyTable(CompatibleBlockSettings settings) {
        super(settings);
        setDefaultState(getDefaultMidohraState().with(FACING, Direction.DOWN));
    }

    public AlchemyTable(CompatIdentifier id) {
        this(CompatibleBlockSettings.copy(id, Blocks.STONE).mapColor(CompatMapColor.BLACK).strength(1.5f, 7.0f));
    }

    @Override
    public CompatActionResult onRightClick(BlockUseEvent e) {
        if (e.isClient()) return e.success();
        if (e.stack.getItem() instanceof Wrench)
            return e.pass();

        if (e.player.isServerPlayerEntity())
            EMCManager.syncS2C(e.player);

        Player player = e.player;
        player.openGuiScreen(new AlchemyTableScreenHandlerFactory());
        return e.consume();
    }


    @Override
    public VoxelShape getOutlineShape(OutlineShapeEvent e) {
        Direction dir = e.has(FACING) ? e.get(FACING) : Direction.DOWN;

        // DOWN
        if (dir.equals(Direction.UP))
            return VoxelShapeUtil.cuboid(0, 0.875, 0, 1, 1, 1);

        if (dir.equals(Direction.NORTH))
            return VoxelShapeUtil.cuboid(0, 0, 0, 1, 1, 0.125);

        if (dir.equals(Direction.SOUTH))
            return VoxelShapeUtil.cuboid(0, 0, 0.875, 1, 1, 1);

        if (dir.equals(Direction.WEST))
            VoxelShapeUtil.cuboid(0, 0, 0, 0.125, 1, 1);

        if (dir.equals(Direction.EAST))
            return VoxelShapeUtil.cuboid(0.875, 0, 0, 1, 1, 1);

        return VoxelShapeUtil.cuboid(0, 0, 0, 1, 0.125, 1);
    }

    @Override
    public void appendProperties(AppendPropertiesArgs args) {
        super.appendProperties(args);
        args.addProperty(FACING);
    }

    @Override
    public BlockState getPlacementState(PlacementStateArgs args) {
        BlockState state = super.getPlacementState(args);

        return state.with(FACING, args.getSide().getOpposite());
    }
}
