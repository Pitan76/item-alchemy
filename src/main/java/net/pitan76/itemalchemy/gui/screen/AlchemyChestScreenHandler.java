package net.pitan76.itemalchemy.gui.screen;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.gui.SimpleScreenHandler;
import net.pitan76.mcpitanlib.api.gui.args.CreateMenuEvent;
import net.pitan76.mcpitanlib.api.util.ItemStackUtil;
import net.pitan76.mcpitanlib.api.util.ScreenHandlerUtil;
import net.pitan76.mcpitanlib.api.util.SlotUtil;
import net.pitan76.mcpitanlib.api.util.inventory.CompatInventory;
import net.pitan76.mcpitanlib.api.util.inventory.CompatPlayerInventory;
import net.pitan76.mcpitanlib.api.util.inventory.ICompatInventory;

public class AlchemyChestScreenHandler extends SimpleScreenHandler {
    public ICompatInventory inventory;
    public CompatPlayerInventory playerInventory;
    public AlchemyChestScreenHandler(CreateMenuEvent e) {
        this(e, new CompatInventory(104));
    }

    public AlchemyChestScreenHandler(CreateMenuEvent e, ICompatInventory inventory) {
        super(ScreenHandlers.ALCHEMY_CHEST, e);

        this.inventory = inventory;
        this.playerInventory = e.getCompatPlayerInventory();
        addPlayerMainInventorySlots(playerInventory.getRaw(), 48, 154);
        addPlayerHotbarSlots(playerInventory.getRaw(), 48, 212);
        addSlots(inventory, 0, 12, 8, -1, 13, 8);
    }

    @Override
    public ItemStack quickMoveOverride(Player player, int index) {
        ItemStack itemStack = ItemStackUtil.empty();
        Slot slot = ScreenHandlerUtil.getSlot(this, index);
        if (SlotUtil.hasStack(slot)) {
            ItemStack itemStack2 = SlotUtil.getStack(slot);
            itemStack = itemStack2.copy();
            if (index < 35 ? !callInsertItem(itemStack2, 36, 140, false) : !this.callInsertItem(itemStack2, 0, 35, true)) {
                return ItemStackUtil.empty();
            }
            if (itemStack2.isEmpty()) {
                SlotUtil.setStack(slot, ItemStackUtil.empty());
            } else {
                SlotUtil.markDirty(slot);
            }
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStackUtil.empty();
            }
            SlotUtil.onTakeItem(slot, player, itemStack2);
        }
        return itemStack;
    }
}
