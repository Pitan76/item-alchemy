package net.pitan76.itemalchemy.gui.slot;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.itemalchemy.data.ModState;
import net.pitan76.itemalchemy.data.ServerState;
import net.pitan76.itemalchemy.data.TeamState;
import net.pitan76.itemalchemy.gui.screen.AlchemyTableScreenHandler;
import net.pitan76.itemalchemy.item.ILearnableItem;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.gui.slot.CompatibleSlot;
import net.pitan76.mcpitanlib.api.util.ItemStackUtil;
import net.pitan76.mcpitanlib.api.util.PersistentStateUtil;
import net.pitan76.mcpitanlib.api.util.WorldUtil;
import net.pitan76.mcpitanlib.api.util.item.ItemUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RemoveSlot extends CompatibleSlot {
    public Player player;

    public RemoveSlot(Inventory inventory, int index, int x, int y, Player player) {
        super(inventory, index, x, y);
        this.player = player;
    }

    @Override
    public void callSetStack(ItemStack stack) {
        Optional<MinecraftServer> serverOptional = WorldUtil.getServer(player.getWorld());
        if (!serverOptional.isPresent()) return;
        MinecraftServer server = serverOptional.get();
        
        Optional<TeamState> teamState = ModState.getModState(server).getTeamByPlayer(player.getUUID());
        if (!teamState.isPresent()) return;

        List<String> items = new ArrayList<>();
        if (stack.getItem() instanceof ILearnableItem) {
            items.addAll(((ILearnableItem) stack.getItem()).onLearn(player));
        } else if (EMCManager.get(stack) != 0) {
            items.add(ItemUtil.toId(stack.getItem()).toString());
        }

        for (String itemId : items) {
            if (!teamState.get().registeredItems.contains(itemId)) continue;
            teamState.get().registeredItems.remove(itemId);

            if (!(player.getCurrentScreenHandler() instanceof AlchemyTableScreenHandler)) continue;
            AlchemyTableScreenHandler screenHandler = (AlchemyTableScreenHandler) player.getCurrentScreenHandler();
            screenHandler.extractInventory.placeExtractSlots();
        }

        if (!player.isClient()) {
            PersistentStateUtil.markDirty(ServerState.getServerState(server));
        }

        player.offerOrDrop(ItemStackUtil.copy(stack));
        if (player.getCurrentScreenHandler() instanceof AlchemyTableScreenHandler) {
            AlchemyTableScreenHandler screenHandler = (AlchemyTableScreenHandler) player.getCurrentScreenHandler();
            screenHandler.extractInventory.placeExtractSlots();
        }
        super.callSetStack(ItemStackUtil.empty());
    }
}
