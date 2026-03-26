package net.pitan76.itemalchemy.gui.slot;

import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.itemalchemy.gui.inventory.ExtractInventory;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.gui.slot.CompatibleSlot;
import net.pitan76.mcpitanlib.midohra.item.ItemStack;

public class ExtractSlot extends CompatibleSlot {

    public ExtractInventory inventory;
    public Player player;

    public ExtractSlot(ExtractInventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.inventory = inventory;
        this.player = inventory.player;
    }

    public boolean canTakeItem = true;

    @Override
    public boolean canTakeItems(Player player) {
        return EMCManager.getEmcFromPlayer(player) >= EMCManager.get(callGetStack().getItem()) && canTakeItem;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }
}
