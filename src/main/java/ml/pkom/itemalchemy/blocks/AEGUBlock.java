package ml.pkom.itemalchemy.blocks;

import ml.pkom.itemalchemy.tiles.AEGUTile;
import ml.pkom.itemalchemy.tiles.EMCCondenserTile;
import ml.pkom.mcpitanlibarch.api.block.ExtendBlock;
import ml.pkom.mcpitanlibarch.api.block.ExtendBlockEntityProvider;
import ml.pkom.mcpitanlibarch.api.event.block.BlockUseEvent;
import ml.pkom.mcpitanlibarch.api.event.block.TileCreateEvent;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AEGUBlock extends ExtendBlock implements ExtendBlockEntityProvider {
    public long emc;

    public AEGUBlock(Settings settings, long emc) {
        super(settings);
        this.emc = emc;
    }

    public AEGUBlock() {
        this(10000);
    }

    public AEGUBlock(long emc) {
        this(Settings.of(Material.STONE, MapColor.YELLOW).strength(2f, 7.0f), emc);
    }

    @Override
    public ActionResult onRightClick(BlockUseEvent e) {
        if (e.world.isClient()) {
            return ActionResult.SUCCESS;
        }

        BlockPos blockPos = AEGUTile.getNearEMCCondenserPos(e.world, e.pos);
        if (blockPos == null) return ActionResult.FAIL;
        BlockEntity blockEntity = e.world.getBlockEntity(blockPos);

        if (blockEntity instanceof EMCCondenserTile) {
            EMCCondenserTile tile = (EMCCondenserTile) blockEntity;
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
