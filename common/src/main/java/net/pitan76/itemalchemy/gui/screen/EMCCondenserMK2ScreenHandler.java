package net.pitan76.itemalchemy.gui.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.itemalchemy.gui.slot.CondenserMK2InputSlot;
import net.pitan76.itemalchemy.gui.slot.CondenserMK2OutputSlot;
import net.pitan76.itemalchemy.tile.EMCCondenserTile;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.util.ItemStackUtil;
import net.pitan76.mcpitanlib.api.util.SlotUtil;
import net.pitan76.mcpitanlib.api.util.inventory.CompatInventory;
import net.pitan76.mcpitanlib.api.util.inventory.ICompatInventory;
import net.pitan76.mcpitanlib.midohra.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class EMCCondenserMK2ScreenHandler extends EMCCondenserScreenHandler {

    public EMCCondenserMK2ScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(ScreenHandlers.EMC_CONDENSER_MK2, syncId, playerInventory, new CompatInventory(85), buf);
    }

    public EMCCondenserMK2ScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ICompatInventory inventory, PacketByteBuf buf) {
        super(type, syncId, playerInventory, inventory, buf);
    }

    public EMCCondenserMK2ScreenHandler(int syncId, PlayerInventory playerInventory, @Nullable EMCCondenserTile tile, ICompatInventory inventory, ItemStack targetStack) {
        super(ScreenHandlers.EMC_CONDENSER_MK2, syncId, playerInventory, tile, inventory, targetStack);
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
    protected Slot addStorageSlot(ICompatInventory inventory, int index, int x, int y) {
        if (index >= 43)
            return this.callAddSlot(new CondenserMK2OutputSlot(inventory, index, x, y));

        return this.callAddSlot(new CondenserMK2InputSlot(inventory, index, x, y));
    }

    @Override
    public net.minecraft.item.ItemStack quickMoveOverride(Player player, int index) {
        Slot slot = callGetSlot(index);
        Slot targetSlot = callGetSlot(36);

        if (SlotUtil.hasStack(slot)) {
            ItemStack originalStack = ItemStack.of(SlotUtil.getStack(slot));

            // TargetSlot
            if(index == 36) {
                //増殖スロットが空だった場合
                if(targetStack.isEmpty()) {
                    if (EMCManager.get(originalStack.getItem()) == 0) {
                        SlotUtil.setStack(targetSlot, ItemStackUtil.empty());
                        return originalStack.toMinecraft();
                    }

                    ItemStack newStack = originalStack.getItem().createStack();
                    newStack.setCount(1);

                    SlotUtil.setStack(targetSlot, newStack.toMinecraft());
                    setTargetStack(newStack);

                    return ItemStackUtil.empty();
                }

                SlotUtil.setStack(targetSlot, ItemStackUtil.empty());
                setTargetStack(ItemStack.EMPTY);

                return ItemStackUtil.empty();
            }

            if (index < 36) {
                // TargetSlot
                if (targetStack.isEmpty()) {
                    if (EMCManager.get(originalStack.getItem()) != 0) {
                        ItemStack newStack = originalStack.getItem().createStack();
                        newStack.setCount(1);

                        SlotUtil.setStack(targetSlot, newStack.toMinecraft());
                        setTargetStack(newStack);
                    }

                    return ItemStackUtil.empty();
                }

                if (!this.callInsertItem(originalStack.toMinecraft(), 36 + 1, 36 + 43, false)) {
                    return ItemStackUtil.empty();
                }
            } else if (!this.callInsertItem(originalStack.toMinecraft(), 0, 36, true)) {
                return ItemStackUtil.empty();
            }

            if (originalStack.isEmpty()) {
                SlotUtil.setStack(slot, ItemStackUtil.empty());
            } else {
                SlotUtil.markDirty(slot);
            }
        }
        return ItemStackUtil.empty();
    }
}
