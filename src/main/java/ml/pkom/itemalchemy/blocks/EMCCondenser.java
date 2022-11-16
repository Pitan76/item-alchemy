package ml.pkom.itemalchemy.blocks;

import ml.pkom.itemalchemy.gui.screens.AlchemyTableScreenHandler;
import ml.pkom.mcpitanlibarch.api.block.ExtendBlock;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import ml.pkom.mcpitanlibarch.api.event.block.BlockUseEvent;
import ml.pkom.mcpitanlibarch.api.event.block.ScreenHandlerCreateEvent;
import ml.pkom.mcpitanlibarch.api.util.TextUtil;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class EMCCondenser extends ExtendBlock {
    public static final DirectionProperty FACING = Properties.FACING;

    private static final Text TITLE = TextUtil.translatable("container.itemalchemy.emc_condenser");

    public EMCCondenser(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }
    public EMCCondenser() {
        this(FabricBlockSettings.of(Material.STONE, MapColor.YELLOW).strength(2f, 7.0f));
    }

    @Override
    public ActionResult onRightClick(BlockUseEvent e) {
        if (e.world.isClient()) {
            return ActionResult.SUCCESS;
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
}
