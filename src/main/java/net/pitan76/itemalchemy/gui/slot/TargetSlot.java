package net.pitan76.itemalchemy.gui.slot;

import net.minecraft.screen.ScreenHandler;
import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.mcpitanlib.api.gui.slot.CompatibleSlot;
import net.pitan76.mcpitanlib.api.util.ItemStackUtil;
import net.pitan76.mcpitanlib.api.util.inventory.ICompatInventory;
import net.pitan76.mcpitanlib.midohra.item.ItemStack;

public class TargetSlot extends CompatibleSlot {

    private final ScreenHandler screenHandler;

    public TargetSlot(ICompatInventory inventory, int index, int x, int y, ScreenHandler screenHandler) {
        super(inventory, index, x, y);
        this.screenHandler = screenHandler;
    }

    /**
     * A target slot only ever displays a copy of the item it is filtering on, so vanilla must
     * never move anything into it. {@code safeInsert}, {@code insertItem} and the quick craft
     * (drag) handler all hand over as much of a stack as the slot claims to accept, and
     * {@link #callSetStack} truncating that down to one silently destroys the remainder.
     * <p>
     * The screen handlers set the displayed item themselves instead, so refusing every
     * insertion here costs nothing and closes every path at once — including the ones that
     * bypass {@code onSlotClick}, such as the single slot shortcut in the drag handler.
     */
    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }

    @Override
    public void callSetStack(net.minecraft.item.ItemStack stack) {
        if (EMCManager.get(stack) == 0) {
            super.callSetStack(ItemStackUtil.empty());
            return;
        }

        net.minecraft.item.ItemStack newStack = stack.copy();
        ItemStackUtil.setCount(newStack, 1);

        super.callSetStack(newStack);
    }

    @Override
    public net.minecraft.item.ItemStack callTakeStack(int amount) {
        callSetStack(ItemStackUtil.empty());
        return ItemStackUtil.empty();
    }

    public ScreenHandler getScreenHandler() {
        return screenHandler;
    }
}
