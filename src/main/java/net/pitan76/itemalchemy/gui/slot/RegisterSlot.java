package net.pitan76.itemalchemy.gui.slot;

import net.minecraft.item.ItemStack;
import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.itemalchemy.gui.inventory.RegisterInventory;
import net.pitan76.itemalchemy.item.ILearnableItem;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.gui.slot.CompatibleSlot;

public class RegisterSlot extends CompatibleSlot {

    public RegisterInventory inventory;
    public Player player;

    public RegisterSlot(RegisterInventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.inventory = inventory;
        this.player = inventory.player;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return EMCManager.get(stack) != 0 || stack.getItem() instanceof ILearnableItem;
    }
}
