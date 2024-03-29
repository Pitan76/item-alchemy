package net.pitan76.itemalchemy.gui.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
import net.pitan76.mcpitanlib.api.util.SlotUtil;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EMCCondenserScreenHandler extends ExtendedScreenHandler {
    public Inventory inventory;
    public PlayerInventory playerInventory;
    public EMCCondenserTile tile = null;

    public long storedEMC = 0;
    public long maxEMC = 0;

    public EMCCondenserScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, null, new SimpleInventory(92));
        NbtCompound data = PacketByteUtil.readNbt(buf);
        if (data == null) return;
        int x, y, z;
        if (data.contains("x") && data.contains("y") && data.contains("z")) {
            x = data.getInt("x");
            y = data.getInt("y");
            z = data.getInt("z");

            tile = (EMCCondenserTile) new Player(playerInventory.player).getWorld().getBlockEntity(new BlockPos(x, y, z));
            storedEMC = data.getInt("stored_emc") - tile.storedEMC;
            maxEMC = data.getInt("max_emc");
        }
    }

    public EMCCondenserScreenHandler(int syncId, PlayerInventory playerInventory, @Nullable EMCCondenserTile tile, Inventory inventory) {
        super(ScreenHandlers.EMC_CONDENSER, syncId);

        this.inventory = inventory;
        this.playerInventory = playerInventory;
        this.tile = tile;
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
        Slot slot = this.slots.get(index);
        if (slot.hasStack()) {
            ItemStack originalStack = SlotUtil.getStack(slot);
            // TargetSlot
            if (index == 36 && EMCManager.get(originalStack.getItem()) != 0) {
                Slot targetSlot = this.slots.get(36);
                SlotUtil.setStack(targetSlot, ItemStack.EMPTY);
                return ItemStack.EMPTY;
            }

            if (index < 36) {
                // TargetSlot
                Slot targetSlot = this.slots.get(36);
                if (SlotUtil.getStack(targetSlot).isEmpty() && EMCManager.get(originalStack.getItem()) != 0) {
                    ItemStack newTargetStack = originalStack.copy();
                    newTargetStack.setCount(1);
                    SlotUtil.setStack(targetSlot, newTargetStack);
                    return ItemStack.EMPTY;
                }


                if (!this.callInsertItem(originalStack, 36 + 1, 36 + 92, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.callInsertItem(originalStack, 0, 36, true)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                SlotUtil.setStack(slot, ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void overrideOnSlotClick(int slotIndex, int button, SlotActionType actionType, Player player) {
        if (slotIndex == 36) { // Target Slot
            ItemStack oldStack = getCursorStack().copy();

            // もともとスロットが空のとき、カーソルのアイテムも消えてしまうのでここで適当にセットしておく。
            callSetStackInSlot(slotIndex, callGetRevision(), new ItemStack(Items.STONE));

            super.overrideOnSlotClick(slotIndex, button, actionType, player);
            if (!oldStack.isEmpty()) {
                callSetCursorStack(oldStack);
            }
            return;
        }
        super.overrideOnSlotClick(slotIndex, button, actionType, player);
    }
}
