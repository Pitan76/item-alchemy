package net.pitan76.itemalchemy.gui.screen;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.pitan76.itemalchemy.gui.slot.FilterSlot;
import net.pitan76.itemalchemy.tile.EMCImporterTile;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.gui.ExtendedScreenHandler;
import net.pitan76.mcpitanlib.api.gui.args.CreateMenuEvent;
import net.pitan76.mcpitanlib.api.gui.args.SlotClickEvent;
import net.pitan76.mcpitanlib.api.util.*;
import net.pitan76.mcpitanlib.api.util.inventory.CompatInventory;
import net.pitan76.mcpitanlib.api.util.inventory.CompatPlayerInventory;
import net.pitan76.mcpitanlib.api.util.inventory.ICompatInventory;
import net.pitan76.mcpitanlib.midohra.network.PacketByteBuf;
import net.pitan76.mcpitanlib.midohra.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class EMCImporterScreenHandler extends ExtendedScreenHandler {
    public ICompatInventory inventory;
    public ICompatInventory filter;
    public CompatPlayerInventory playerInventory;
    public EMCImporterTile tile;
    public String ownerName = "";

    public EMCImporterScreenHandler(CreateMenuEvent e, PacketByteBuf buf) {
        this(e, null, new CompatInventory(1), new CompatInventory(9));

        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();

        tile = e.getWorldM().getBlockEntity(BlockPos.of(x, y, z)).getCompatBlockEntity(EMCImporterTile.class);

        if (buf.readBool()) {
            tile.teamUUID = buf.toCompat().readUuid(); // TODO: buf.readUuid()

            if (buf.readBool()) {
                ownerName = buf.readString();
            }
        }
    }

    public EMCImporterScreenHandler(CreateMenuEvent e, @Nullable EMCImporterTile tile, ICompatInventory inventory, ICompatInventory filter) {
        super(ScreenHandlers.EMC_IMPORTER, e.syncId);

        this.inventory = inventory;
        this.filter = filter;

        this.playerInventory = e.getCompatPlayerInventory();
        this.tile = tile;
        addPlayerMainInventorySlots(playerInventory, 8, 102);
        addPlayerHotbarSlots(playerInventory, 8, 160);
        
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                addFilterSlot(filter, j + i * 3, 62 + j * 18, 22 + i * 18);
            }
        }
    }

    protected Slot addFilterSlot(ICompatInventory inventory, int index, int x, int y) {
        Slot slot = new FilterSlot(inventory, index, x, y, this);
        return this.callAddSlot(slot);
    }

    @Override
    public ItemStack quickMoveOverride(Player player, int index) {
        Slot slot = ScreenHandlerUtil.getSlot(this, index);
        if (SlotUtil.hasStack(slot)) {
            ItemStack originalStack = SlotUtil.getStack(slot);
            // Filter Slot
            if (index >= 37 && index <= 46) {
                SlotUtil.setStack(slot, ItemStackUtil.empty());
                return ItemStackUtil.empty();
            }

            // Inventory
            if (index < 36) {
                for (int i = 37; i <= 46; i++) {
                    Slot targetSlot = ScreenHandlerUtil.getSlot(this, i);
                    if (SlotUtil.getStack(targetSlot).isEmpty()) {
                        ItemStack newTargetStack = originalStack.copy();
                        newTargetStack.setCount(1);
                        SlotUtil.setStack(targetSlot, newTargetStack);
                        return ItemStackUtil.empty();
                    }
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
    public void onSlotClick(SlotClickEvent e) {
        if (e.slot >= 37 && e.slot <= 46) { // Target Slot
            ItemStack oldStack = ItemStackUtil.copy(callGetCursorStack());
            super.onSlotClick(e);
            if (!ItemStackUtil.isEmpty(oldStack))
                callSetCursorStack(oldStack);

            return;
        }
        super.onSlotClick(e);
    }
}
