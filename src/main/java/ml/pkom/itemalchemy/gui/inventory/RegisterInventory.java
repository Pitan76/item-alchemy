package ml.pkom.itemalchemy.gui.inventory;

import java.util.ArrayList;
import java.util.List;
import ml.pkom.itemalchemy.EMCManager;
import ml.pkom.itemalchemy.data.PlayerState;
import ml.pkom.itemalchemy.data.ServerState;
import ml.pkom.itemalchemy.data.TeamState;
import ml.pkom.itemalchemy.gui.screen.AlchemyTableScreenHandler;
import ml.pkom.itemalchemy.item.ILearnableItem;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import ml.pkom.mcpitanlibarch.api.util.ItemUtil;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;

public class RegisterInventory extends SimpleInventory {
    public Player player;
    public RegisterInventory(int size, Player player) {
        super(size);
        this.player = player;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (!stack.isEmpty()) {
            boolean consumedItem = true;
            if(!player.getWorld().isClient) {
                ServerState state = ServerState.getServerState(player.getWorld().getServer());
                PlayerState playerState = state.getPlayer(player.getUUID()).get();

                TeamState teamState = state.getTeam(playerState.teamID).get();

                List<String> items = new ArrayList<>();
                if (stack.getItem() instanceof ILearnableItem) {
                    items.addAll(((ILearnableItem) stack.getItem()).onLearn(player));
                    consumedItem  = false;
                } else if (EMCManager.get(stack) != 0) {
                    items.add(ItemUtil.toID(stack.getItem()).toString());
                }

                for (String itemId : items) {
                    if (teamState.registeredItems.contains(itemId)) continue;
                    consumedItem = true;
                    teamState.registeredItems.add(itemId);
                }

                state.markDirty();
            }

            if (slot == 50) {
                if (!player.getWorld().isClient && EMCManager.get(stack) != 0) {
                    EMCManager.writeEmcToPlayer(player, stack);
                }
            } else {
                player.offerOrDrop(stack.copy());
            }

            if (player.getCurrentScreenHandler() instanceof AlchemyTableScreenHandler) {
                AlchemyTableScreenHandler screenHandler = (AlchemyTableScreenHandler) player.getCurrentScreenHandler();
                screenHandler.extractInventory.placeExtractSlots();
            }

            if (consumedItem) stack = ItemStack.EMPTY;
        }
        super.setStack(slot, stack);
    }
}
