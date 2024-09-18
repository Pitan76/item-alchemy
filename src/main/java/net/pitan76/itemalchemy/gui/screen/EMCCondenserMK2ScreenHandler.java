package net.pitan76.itemalchemy.gui.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.pitan76.itemalchemy.gui.slot.CondenserMK2InputSlot;
import net.pitan76.itemalchemy.gui.slot.CondenserMK2OutputSlot;
import net.pitan76.itemalchemy.tile.EMCCondenserTile;
import net.pitan76.mcpitanlib.api.util.*;
import org.jetbrains.annotations.Nullable;

public class EMCCondenserMK2ScreenHandler extends EMCCondenserScreenHandler {

    public EMCCondenserMK2ScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        super(syncId, playerInventory, InventoryUtil.createSimpleInventory(85), buf);
    }

    public EMCCondenserMK2ScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PacketByteBuf buf) {
        super(syncId, playerInventory, inventory, buf);
    }

    public EMCCondenserMK2ScreenHandler(int syncId, PlayerInventory playerInventory, @Nullable EMCCondenserTile tile, Inventory inventory, ItemStack targetStack) {
        super(syncId, playerInventory, tile, inventory, targetStack);
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
    protected Slot addStorageSlot(Inventory inventory, int index, int x, int y) {
        if (index >= 43)
            return this.callAddSlot(new CondenserMK2OutputSlot(inventory, index, x, y));

        return this.callAddSlot(new CondenserMK2InputSlot(inventory, index, x, y));
    }
}
