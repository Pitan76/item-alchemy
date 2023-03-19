package ml.pkom.itemalchemy.gui.slot;

import ml.pkom.itemalchemy.EMCManager;
import ml.pkom.itemalchemy.gui.inventory.ExtractInventory;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import ml.pkom.mcpitanlibarch.api.gui.slot.CompatibleSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class ExtractSlot extends CompatibleSlot {

    public ExtractInventory inventory;
    public Player player;

    public ExtractSlot(ExtractInventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.inventory = inventory;
        this.player = inventory.player;
    }

    public boolean canTakeItem = true;

    @Override
    public boolean canTakeItems(PlayerEntity playerEntity) {
        return EMCManager.getEmcFromPlayer(player) >= EMCManager.get(callGetStack().getItem()) && canTakeItem;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }
}
