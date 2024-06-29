package net.pitan76.itemalchemy.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.pitan76.itemalchemy.tile.EMCCollectorTile;
import net.pitan76.mcpitanlib.api.block.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.api.block.ExtendBlock;
import net.pitan76.mcpitanlib.api.block.ExtendBlockEntityProvider;
import net.pitan76.mcpitanlib.api.event.block.AppendPropertiesArgs;
import net.pitan76.mcpitanlib.api.event.block.BlockUseEvent;
import net.pitan76.mcpitanlib.api.event.block.StateReplacedEvent;
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent;
import net.pitan76.mcpitanlib.api.util.BlockStateUtil;
import net.pitan76.mcpitanlib.api.util.TextUtil;
import org.jetbrains.annotations.Nullable;

public class EMCCollector extends ExtendBlock implements ExtendBlockEntityProvider {
    public static DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    private static final Text TITLE = TextUtil.translatable("container.itemalchemy.emc_collector");

    public long maxEMC;

    public EMCCollector(CompatibleBlockSettings settings, long maxEMC) {
        super(settings);
        setDefaultState(BlockStateUtil.getDefaultState(this).with(FACING, Direction.NORTH));
        this.maxEMC = maxEMC;
    }

    @Override
    public void appendProperties(AppendPropertiesArgs args) {
        args.addProperty(FACING);
        super.appendProperties(args);
    }

    @Override
    public void onStateReplaced(StateReplacedEvent e) {
        World world = e.world;
        BlockPos pos = e.pos;
        if (e.state.isOf(e.newState.getBlock()))
            return;

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof Inventory) {
            Inventory inventory = (Inventory) blockEntity;
            inventory.setStack(1, ItemStack.EMPTY);
            ItemScatterer.spawn(world, pos, inventory);
            world.updateComparators(pos, this);
        }
        super.onStateReplaced(e);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getPlayer().getHorizontalFacing().getOpposite());
    }

    public EMCCollector() {
        this(10000);
    }

    public EMCCollector(long maxEMC) {
        this(CompatibleBlockSettings.copy(Blocks.STONE).mapColor(MapColor.YELLOW).strength(2f, 7.0f), maxEMC);
    }

    @Override
    public ActionResult onRightClick(BlockUseEvent e) {
        if (e.world.isClient()) {
            return ActionResult.SUCCESS;
        }

        BlockEntity blockEntity = e.world.getBlockEntity(e.pos);
        if (blockEntity instanceof EMCCollectorTile) {
            EMCCollectorTile tile = (EMCCollectorTile)blockEntity;
            e.player.openExtendedMenu(tile);
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
        return new EMCCollectorTile(event);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return ((world1, pos, state1, blockEntity) -> {
            if (blockEntity instanceof EMCCollectorTile) {
                EMCCollectorTile emcCollectorTile = (EMCCollectorTile) blockEntity;
                emcCollectorTile.tick(world1, pos, state1, emcCollectorTile);
            }
        });
    }
}
