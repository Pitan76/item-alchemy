package ml.pkom.itemalchemy.gui.inventory;

import ml.pkom.itemalchemy.EMCManager;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class TargetSlot extends Slot {

    private ScreenHandler screenHandler;

    public TargetSlot(Inventory inventory, int index, int x, int y, ScreenHandler screenHandler) {
        super(inventory, index, x, y);
        this.screenHandler = screenHandler;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return EMCManager.get(stack) != 0 || stack.isEmpty();
    }

    @Override
    public void setStack(ItemStack stack) {
        ItemStack newStack = stack.copy();
        newStack.setCount(1);
        super.setStack(newStack);
    }

    @Override
    public ItemStack takeStack(int amount) {
        setStack(ItemStack.EMPTY);
        return ItemStack.EMPTY;
    }

    public ScreenHandler getScreenHandler() {
        return screenHandler;
    }
}
