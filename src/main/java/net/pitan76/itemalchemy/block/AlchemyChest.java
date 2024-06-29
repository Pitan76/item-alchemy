package net.pitan76.itemalchemy.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.pitan76.itemalchemy.tile.AlchemyChestTile;
import net.pitan76.mcpitanlib.api.block.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.api.block.ExtendBlock;
import net.pitan76.mcpitanlib.api.block.ExtendBlockEntityProvider;
import net.pitan76.mcpitanlib.api.event.block.AppendPropertiesArgs;
import net.pitan76.mcpitanlib.api.event.block.BlockUseEvent;
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent;
import net.pitan76.mcpitanlib.api.util.BlockStateUtil;
import net.pitan76.mcpitanlib.api.util.TextUtil;
import org.jetbrains.annotations.Nullable;

public class AlchemyChest extends ExtendBlock implements ExtendBlockEntityProvider {
    public static DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    private static final Text TITLE = TextUtil.translatable("block.itemalchemy.alchemy_chest");

    public AlchemyChest(CompatibleBlockSettings settings) {
        super(settings);
        setDefaultState(BlockStateUtil.getDefaultState(this).with(FACING, Direction.NORTH));
    }

    @Override
    public void appendProperties(AppendPropertiesArgs args) {
        args.addProperty(FACING);
        super.appendProperties(args);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof Inventory) {
            Inventory inventory = (Inventory) blockEntity;
            ItemScatterer.spawn(world, pos, inventory);
            world.updateComparators(pos, this);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getPlayer().getHorizontalFacing().getOpposite());
    }

    public AlchemyChest() {
        this(CompatibleBlockSettings.copy(net.minecraft.block.Blocks.STONE).mapColor(MapColor.YELLOW).strength(2f, 7.0f));
    }

    @Override
    public ActionResult onRightClick(BlockUseEvent e) {
        if (e.world.isClient()) {
            return ActionResult.SUCCESS;
        }

        BlockEntity blockEntity = e.world.getBlockEntity(e.pos);
        if (blockEntity instanceof AlchemyChestTile) {
            AlchemyChestTile tile = (AlchemyChestTile)blockEntity;
            e.player.openGuiScreen(tile);
            return ActionResult.CONSUME;
        }
        return ActionResult.PASS;
    }

    @Nullable
    @Override
    public Text getScreenTitle() {
        return TITLE;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(TileCreateEvent event) {
        return new AlchemyChestTile(event);
    }
}
