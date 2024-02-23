package net.pitan76.itemalchemy.item;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
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
        if (e.world.isClient()) {
            return TypedActionResult.consume(e.user.getPlayerEntity().getStackInHand(e.hand));
        }
        if (e.user.getPlayerEntity() instanceof ServerPlayerEntity) {
            EMCManager.syncS2C((ServerPlayerEntity) e.user.getPlayerEntity());
        }
        Player player = e.user;
        player.openGuiScreen(new AlchemyTableScreenHandlerFactory());
        return TypedActionResult.consume(e.user.getPlayerEntity().getStackInHand(e.hand));
    }
}
