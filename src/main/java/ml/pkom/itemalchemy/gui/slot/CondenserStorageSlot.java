package ml.pkom.itemalchemy.gui.slot;

import ml.pkom.itemalchemy.tiles.EMCCondenserTile;
import ml.pkom.mcpitanlibarch.api.gui.slot.CompatibleSlot;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class CondenserStorageSlot extends CompatibleSlot {
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
