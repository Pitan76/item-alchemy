package net.pitan76.itemalchemy.gui.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import net.pitan76.itemalchemy.gui.slot.TargetSlot;
import net.pitan76.itemalchemy.tile.EMCCollectorTile;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.gui.ExtendedScreenHandler;
import net.pitan76.mcpitanlib.api.network.PacketByteUtil;
import net.pitan76.mcpitanlib.api.util.InventoryUtil;
import net.pitan76.mcpitanlib.api.util.ItemStackUtil;
import net.pitan76.mcpitanlib.api.util.ScreenHandlerUtil;
import net.pitan76.mcpitanlib.api.util.SlotUtil;
import org.jetbrains.annotations.Nullable;

public class EMCCollectorScreenHandler extends ExtendedScreenHandler {
    public Inventory inventory;
    public PlayerInventory playerInventory;
    public EMCCollectorTile tile = null;

    public long storedEMC = 0;
    public long maxEMC = 0;

    public EMCCollectorScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, null, InventoryUtil.createSimpleInventory(16 + 3));
        NbtCompound data = PacketByteUtil.readNbt(buf);
        if (data == null) return;
        int x, y, z;
        if (data.contains("x") && data.contains("y") && data.contains("z")) {
            x = data.getInt("x");
            y = data.getInt("y");
            z = data.getInt("z");

            tile = (EMCCollectorTile) new Player(playerInventory.player).getWorld().getBlockEntity(new BlockPos(x, y, z));
            storedEMC = data.getInt("stored_emc") - tile.storedEMC;
            maxEMC = data.getInt("max_emc");
        }
    }

    public EMCCollectorScreenHandler(int syncId, PlayerInventory playerInventory, @Nullable EMCCollectorTile tile, Inventory inventory) {
        super(ScreenHandlers.EMC_COLLECTOR, syncId);

        this.inventory = inventory;
        this.playerInventory = playerInventory;
        this.tile = tile;
        addPlayerMainInventorySlots(playerInventory, 24, 84);
        addPlayerHotbarSlots(playerInventory, 24, 142);
        addNormalSlot(inventory, 0, 149, 12);
        addTargetSlot(inventory, 1, 177, 35);
        addNormalSlot(inventory, 2, 149, 58);
        addSlots(inventory, 3, 14, 8, -1, 4, 4);
    }

    protected Slot addTargetSlot(Inventory inventory, int index, int x, int y) {
        Slot slot = new TargetSlot(inventory, index, x, y, this);
        return this.callAddSlot(slot);
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
                    newTargetStack.setCount(37);
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
                setCursorStack(oldStack);
            }
            return;
        }
        super.overrideOnSlotClick(slotIndex, button, actionType, player);
    }
}
