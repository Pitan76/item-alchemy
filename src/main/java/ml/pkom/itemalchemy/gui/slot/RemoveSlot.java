package ml.pkom.itemalchemy.gui.slot;

import ml.pkom.itemalchemy.EMCManager;
import ml.pkom.itemalchemy.data.ServerState;
import ml.pkom.itemalchemy.data.TeamState;
import ml.pkom.itemalchemy.gui.screen.AlchemyTableScreenHandler;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import ml.pkom.mcpitanlibarch.api.gui.slot.CompatibleSlot;
import ml.pkom.mcpitanlibarch.api.nbt.NbtTag;
import ml.pkom.mcpitanlibarch.api.util.ItemUtil;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.util.Optional;

public class RemoveSlot extends CompatibleSlot {
    public Player player;

    public RemoveSlot(Inventory inventory, int index, int x, int y, Player player) {
        super(inventory, index, x, y);
        this.player = player;
    }

    @Override
    public void callSetStack(ItemStack stack) {
        Optional<TeamState> teamState = EMCManager.getModState(player.getWorld().getServer()).getTeamByPlayer(player.getUUID());

        if(!teamState.isPresent()) {
            return;
        }

        if (teamState.get().registeredItems.contains(ItemUtil.toID(stack.getItem()).toString())) {
            teamState.get().registeredItems.remove(ItemUtil.toID(stack.getItem()).toString());
            if (player.getCurrentScreenHandler() instanceof AlchemyTableScreenHandler) {
                AlchemyTableScreenHandler screenHandler = (AlchemyTableScreenHandler) player.getCurrentScreenHandler();
                screenHandler.extractInventory.placeExtractSlots();
            }
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
