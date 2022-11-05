package ml.pkom.itemalchemy.blocks;

import ml.pkom.itemalchemy.EMCManager;
import ml.pkom.itemalchemy.screens.AlchemyTableScreenHandler;
import ml.pkom.mcpitanlibarch.api.block.ExtendBlock;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import ml.pkom.mcpitanlibarch.api.event.block.BlockUseEvent;
import ml.pkom.mcpitanlibarch.api.event.block.ScreenHandlerCreateEvent;
import ml.pkom.mcpitanlibarch.api.util.TextUtil;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class AlchemyTable extends ExtendBlock {
    private static final Text TITLE = TextUtil.translatable("container.itemalchemy.alchemy_table");

    public AlchemyTable(AbstractBlock.Settings settings) {
        super(settings);
    }

    public AlchemyTable() {
        this(FabricBlockSettings.of(Material.STONE, MapColor.BLACK));
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
        player.openGuiScreen(e.world, e.state, e.pos);
        return ActionResult.CONSUME;
    }

    @Override
    public @Nullable ScreenHandler createScreenHandler(ScreenHandlerCreateEvent e) {
        return new AlchemyTableScreenHandler(e.syncId, e.inventory);
    }

    @Nullable
    @Override
    public Text getScreenTitle() {
        return TITLE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0, 0, 0, 1, 0.125, 1);
    }
}
