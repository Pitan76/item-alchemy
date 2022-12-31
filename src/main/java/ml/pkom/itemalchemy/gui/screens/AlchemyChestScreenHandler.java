package ml.pkom.itemalchemy.gui.screens;

import ml.pkom.itemalchemy.ScreenHandlers;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import ml.pkom.mcpitanlibarch.api.gui.SimpleScreenHandler;
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
        addSlots(inventory, 1, 12, 8, -1, 13, 8);
    }

    @Override
    public ItemStack quickMoveOverride(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (index < 9 ? !callInsertItem(itemStack2, 9, 45, true) : !this.callInsertItem(itemStack2, 0, 9, false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
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
