package net.pitan76.itemalchemy.gui;

import net.pitan76.itemalchemy.gui.screen.AlchemyTableScreenHandler;
import net.pitan76.mcpitanlib.api.event.container.factory.DisplayNameArgs;
import net.pitan76.mcpitanlib.api.gui.SimpleScreenHandler;
import net.pitan76.mcpitanlib.api.gui.args.CreateMenuEvent;
import net.pitan76.mcpitanlib.api.gui.v3.SimpleScreenHandlerFactory;
import net.pitan76.mcpitanlib.api.text.TextComponent;

public class AlchemyTableScreenHandlerFactory implements SimpleScreenHandlerFactory {
    private static final TextComponent TITLE = TextComponent.translatable("container.itemalchemy.alchemy_table");

    @Override
    public SimpleScreenHandler createMenu(CreateMenuEvent e) {
        return new AlchemyTableScreenHandler(e);
    }

    @Override
    public TextComponent getDisplayText(DisplayNameArgs args) {
        return TITLE;
    }
}
