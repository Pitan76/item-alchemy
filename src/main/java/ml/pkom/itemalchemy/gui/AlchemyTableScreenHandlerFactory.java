package ml.pkom.itemalchemy.gui;

import ml.pkom.itemalchemy.gui.screen.AlchemyTableScreenHandler;
import ml.pkom.mcpitanlibarch.api.util.TextUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

public class AlchemyTableScreenHandlerFactory implements NamedScreenHandlerFactory {
    private static final Text TITLE = TextUtil.translatable("container.itemalchemy.alchemy_table");

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new AlchemyTableScreenHandler(syncId, inv);
    }

    @Override
    public Text getDisplayName() {
        return TITLE;
    }
}
