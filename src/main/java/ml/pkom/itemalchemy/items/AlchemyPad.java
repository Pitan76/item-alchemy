package ml.pkom.itemalchemy.items;

import ml.pkom.itemalchemy.EMCManager;
import ml.pkom.itemalchemy.gui.AlchemyTableScreenHandlerFactory;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import ml.pkom.mcpitanlibarch.api.event.item.ItemUseEvent;
import ml.pkom.mcpitanlibarch.api.item.CompatibleItemSettings;
import ml.pkom.mcpitanlibarch.api.item.ExtendItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.TypedActionResult;

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
