package ml.pkom.itemalchemy.gui.slot;

import ml.pkom.itemalchemy.EMCManager;
import ml.pkom.itemalchemy.gui.inventory.RegisterInventory;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import ml.pkom.mcpitanlibarch.api.gui.slot.CompatibleSlot;
import net.minecraft.item.ItemStack;

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
        return EMCManager.get(stack) != 0;
    }
}
