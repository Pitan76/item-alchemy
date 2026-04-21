package net.pitan76.itemalchemy.gui;

import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.pitan76.itemalchemy.gui.screen.AlchemyTableScreenHandler;
import net.pitan76.mcpitanlib.api.event.container.factory.DisplayNameArgs;
import net.pitan76.mcpitanlib.api.gui.args.CreateMenuEvent;
import net.pitan76.mcpitanlib.api.gui.v2.SimpleScreenHandlerFactory;
import net.pitan76.mcpitanlib.api.util.TextUtil;

public class AlchemyTableScreenHandlerFactory implements SimpleScreenHandlerFactory {
    private static final Text TITLE = TextUtil.translatable("container.itemalchemy.alchemy_table");

    @Override
    public ScreenHandler createMenu(CreateMenuEvent e) {
        return new AlchemyTableScreenHandler(e.syncId, e.playerInventory);
    }

    @Override
    public Text getDisplayName(DisplayNameArgs args) {
        return TITLE;
    }
}
