package net.pitan76.itemalchemy.gui.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.itemalchemy.gui.slot.CondenserMK2InputSlot;
import net.pitan76.itemalchemy.gui.slot.CondenserMK2OutputSlot;
import net.pitan76.itemalchemy.tile.EMCCondenserTile;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.util.*;
import org.jetbrains.annotations.Nullable;

public class EMCCondenserMK2ScreenHandler extends EMCCondenserScreenHandler {

    public EMCCondenserMK2ScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        super(syncId, playerInventory, InventoryUtil.createSimpleInventory(85), buf);
    }

    public EMCCondenserMK2ScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PacketByteBuf buf) {
        super(syncId, playerInventory, inventory, buf);
    }

    public EMCCondenserMK2ScreenHandler(int syncId, PlayerInventory playerInventory, @Nullable EMCCondenserTile tile, Inventory inventory, ItemStack targetStack) {
        super(syncId, playerInventory, tile, inventory, targetStack);
    }

    @Override
    public void initSlots() {
        addPlayerMainInventorySlots(playerInventory, 48, 154);
        addPlayerHotbarSlots(playerInventory, 48, 212);
        addTargetSlot(inventory, 0, 12, 6);
        addStorageSlots(inventory, 1, 12, 26, -1, 6, 7);
        addStorageSlots(inventory, 43, 138, 26, -1, 6, 7);
    }

    @Override
    protected Slot addStorageSlot(Inventory inventory, int index, int x, int y) {
        if (index >= 43)
            return this.callAddSlot(new CondenserMK2OutputSlot(inventory, index, x, y));

        return this.callAddSlot(new CondenserMK2InputSlot(inventory, index, x, y));
    }

    @Override
    public ItemStack quickMoveOverride(Player player, int index) {
        Slot slot = callGetSlot(index);
        Slot targetSlot = callGetSlot(36);

        if (SlotUtil.hasStack(slot)) {
            ItemStack originalStack = SlotUtil.getStack(slot);

            // TargetSlot
            if(index == 36) {
                //増殖スロットが空だった場合
                if(ItemStackUtil.isEmpty(targetStack)) {
                    if (EMCManager.get(originalStack.getItem()) == 0) {
                        SlotUtil.setStack(targetSlot, ItemStackUtil.empty());
                        return originalStack;
                    }

                    ItemStack newStack = ItemStackUtil.create(originalStack.getItem());
                    newStack.setCount(1);

                    SlotUtil.setStack(targetSlot, newStack);
                    setTargetStack(newStack);

                    return ItemStackUtil.empty();
                }

                SlotUtil.setStack(targetSlot, ItemStackUtil.empty());
                setTargetStack(ItemStackUtil.empty());

                return ItemStackUtil.empty();
            }

            if (index < 36) {
                // TargetSlot
                if (ItemStackUtil.isEmpty(targetStack)) {
                    if (EMCManager.get(originalStack.getItem()) != 0) {
                        ItemStack newStack = ItemStackUtil.create(originalStack.getItem());
                        newStack.setCount(1);

                        SlotUtil.setStack(targetSlot, newStack);
                        setTargetStack(newStack);
                    }

                    return ItemStackUtil.empty();
                }

                if (!this.callInsertItem(originalStack, 36 + 1, 36 + 43, false)) {
                    return ItemStackUtil.empty();
                }
            } else if (!this.callInsertItem(originalStack, 0, 36, true)) {
                return ItemStackUtil.empty();
            }

            if (ItemStackUtil.isEmpty(originalStack)) {
                SlotUtil.setStack(slot, ItemStackUtil.empty());
            } else {
                SlotUtil.markDirty(slot);
            }
        }
        return ItemStackUtil.empty();
    }
}
