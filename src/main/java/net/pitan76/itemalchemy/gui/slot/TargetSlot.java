package net.pitan76.itemalchemy.gui.slot;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.mcpitanlib.api.gui.slot.CompatibleSlot;

public class TargetSlot extends CompatibleSlot {

    private final ScreenHandler screenHandler;

    public TargetSlot(Inventory inventory, int index, int x, int y, ScreenHandler screenHandler) {
        super(inventory, index, x, y);
        this.screenHandler = screenHandler;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return EMCManager.get(stack) != 0 && !stack.isEmpty();
    }

    @Override
    public void callSetStack(ItemStack stack) {
        ItemStack newStack = stack.copy();
        newStack.setCount(1);
        super.callSetStack(newStack);
    }

    @Override
    public ItemStack callTakeStack(int amount) {
        callSetStack(ItemStack.EMPTY);
        return ItemStack.EMPTY;
    }

    public ScreenHandler getScreenHandler() {
        return screenHandler;
    }
}
