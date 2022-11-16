package ml.pkom.itemalchemy.gui.screens;

import ml.pkom.itemalchemy.ScreenHandlers;
import ml.pkom.mcpitanlibarch.api.gui.SimpleScreenHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.slot.Slot;

import java.util.List;

public class EMCCollectorScreenHandler extends SimpleScreenHandler {

    private final Inventory inventory;

    public EMCCollectorScreenHandler(int syncId) {
        super(ScreenHandlers.EMC_COLLECTOR, syncId);
        inventory = new SimpleInventory(16 + 3);
    }

    public EMCCollectorScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId);

        addPlayerMainInventorySlots(playerInventory, 24, 84);
        addPlayerHotbarSlots(playerInventory, 24, 142);
        addNormalSlot(inventory, 0, 149, 12);
        addNormalSlot(inventory, 1, 177, 35);
        addNormalSlot(inventory, 2, 149, 58);
        addSlots(inventory, 3, 14, 8, -1, 4, 4);
    }

    protected List<Slot> addPlayerMainInventorySlots(PlayerInventory inventory, int x, int y) {
        return this.addSlots(inventory, 20 + 9, x, y, DEFAULT_SLOT_SIZE, 9, 3);
    }

    protected List<Slot> addPlayerHotbarSlots(PlayerInventory inventory, int x, int y) {
        return this.addSlotsX(inventory, 20, x, y, DEFAULT_SLOT_SIZE, 9);
    }
}
