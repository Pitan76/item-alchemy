package ml.pkom.itemalchemy.block;

import ml.pkom.itemalchemy.EMCManager;
import ml.pkom.itemalchemy.gui.AlchemyTableScreenHandlerFactory;
import ml.pkom.mcpitanlibarch.api.block.CompatibleBlockSettings;
import ml.pkom.mcpitanlibarch.api.block.ExtendBlock;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import ml.pkom.mcpitanlibarch.api.event.block.BlockUseEvent;
import net.minecraft.block.*;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class AlchemyTable extends ExtendBlock {
    public AlchemyTable(AbstractBlock.Settings settings) {
        super(settings);
    }

    public AlchemyTable() {
        this(CompatibleBlockSettings.copy(Blocks.STONE).mapColor(MapColor.BLACK).strength(1.5f, 7.0f).build());
    }

    @Override
    public ActionResult onRightClick(BlockUseEvent e) {
        if (e.world.isClient()) {
            return ActionResult.SUCCESS;
        }
        if (e.player.getPlayerEntity() instanceof ServerPlayerEntity) {
            EMCManager.syncS2C((ServerPlayerEntity) e.player.getPlayerEntity());
        }
        Player player = e.player;
        player.openGuiScreen(new AlchemyTableScreenHandlerFactory());
        return ActionResult.CONSUME;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0, 0, 0, 1, 0.125, 1);
    }
}
