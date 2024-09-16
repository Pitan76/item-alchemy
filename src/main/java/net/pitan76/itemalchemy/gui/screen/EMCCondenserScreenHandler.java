package net.pitan76.itemalchemy.gui.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.itemalchemy.gui.slot.CondenserStorageSlot;
import net.pitan76.itemalchemy.gui.slot.TargetSlot;
import net.pitan76.itemalchemy.tile.EMCCondenserTile;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.gui.ExtendedScreenHandler;
import net.pitan76.mcpitanlib.api.network.PacketByteUtil;
import net.pitan76.mcpitanlib.api.util.*;
import net.pitan76.mcpitanlib.api.util.math.PosUtil;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EMCCondenserScreenHandler extends ExtendedScreenHandler {
    public Inventory inventory;
    public PlayerInventory playerInventory;
    public EMCCondenserTile tile = null;

    public long storedEMC = 0;
    public long maxEMC = 0;
    public ItemStack targetStack = ItemStackUtil.empty();

    public EMCCondenserScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, null, InventoryUtil.createSimpleInventory(92), ItemStackUtil.empty());
        NbtCompound data = PacketByteUtil.readNbt(buf);
        if (data == null) return;
        int x, y, z;
        if (NbtUtil.has(data, "x") && NbtUtil.has(data, "y") && NbtUtil.has(data, "z")) {
            x = NbtUtil.getInt(data, "x");
            y = NbtUtil.getInt(data, "y");
            z = NbtUtil.getInt(data, "z");

            Player player = new Player(playerInventory.player);

            tile = (EMCCondenserTile) WorldUtil.getBlockEntity(player.getWorld(), PosUtil.flooredBlockPos(x, y, z));
            storedEMC = NbtUtil.getLong(data, "stored_emc") - tile.storedEMC;
            maxEMC = NbtUtil.getLong(data, "max_emc");

            targetStack = ItemStackUtil.fromNbt(player.getWorld(), NbtUtil.get(data, "target_item"));
        }
    }

    public EMCCondenserScreenHandler(int syncId, PlayerInventory playerInventory, @Nullable EMCCondenserTile tile, Inventory inventory, ItemStack targetStack) {
        super(ScreenHandlers.EMC_CONDENSER, syncId);

        this.inventory = inventory;
        this.playerInventory = playerInventory;
        this.tile = tile;
        this.targetStack = targetStack;
        addPlayerMainInventorySlots(playerInventory, 48, 154);
        addPlayerHotbarSlots(playerInventory, 48, 212);
        addTargetSlot(inventory, 0, 12, 6);
        addStorageSlots(inventory, 1, 12, 26, -1, 13, 7);
    }

    protected Slot addTargetSlot(Inventory inventory, int index, int x, int y) {
        Slot slot = new TargetSlot(inventory, index, x, y, this);

        return this.callAddSlot(slot);
    }

    protected Slot addStorageSlot(Inventory inventory, int index, int x, int y) {
        Slot slot = new CondenserStorageSlot(inventory, index, x, y);

        return this.callAddSlot(slot);
    }

    protected List<Slot> addStorageSlots(Inventory inventory, int firstIndex, int firstX, int firstY, int size, int maxAmountX, int maxAmountY) {
        if (size < 0) size = DEFAULT_SLOT_SIZE;
        List<Slot> slots = new ArrayList<>();

        for (int y = 0; y < maxAmountY; ++y) {
            List<Slot> xSlots = this.addStorageSlotsX(inventory, firstIndex + (y * maxAmountX), firstX, firstY + (y * size), size, maxAmountX);
            slots.addAll(xSlots);
        }
        return slots;
    }

    protected List<Slot> addStorageSlotsX(Inventory inventory, int firstIndex, int firstX, int y, int size, int amount) {
        if (size < 0) size = DEFAULT_SLOT_SIZE;
        List<Slot> slots = new ArrayList<>();

        for (int x = 0; x < amount; ++x) {
            Slot slot = this.addStorageSlot(inventory, firstIndex + x, firstX + (x * size), y);
            slots.add(slot);
        }
        return slots;
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

                if (!this.callInsertItem(originalStack, 36 + 1, 36 + 92, false)) {
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

    @Override
    public void overrideOnSlotClick(int slotIndex, int button, SlotActionType actionType, Player player) {
        Slot targetSlot = callGetSlot(36);

        if (slotIndex == 36) { // Target Slot
            // カーソルでアイテムを持ってない場合
            if (getCursorStack().isEmpty()) {
                setTargetStack(ItemStackUtil.empty());
                SlotUtil.setStack(targetSlot, ItemStackUtil.empty());

                return;
            }

            ItemStack oldStack = ItemStackUtil.create(getCursorStack().getItem());

            if(EMCManager.get(oldStack.getItem()) == 0) {
                callSetCursorStack(getCursorStack());

                super.overrideOnSlotClick(slotIndex, button, actionType, player);

                return;
            }

            setTargetStack(oldStack);
            SlotUtil.setStack(targetSlot, oldStack);

            callSetCursorStack(getCursorStack());

            return;
        }

        super.overrideOnSlotClick(slotIndex, button, actionType, player);
    }

    private void setTargetStack(ItemStack stack) {
        targetStack = stack;
        tile.setTargetStack(stack);
    }
}
