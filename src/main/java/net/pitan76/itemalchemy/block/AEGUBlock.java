package net.pitan76.itemalchemy.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pitan76.itemalchemy.tile.AEGUTile;
import net.pitan76.itemalchemy.tile.EMCCondenserTile;
import net.pitan76.mcpitanlib.api.block.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.api.block.ExtendBlock;
import net.pitan76.mcpitanlib.api.block.ExtendBlockEntityProvider;
import net.pitan76.mcpitanlib.api.event.block.AppendPropertiesArgs;
import net.pitan76.mcpitanlib.api.event.block.BlockUseEvent;
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent;
import net.pitan76.mcpitanlib.api.util.BlockStateUtil;
import org.jetbrains.annotations.Nullable;

public class AEGUBlock extends ExtendBlock implements ExtendBlockEntityProvider {
    public static BooleanProperty CONNECTED = BooleanProperty.of("connected");
    public long emc;

    public AEGUBlock(CompatibleBlockSettings settings, long emc) {
        super(settings);
        setNewDefaultState(BlockStateUtil.getDefaultState(this).with(CONNECTED, false));
        this.emc = emc;
    }

    public AEGUBlock() {
        this(10000);
    }

    @Override
    public void appendProperties(AppendPropertiesArgs args) {
        args.addProperty(CONNECTED);
        super.appendProperties(args);
    }

    public AEGUBlock(long emc) {
        this(CompatibleBlockSettings.copy(net.minecraft.block.Blocks.STONE).mapColor(MapColor.YELLOW).strength(2f, 7.0f), emc);
    }

    public static BlockState setConnected(BlockState state, boolean isConnected) {
        return state.with(CONNECTED, isConnected);
    }

    public static boolean isConnected(BlockState state) {
        return state.get(CONNECTED);
    }

    @Override
    public ActionResult onRightClick(BlockUseEvent e) {
        BlockPos blockPos = AEGUTile.getNearEMCCondenserPos(e.world, e.pos);
        if (blockPos == null) return ActionResult.FAIL;
        BlockEntity blockEntity = e.world.getBlockEntity(blockPos);

        if (blockEntity instanceof EMCCondenserTile) {
            EMCCondenserTile tile = (EMCCondenserTile) blockEntity;
            if (e.world.isClient) return ActionResult.SUCCESS;
            e.player.openGuiScreen(tile);
            return ActionResult.CONSUME;
        }
        return ActionResult.PASS;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(TileCreateEvent event) {
        return new AEGUTile(event);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return ((world1, pos, state1, blockEntity) -> {
            if (blockEntity instanceof AEGUTile) {
                AEGUTile aeguTile = (AEGUTile) blockEntity;
                aeguTile.tick(world1, pos, state1, aeguTile);
            }
        });
    }
}
