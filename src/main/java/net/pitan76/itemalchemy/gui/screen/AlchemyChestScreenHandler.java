package net.pitan76.itemalchemy.gui.screen;

import ml.pkom.mcpitanlibarch.api.entity.Player;
import ml.pkom.mcpitanlibarch.api.gui.SimpleScreenHandler;
import ml.pkom.mcpitanlibarch.api.util.SlotUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class AlchemyChestScreenHandler extends SimpleScreenHandler {
    public Inventory inventory;
    public PlayerInventory playerInventory;
    public AlchemyChestScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(104));
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
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasStack()) {
            ItemStack itemStack2 = SlotUtil.getStack(slot);
            itemStack = itemStack2.copy();
            if (index < 35 ? !callInsertItem(itemStack2, 36, 140, false) : !this.callInsertItem(itemStack2, 0, 35, true)) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                SlotUtil.setStack(slot, ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTakeItem(player.getPlayerEntity(), itemStack2);
        }
        return itemStack;
    }
}
