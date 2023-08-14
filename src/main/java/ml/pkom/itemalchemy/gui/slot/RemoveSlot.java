package ml.pkom.itemalchemy.gui.slot;

import java.util.ArrayList;
import java.util.List;
import ml.pkom.itemalchemy.EMCManager;
import ml.pkom.itemalchemy.data.ModState;
import ml.pkom.itemalchemy.data.ServerState;
import ml.pkom.itemalchemy.data.TeamState;
import ml.pkom.itemalchemy.gui.screen.AlchemyTableScreenHandler;
import ml.pkom.itemalchemy.item.ILearnableItem;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import ml.pkom.mcpitanlibarch.api.gui.slot.CompatibleSlot;
import ml.pkom.mcpitanlibarch.api.util.ItemUtil;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import java.util.Optional;

public class RemoveSlot extends CompatibleSlot {
    public Player player;

    public RemoveSlot(Inventory inventory, int index, int x, int y, Player player) {
        super(inventory, index, x, y);
        this.player = player;
    }

    @Override
    public void callSetStack(ItemStack stack) {
        Optional<TeamState> teamState = ModState.getModState(player.getWorld().getServer()).getTeamByPlayer(player.getUUID());

        if(!teamState.isPresent()) {
            return;
        }

        List<String> items = new ArrayList<>();
        if (stack.getItem() instanceof ILearnableItem) {
            items.addAll(((ILearnableItem) stack.getItem()).onLearn(player));
        } else if (EMCManager.get(stack) != 0) {
            items.add(ItemUtil.toID(stack.getItem()).toString());
        }

        for (String itemId : items) {
            if (!teamState.get().registeredItems.contains(itemId)) continue;
            teamState.get().registeredItems.remove(itemId);

            if (!(player.getCurrentScreenHandler() instanceof AlchemyTableScreenHandler)) continue;
            AlchemyTableScreenHandler screenHandler = (AlchemyTableScreenHandler) player.getCurrentScreenHandler();
            screenHandler.extractInventory.placeExtractSlots();
        }

        if(!player.isClient()) {
            ServerState.getServerState(player.getWorld().getServer()).markDirty();
        }

        player.offerOrDrop(stack.copy());
        if (player.getCurrentScreenHandler() instanceof AlchemyTableScreenHandler) {
            AlchemyTableScreenHandler screenHandler = (AlchemyTableScreenHandler) player.getCurrentScreenHandler();
            screenHandler.extractInventory.placeExtractSlots();
        }
        super.callSetStack(ItemStack.EMPTY);
    }
}
