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
import net.pitan76.itemalchemy.item.KleinStar;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.util.ItemStackUtil;
import net.pitan76.mcpitanlib.api.util.WorldUtil;
import net.pitan76.mcpitanlib.api.util.item.ItemUtil;

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

            // Klein Star in circle slots (52-63): charge with EMC from player's pool
            if (slot >= 52 && slot <= 63 && stack.getItem() instanceof KleinStar) {
                if (!player.isClient()) {
                    KleinStar star = (KleinStar) stack.getItem();
                    long space = star.getMaxEmc() - KleinStar.getStoredEmc(stack);
                    long available = EMCManager.getEmcFromPlayer(player);
                    long toCharge = Math.min(space, available);
                    if (toCharge > 0) {
                        KleinStar.addEmc(stack, toCharge);
                        EMCManager.decrementEmc(player, toCharge);
                    }
                    if (player.isServerPlayerEntity())
                        EMCManager.syncS2C(player);
                    player.offerOrDrop(stack.copy());
                }

                super.setStack(slot, ItemStackUtil.empty());
                return;
            }

            boolean consumedItem = false,
                    learning = stack.getItem() instanceof ILearnableItem;

            if (!player.isClient()) {
                Optional<MinecraftServer> server = WorldUtil.getServer(player.getWorld());
                if (!server.isPresent()) return;

                ServerState state = ServerState.getServerState(server.get());
                PlayerState playerState = state.getPlayer(player.getUUID()).get();

                TeamState teamState = state.getTeam(playerState.teamID).get();

                List<String> items = new ArrayList<>();
                if (learning) {
                    items.addAll(((ILearnableItem) stack.getItem()).onLearn(player));
                } else if (EMCManager.get(stack) != 0) {
                    items.add(ItemUtil.toId(stack.getItem()).toString());
                }

                for (String itemId : items) {
                    if (teamState.registeredItems.contains(itemId)) continue;
                    consumedItem = true;
                    teamState.registeredItems.add(itemId);
                }

                state.callMarkDirty();
            }

            if (slot == 50) {
                if (!player.isClient() && EMCManager.get(stack) != 0) {
                    EMCManager.writeEmcToPlayer(player, stack);
                    if (player.isServerPlayerEntity())
                        EMCManager.syncS2C(player);
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
