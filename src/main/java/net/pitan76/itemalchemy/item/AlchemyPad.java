package net.pitan76.itemalchemy.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.TypedActionResult;
import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.itemalchemy.gui.AlchemyTableScreenHandlerFactory;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.event.item.ItemUseEvent;
import net.pitan76.mcpitanlib.api.item.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.item.ExtendItem;

public class AlchemyPad extends ExtendItem {

    public AlchemyPad(CompatibleItemSettings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> onRightClick(ItemUseEvent e) {
        if (e.isClient()) return TypedActionResult.consume(e.getStack());

        if (e.user.isServerPlayerEntity())
            EMCManager.syncS2C(e.user);

        Player player = e.user;
        player.openGuiScreen(new AlchemyTableScreenHandlerFactory());
        return TypedActionResult.consume(e.getStack());
    }
}
