package net.pitan76.itemalchemy.gui.slot;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.mcpitanlib.api.gui.slot.CompatibleSlot;
import net.pitan76.mcpitanlib.api.util.ItemStackUtil;

public class FilterSlot extends CompatibleSlot {

    public FilterSlot(Inventory inventory, int index, int x, int y, ScreenHandler screenHandler) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return EMCManager.get(stack) != 0 && !ItemStackUtil.isEmpty(stack);
    }

    @Override
    public void callSetStack(ItemStack stack) {
        if (EMCManager.get(stack) == 0) {
            super.callSetStack(ItemStackUtil.empty());
            return;
        }

        ItemStack newStack = stack.copy();
        newStack.setCount(1);

        super.callSetStack(newStack);
    }

    @Override
    public ItemStack callTakeStack(int amount) {
        callSetStack(ItemStackUtil.empty());
        return ItemStackUtil.empty();
    }
}
