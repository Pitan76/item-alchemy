package ml.pkom.itemalchemy.blocks;

import ml.pkom.itemalchemy.tiles.EMCCondenserTile;
import ml.pkom.mcpitanlibarch.api.block.ExtendBlock;
import ml.pkom.mcpitanlibarch.api.block.ExtendBlockEntityProvider;
import ml.pkom.mcpitanlibarch.api.event.block.BlockUseEvent;
import ml.pkom.mcpitanlibarch.api.event.block.TileCreateEvent;
import ml.pkom.mcpitanlibarch.api.util.TextUtil;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EMCCondenser extends ExtendBlock implements ExtendBlockEntityProvider {
    public static DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    private static final Text TITLE = TextUtil.translatable("container.itemalchemy.emc_condenser");

    public long maxEMC = 100000;

    public EMCCondenser(Settings settings) {
        super(settings);
        getStateManager().getDefaultState().with(FACING, Direction.NORTH);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        super.appendProperties(builder);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getPlayer().getHorizontalFacing().getOpposite());
    }

    public EMCCondenser() {
        this(FabricBlockSettings.of(Material.STONE, MapColor.BLACK).strength(2f, 7.0f));
    }

    @Override
    public ActionResult onRightClick(BlockUseEvent e) {
        if (e.world.isClient()) {
            return ActionResult.SUCCESS;
        }

        BlockEntity blockEntity = e.world.getBlockEntity(e.pos);
        if (blockEntity instanceof EMCCondenserTile) {
            EMCCondenserTile tile = (EMCCondenserTile)blockEntity;
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
        return new EMCCondenserTile(event);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return ((world1, pos, state1, blockEntity) -> {
            if (blockEntity instanceof EMCCondenserTile) {
                EMCCondenserTile EMCCondenserTile = (EMCCondenserTile) blockEntity;
                EMCCondenserTile.tick(world1, pos, state1, EMCCondenserTile);
            }
        });
    }
}
