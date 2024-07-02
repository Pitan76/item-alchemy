package net.pitan76.itemalchemy.block;

import net.minecraft.block.Blocks;
import net.minecraft.block.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.itemalchemy.gui.AlchemyTableScreenHandlerFactory;
import net.pitan76.mcpitanlib.api.block.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.api.block.ExtendBlock;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.event.block.BlockUseEvent;
import net.pitan76.mcpitanlib.api.event.block.OutlineShapeEvent;

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
    public VoxelShape getOutlineShape(OutlineShapeEvent e) {
        return VoxelShapes.cuboid(0, 0, 0, 1, 0.125, 1);
    }
}
