package ml.pkom.itemalchemy.gui.inventory;

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
            if(!player.getWorld().isClient) {
                ServerState state = ServerState.getServerState(player.getWorld().getServer());
                PlayerState playerState = state.getPlayer(player.getUUID()).get();

                TeamState teamState = state.getTeam(playerState.teamID).get();

                if(EMCManager.get(stack) != 0 && !teamState.registeredItems.contains(ItemUtil.toID(stack.getItem()).toString())) {
                    teamState.registeredItems.add(ItemUtil.toID(stack.getItem()).toString());
                }

                if (stack.getItem() instanceof ILearnableItem) {
                    ((ILearnableItem) stack.getItem()).onLearn(player);
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

            stack = ItemStack.EMPTY;
        }
        super.setStack(slot, stack);
    }
}
