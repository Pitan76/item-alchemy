package net.pitan76.itemalchemy.gui.slot;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;
import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.itemalchemy.gui.screen.EMCCondenserScreenHandler;
import net.pitan76.mcpitanlib.api.gui.slot.CompatibleSlot;
import net.pitan76.mcpitanlib.api.util.ItemStackUtil;
import net.pitan76.mcpitanlib.midohra.item.ItemStack;

public class TargetSlot extends CompatibleSlot {

    private final ScreenHandler screenHandler;

    public TargetSlot(Inventory inventory, int index, int x, int y, ScreenHandler screenHandler) {
        super(inventory, index, x, y);
        this.screenHandler = screenHandler;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        if (screenHandler instanceof EMCCondenserScreenHandler) {
            EMCCondenserScreenHandler handler = (EMCCondenserScreenHandler) screenHandler;

            return EMCManager.get(stack) != 0 && !stack.isEmpty() && ItemStackUtil.isEmpty(handler.targetStack);
        }

        return EMCManager.get(stack) != 0 && !stack.isEmpty();
    }

    @Override
    public void callSetStack(net.minecraft.item.ItemStack stack) {
        if (EMCManager.get(stack) == 0) {
            super.callSetStack(ItemStackUtil.empty());
            return;
        }

        net.minecraft.item.ItemStack newStack = stack.copy();
        newStack.setCount(1);

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
