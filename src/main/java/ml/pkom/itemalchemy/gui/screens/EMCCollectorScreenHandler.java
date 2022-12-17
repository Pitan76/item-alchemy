package ml.pkom.itemalchemy.gui.screens;

import ml.pkom.itemalchemy.ScreenHandlers;
import ml.pkom.itemalchemy.gui.inventory.TargetSlot;
import ml.pkom.itemalchemy.tiles.EMCCollectorTile;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import ml.pkom.mcpitanlibarch.api.gui.ExtendedScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class EMCCollectorScreenHandler extends ExtendedScreenHandler {
    public Inventory inventory;
    public PlayerInventory playerInventory;
    public EMCCollectorTile tile = null;

    public long storedEMC = 0;
    public long maxEMC = 0;

    public EMCCollectorScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, null, new SimpleInventory(16 + 3));
        NbtCompound data = buf.readNbt();
        if (data == null) return;
        int x, y, z;
        if (data.contains("x") && data.contains("y") && data.contains("z")) {
            x = data.getInt("x");
            y = data.getInt("y");
            z = data.getInt("z");

            tile = (EMCCollectorTile) playerInventory.player.world.getBlockEntity(new BlockPos(x, y, z));
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
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            // TargetSlot
            if (index == 37) {
                Slot targetSlot = this.slots.get(37);
                targetSlot.setStack(ItemStack.EMPTY);
                return ItemStack.EMPTY;
            }

            if (index < 36) {
                if (!this.callInsertItem(originalStack, 36 + 3, 36 + 16 + 3, false)) {
                    if (!this.callInsertItem(originalStack, 36, 36 + 3, false)) {
                        return ItemStack.EMPTY;
                    }
                }

                // TargetSlot
                Slot targetSlot = this.slots.get(37);
                if (targetSlot.getStack().isEmpty()) {
                    ItemStack newTargetStack = originalStack.copy();
                    newTargetStack.setCount(37);
                    targetSlot.setStack(newTargetStack);
                    return ItemStack.EMPTY;
                }
            } else if (!this.callInsertItem(originalStack, 0, 36, false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (slotIndex == 37) { // Target Slot
            ItemStack oldStack = getCursorStack().copy();
            super.onSlotClick(slotIndex, button, actionType, player);
            if (!oldStack.isEmpty()) {
                setCursorStack(oldStack);
            }
            return;
        }
        super.onSlotClick(slotIndex, button, actionType, player);
    }
}
