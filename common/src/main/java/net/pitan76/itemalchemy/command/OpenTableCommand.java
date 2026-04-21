package net.pitan76.itemalchemy.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.pitan76.itemalchemy.gui.AlchemyTableScreenHandlerFactory;
import net.pitan76.mcpitanlib.api.command.CommandSettings;
import net.pitan76.mcpitanlib.api.command.LiteralCommand;
import net.pitan76.mcpitanlib.api.event.ServerCommandEvent;
import net.pitan76.mcpitanlib.api.util.TextUtil;

public class OpenTableCommand extends LiteralCommand {

    @Override
    public void init(CommandSettings settings) {
        settings.permissionLevel(2);
    }

    @Override
    public void execute(ServerCommandEvent e) {
        if (!e.isClient()) {
            try {
                e.getPlayer().openGuiScreen(new AlchemyTableScreenHandlerFactory());
            } catch (CommandSyntaxException ex) {
                e.sendFailure(TextUtil.literal("[ItemAlchemy] " + ex.getMessage()));
            }
        }
    }
}
