package ml.pkom.itemalchemy.gui.inventory;

import ml.pkom.itemalchemy.EMCManager;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class RegisterSlot extends Slot {

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
