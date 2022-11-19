package ml.pkom.itemalchemy.gui.inventory;

import ml.pkom.itemalchemy.tiles.EMCCondenserTile;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class CondenserStorageSlot extends Slot {
    public CondenserStorageSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        if (inventory instanceof EMCCondenserTile) {
            EMCCondenserTile tile = (EMCCondenserTile) inventory;
            return tile.getTargetStack().isEmpty() || tile.getTargetStack().getItem() != stack.getItem();
        }
        return true;
    }
}
