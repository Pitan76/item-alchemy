package net.pitan76.itemalchemy.gui.inventory;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.itemalchemy.data.PlayerState;
import net.pitan76.itemalchemy.data.ServerState;
import net.pitan76.itemalchemy.data.TeamState;
import net.pitan76.itemalchemy.gui.screen.AlchemyTableScreenHandler;
import net.pitan76.itemalchemy.item.ILearnableItem;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.util.ItemStackUtil;
import net.pitan76.mcpitanlib.api.util.ItemUtil;
import net.pitan76.mcpitanlib.api.util.PersistentStateUtil;
import net.pitan76.mcpitanlib.api.util.WorldUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RegisterInventory extends SimpleInventory {
    public Player player;
    public RegisterInventory(int size, Player player) {
        super(size);
        this.player = player;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (!ItemStackUtil.isEmpty(stack)) {
            boolean consumedItem = false,
                    learning = stack.getItem() instanceof ILearnableItem;

            if(!player.isClient()) {
                Optional<MinecraftServer> server = WorldUtil.getServer(player.getWorld());
                if (!server.isPresent()) return;

                ServerState state = ServerState.getServerState(server.get());
                PlayerState playerState = state.getPlayer(player.getUUID()).get();

                TeamState teamState = state.getTeam(playerState.teamID).get();

                List<String> items = new ArrayList<>();
                if (learning) {
                    items.addAll(((ILearnableItem) stack.getItem()).onLearn(player));
                } else if (EMCManager.get(stack) != 0) {
                    items.add(ItemUtil.toID(stack.getItem()).toString());
                }

                for (String itemId : items) {
                    if (teamState.registeredItems.contains(itemId)) continue;
                    consumedItem = true;
                    teamState.registeredItems.add(itemId);
                }

                PersistentStateUtil.markDirty(state);
            }

            if (slot == 50) {
                if (!player.isClient() && EMCManager.get(stack) != 0) {
                    EMCManager.writeEmcToPlayer(player, stack);
                }
            } else {
                player.offerOrDrop(stack.copy());
            }

            if (player.getCurrentScreenHandler() instanceof AlchemyTableScreenHandler) {
                AlchemyTableScreenHandler screenHandler = (AlchemyTableScreenHandler) player.getCurrentScreenHandler();
                screenHandler.extractInventory.placeExtractSlots();
            }

            if (consumedItem || !learning) stack = ItemStackUtil.empty();
        }
        super.setStack(slot, stack);
    }
}
