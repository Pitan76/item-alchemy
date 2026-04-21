package net.pitan76.itemalchemy.gui.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.itemalchemy.gui.slot.CondenserStorageSlot;
import net.pitan76.itemalchemy.gui.slot.TargetSlot;
import net.pitan76.itemalchemy.tile.EMCCondenserTile;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.gui.ExtendedScreenHandler;
import net.pitan76.mcpitanlib.api.gui.args.SlotClickEvent;
import net.pitan76.mcpitanlib.api.network.PacketByteUtil;
import net.pitan76.mcpitanlib.api.util.*;
import net.pitan76.mcpitanlib.api.util.inventory.CompatInventory;
import net.pitan76.mcpitanlib.api.util.inventory.ICompatInventory;
import net.pitan76.mcpitanlib.api.util.math.PosUtil;
import net.pitan76.mcpitanlib.midohra.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EMCCondenserScreenHandler extends ExtendedScreenHandler {
    public ICompatInventory inventory;
    public PlayerInventory playerInventory;
    public EMCCondenserTile tile;

    public long storedEMC = 0;
    public long maxEMC = 0;
    public ItemStack targetStack;

    public EMCCondenserScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(ScreenHandlers.EMC_CONDENSER, syncId, playerInventory, new CompatInventory(92), buf);
    }

    public EMCCondenserScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ICompatInventory inventory, PacketByteBuf buf) {
        this(type, syncId, playerInventory, null, inventory, ItemStack.EMPTY);
        int x, y, z;
        x = PacketByteUtil.readInt(buf);
        y = PacketByteUtil.readInt(buf);
        z = PacketByteUtil.readInt(buf);

        Player player = new Player(playerInventory.player);

        tile = (EMCCondenserTile) WorldUtil.getBlockEntity(player.getWorld(), PosUtil.flooredBlockPos(x, y, z));
        storedEMC = PacketByteUtil.readLong(buf) - tile.storedEMC;
        maxEMC = PacketByteUtil.readLong(buf);

        targetStack = ItemStack.of(PacketByteUtil.readItemStack(buf));
    }

    public EMCCondenserScreenHandler(int syncId, PlayerInventory playerInventory, @Nullable EMCCondenserTile tile, ICompatInventory inventory, ItemStack targetStack) {
        this(ScreenHandlers.EMC_CONDENSER, syncId, playerInventory, tile, inventory, targetStack);
    }

    public EMCCondenserScreenHandler(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, @Nullable EMCCondenserTile tile, ICompatInventory inventory, ItemStack targetStack) {
        super(type, syncId);

        this.inventory = inventory;
        this.playerInventory = playerInventory;
        this.tile = tile;
        this.targetStack = targetStack;

        initSlots();
    }

    public void initSlots() {
        addPlayerMainInventorySlots(playerInventory, 48, 154);
        addPlayerHotbarSlots(playerInventory, 48, 212);
        addTargetSlot(inventory, 0, 12, 6);
        addStorageSlots(inventory, 1, 12, 26, -1, 13, 7);
    }

    protected Slot addTargetSlot(ICompatInventory inventory, int index, int x, int y) {
        Slot slot = new TargetSlot(inventory, index, x, y, this);

        return this.callAddSlot(slot);
    }

    protected Slot addStorageSlot(ICompatInventory inventory, int index, int x, int y) {
        Slot slot = new CondenserStorageSlot(inventory, index, x, y);

        return this.callAddSlot(slot);
    }

    protected List<Slot> addStorageSlots(ICompatInventory inventory, int firstIndex, int firstX, int firstY, int size, int maxAmountX, int maxAmountY) {
        if (size < 0) size = DEFAULT_SLOT_SIZE;
        List<Slot> slots = new ArrayList<>();

        for (int y = 0; y < maxAmountY; ++y) {
            List<Slot> xSlots = this.addStorageSlotsX(inventory, firstIndex + (y * maxAmountX), firstX, firstY + (y * size), size, maxAmountX);
            slots.addAll(xSlots);
        }
        return slots;
    }

    protected List<Slot> addStorageSlotsX(ICompatInventory inventory, int firstIndex, int firstX, int y, int size, int amount) {
        if (size < 0) size = DEFAULT_SLOT_SIZE;
        List<Slot> slots = new ArrayList<>();

        for (int x = 0; x < amount; ++x) {
            Slot slot = this.addStorageSlot(inventory, firstIndex + x, firstX + (x * size), y);
            slots.add(slot);
        }
        return slots;
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

                if (!this.callInsertItem(originalStack.toMinecraft(), 36 + 1, 36 + 92, false)) {
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

    @Override
    public void onSlotClick(SlotClickEvent e) {
        Slot targetSlot = callGetSlot(36);

        if (e.slot == 36) { // Target Slot
            // カーソルでアイテムを持ってない場合
            if (getCursorStackM().isEmpty()) {
                setTargetStack(ItemStack.EMPTY);
                SlotUtil.setStack(targetSlot, ItemStackUtil.empty());

                return;
            }

            ItemStack oldStack = getCursorStackM().getItem().createStack();

            if(EMCManager.get(oldStack.getItem()) == 0) {
                callSetCursorStack(callGetCursorStack());

                super.onSlotClick(e);

                return;
            }

            setTargetStack(oldStack);
            SlotUtil.setStack(targetSlot, oldStack.toMinecraft());

            callSetCursorStack(callGetCursorStack());

            return;
        }

        super.onSlotClick(e);
    }

    protected void setTargetStack(ItemStack stack) {
        targetStack = stack;
        tile.setTargetStack(stack);
    }
}
