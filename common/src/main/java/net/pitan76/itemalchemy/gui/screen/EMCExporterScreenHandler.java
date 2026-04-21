package net.pitan76.itemalchemy.gui.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.pitan76.itemalchemy.gui.slot.FilterSlot;
import net.pitan76.itemalchemy.tile.EMCExporterTile;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.gui.ExtendedScreenHandler;
import net.pitan76.mcpitanlib.api.gui.args.SlotClickEvent;
import net.pitan76.mcpitanlib.api.network.PacketByteUtil;
import net.pitan76.mcpitanlib.api.util.*;
import net.pitan76.mcpitanlib.api.util.inventory.CompatInventory;
import net.pitan76.mcpitanlib.api.util.inventory.ICompatInventory;
import net.pitan76.mcpitanlib.midohra.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class EMCExporterScreenHandler extends ExtendedScreenHandler {
    public ICompatInventory filter;
    public PlayerInventory playerInventory;
    public EMCExporterTile tile = null;
    public String ownerName = "";

    public EMCExporterScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, null, new CompatInventory(9));

        int x = PacketByteUtil.readInt(buf);
        int y = PacketByteUtil.readInt(buf);
        int z = PacketByteUtil.readInt(buf);

        Player player = new Player(playerInventory.player);
        tile = player.getMidohraWorld().getBlockEntity(BlockPos.of(x, y, z)).getCompatBlockEntity(EMCExporterTile.class);

        if (PacketByteUtil.readBoolean(buf)) {
            tile.teamUUID = PacketByteUtil.readUuid(buf);

            if (PacketByteUtil.readBoolean(buf)) {
                ownerName = PacketByteUtil.readString(buf);
            }
        }
    }

    public EMCExporterScreenHandler(int syncId, PlayerInventory playerInventory, @Nullable EMCExporterTile tile, ICompatInventory filter) {
        super(ScreenHandlers.EMC_EXPORTER, syncId);

        this.filter = filter;
        this.playerInventory = playerInventory;
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
            ItemStack oldStack = callGetCursorStack().copy();
            super.onSlotClick(e);
            if (!ItemStackUtil.isEmpty(oldStack))
                callSetCursorStack(oldStack);

            return;
        }
        super.onSlotClick(e);
    }
}
