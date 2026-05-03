package net.pitan76.itemalchemy.gui.screen;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.pitan76.itemalchemy.gui.slot.TargetSlot;
import net.pitan76.itemalchemy.tile.EMCCollectorTile;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.gui.ExtendedScreenHandler;
import net.pitan76.mcpitanlib.api.gui.args.CreateMenuEvent;
import net.pitan76.mcpitanlib.api.gui.args.SlotClickEvent;
import net.pitan76.mcpitanlib.api.network.PacketByteUtil;
import net.pitan76.mcpitanlib.api.util.*;
import net.pitan76.mcpitanlib.api.util.inventory.CompatInventory;
import net.pitan76.mcpitanlib.api.util.inventory.CompatPlayerInventory;
import net.pitan76.mcpitanlib.api.util.inventory.ICompatInventory;
import net.pitan76.mcpitanlib.midohra.block.entity.BlockEntityWrapper;
import net.pitan76.mcpitanlib.midohra.item.ItemStack;
import net.pitan76.mcpitanlib.midohra.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class EMCCollectorScreenHandler extends ExtendedScreenHandler {
    public ICompatInventory inventory;
    public CompatPlayerInventory playerInventory;
    public EMCCollectorTile tile = null;

    public long storedEMC = 0;
    public long maxEMC = 0;

    public EMCCollectorScreenHandler(CreateMenuEvent e, PacketByteBuf buf) {
        this(e, null, new CompatInventory(16 + 3));

        BlockPos pos = PacketByteUtil.readBlockPosM(buf);
        BlockEntityWrapper blockEntity = e.getWorldM().getBlockEntity(pos);

        if (blockEntity.isPresent()) {
            tile = blockEntity.getCompatBlockEntity(EMCCollectorTile.class);
            storedEMC = PacketByteUtil.readLong(buf) - tile.storedEMC;
            maxEMC = PacketByteUtil.readLong(buf);
        }
    }

    public EMCCollectorScreenHandler(CreateMenuEvent e, @Nullable EMCCollectorTile tile, ICompatInventory inventory) {
        super(ScreenHandlers.EMC_COLLECTOR, e.syncId);

        this.inventory = inventory;
        this.playerInventory = e.getCompatPlayerInventory();
        this.tile = tile;
        addPlayerMainInventorySlots(playerInventory.getRaw(), 24, 84);
        addPlayerHotbarSlots(playerInventory.getRaw(), 24, 142);
        addNormalSlot(inventory, 0, 149, 12);
        addTargetSlot(inventory, 1, 177, 35);
        addNormalSlot(inventory, 2, 149, 58);
        addSlots(inventory, 3, 14, 8, -1, 4, 4);
    }

    protected Slot addTargetSlot(ICompatInventory inventory, int index, int x, int y) {
        Slot slot = new TargetSlot(inventory, index, x, y, this);
        return this.callAddSlot(slot);
    }

    @Override
    public net.minecraft.item.ItemStack quickMoveOverride(Player player, int index) {
        Slot slot = ScreenHandlerUtil.getSlot(this, index);
        if (SlotUtil.hasStack(slot)) {
            ItemStack originalStack = ItemStack.of(SlotUtil.getStack(slot));
            // TargetSlot
            if (index == 37) {
                Slot targetSlot = ScreenHandlerUtil.getSlot(this, 37);
                SlotUtil.setStack(targetSlot, ItemStackUtil.empty());
                return ItemStackUtil.empty();
            }

            if (index < 36) {
                if (!this.callInsertItem(originalStack.toMinecraft(), 36 + 3, 36 + 16 + 3, false)) {
                    if (!this.callInsertItem(originalStack.toMinecraft(), 36, 36 + 3, false)) {
                        return ItemStackUtil.empty();
                    }
                }

                // TargetSlot
                Slot targetSlot = ScreenHandlerUtil.getSlot(this, 37);
                if (SlotUtil.getStack(targetSlot).isEmpty()) {
                    ItemStack newTargetStack = originalStack.copy();
                    newTargetStack.setCount(1);
                    SlotUtil.setStack(targetSlot, newTargetStack.toMinecraft());
                    return ItemStackUtil.empty();
                }
            } else if (!this.callInsertItem(originalStack.toMinecraft(), 0, 36, false)) {
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
        if (e.slot == 37) { // Target Slot
            ItemStack oldStack = getCursorStackM().copy();
            super.onSlotClick(e);
            if (!oldStack.isEmpty()) {
                setCursorStack(oldStack);
            }
            return;
        }
        super.onSlotClick(e);
    }
}
