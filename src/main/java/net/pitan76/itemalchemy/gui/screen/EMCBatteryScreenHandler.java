package net.pitan76.itemalchemy.gui.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.pitan76.itemalchemy.tile.EMCBatteryTile;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.gui.ExtendedScreenHandler;
import net.pitan76.mcpitanlib.api.network.PacketByteUtil;
import net.pitan76.mcpitanlib.api.util.*;
import net.pitan76.mcpitanlib.api.util.math.PosUtil;
import org.jetbrains.annotations.Nullable;

public class EMCBatteryScreenHandler extends ExtendedScreenHandler {
    public Inventory inventory;
    public PlayerInventory playerInventory;
    public EMCBatteryTile tile = null;

    public long storedEMC = 0;
    public long maxEMC = 0;

    public EMCBatteryScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, null, InventoryUtil.createSimpleInventory(2));
        NbtCompound data = PacketByteUtil.readNbt(buf);
        if (data == null) return;
        int x, y, z;
        if (NbtUtil.has(data, "x") && NbtUtil.has(data, "y") && NbtUtil.has(data, "z")) {
            x = NbtUtil.getInt(data, "x");
            y = NbtUtil.getInt(data, "y");
            z = NbtUtil.getInt(data, "z");

            Player player = new Player(playerInventory.player);

            tile = (EMCBatteryTile) WorldUtil.getBlockEntity(player.getWorld(), PosUtil.flooredBlockPos(x, y, z));
            storedEMC = NbtUtil.getLong(data, "stored_emc") - tile.storedEMC;
            maxEMC = NbtUtil.getLong(data, "max_emc");
        }
    }

    public EMCBatteryScreenHandler(int syncId, PlayerInventory playerInventory, @Nullable EMCBatteryTile tile, Inventory inventory) {
        this(ScreenHandlers.EMC_BATTERY, syncId, playerInventory, tile, inventory);
    }

    public EMCBatteryScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, @Nullable EMCBatteryTile tile, Inventory inventory) {
        super(type, syncId);

        this.inventory = inventory;
        this.playerInventory = playerInventory;
        this.tile = tile;

        initSlots();
    }

    public void initSlots() {
        addPlayerMainInventorySlots(playerInventory, 24, 84);
        addPlayerHotbarSlots(playerInventory, 24, 142);
        addNormalSlot(inventory, 0, 24, 33);
        addNormalSlot(inventory, 1, 168, 33);
        //addSlots(inventory, 3, 14, 8, -1, 4, 4);
    }

    @Override
    public ItemStack quickMoveOverride(Player player, int index) {
        Slot slot = ScreenHandlerUtil.getSlot(this, index);
        if (SlotUtil.hasStack(slot)) {
            ItemStack originalStack = SlotUtil.getStack(slot);
            // TargetSlot
            if (index == 37) {
                Slot targetSlot = this.slots.get(37);
                SlotUtil.setStack(targetSlot, ItemStackUtil.empty());
                return ItemStackUtil.empty();
            }

            if (index < 36) {
                if (!this.callInsertItem(originalStack, 36 + 3, 36 + 16 + 3, false)) {
                    if (!this.callInsertItem(originalStack, 36, 36 + 3, false)) {
                        return ItemStackUtil.empty();
                    }
                }

                // TargetSlot
                Slot targetSlot = this.slots.get(37);
                if (SlotUtil.getStack(targetSlot).isEmpty()) {
                    ItemStack newTargetStack = originalStack.copy();
                    newTargetStack.setCount(1);
                    SlotUtil.setStack(targetSlot, newTargetStack);
                    return ItemStackUtil.empty();
                }
            } else if (!this.callInsertItem(originalStack, 0, 36, false)) {
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

    @Override
    public void overrideOnSlotClick(int slotIndex, int button, SlotActionType actionType, Player player) {
        if (slotIndex == 37) { // Target Slot
            ItemStack oldStack = getCursorStack().copy();
            super.overrideOnSlotClick(slotIndex, button, actionType, player);
            if (!ItemStackUtil.isEmpty(oldStack)) {
                callSetCursorStack(oldStack);
            }
            return;
        }
        super.overrideOnSlotClick(slotIndex, button, actionType, player);
    }
}
