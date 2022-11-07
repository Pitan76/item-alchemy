package ml.pkom.itemalchemy.blocks;

import ml.pkom.itemalchemy.screens.AlchemyTableScreenHandler;
import ml.pkom.mcpitanlibarch.api.block.ExtendBlock;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import ml.pkom.mcpitanlibarch.api.event.block.BlockUseEvent;
import ml.pkom.mcpitanlibarch.api.event.block.ScreenHandlerCreateEvent;
import ml.pkom.mcpitanlibarch.api.util.TextUtil;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.Nullable;

public class EMCCollector extends ExtendBlock {

    private static final Text TITLE = TextUtil.translatable("container.itemalchemy.emc_collector");

    public EMCCollector(Settings settings) {
        super(settings);
    }
    public EMCCollector() {
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
