package net.pitan76.itemalchemy.item;

import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.itemalchemy.gui.AlchemyTableScreenHandlerFactory;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.event.item.ItemUseEvent;
import net.pitan76.mcpitanlib.api.item.v2.CompatItem;
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.util.StackActionResult;

public class AlchemyPad extends CompatItem  {

    public AlchemyPad(CompatibleItemSettings settings) {
        super(settings);
    }

    @Override
    public StackActionResult onRightClick(ItemUseEvent e) {
        if (e.isClient()) return e.consume();

        if (e.user.isServerPlayerEntity())
            EMCManager.syncS2C(e.user);

        Player player = e.user;
        player.openGuiScreen(new AlchemyTableScreenHandlerFactory());
        return e.consume();
    }
}
