package net.pitan76.itemalchemy.gui.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.gui.SimpleScreenHandler;
import net.pitan76.mcpitanlib.api.util.InventoryUtil;
import net.pitan76.mcpitanlib.api.util.ItemStackUtil;
import net.pitan76.mcpitanlib.api.util.ScreenHandlerUtil;
import net.pitan76.mcpitanlib.api.util.SlotUtil;

public class AlchemyChestScreenHandler extends SimpleScreenHandler {
    public Inventory inventory;
    public PlayerInventory playerInventory;
    public AlchemyChestScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, InventoryUtil.createSimpleInventory(104));
    }

    public AlchemyChestScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(ScreenHandlers.ALCHEMY_CHEST, syncId);

        this.inventory = inventory;
        this.playerInventory = playerInventory;
        addPlayerMainInventorySlots(playerInventory, 48, 154);
        addPlayerHotbarSlots(playerInventory, 48, 212);
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
