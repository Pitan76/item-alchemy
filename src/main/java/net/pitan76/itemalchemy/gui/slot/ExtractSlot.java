package net.pitan76.itemalchemy.gui.slot;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.itemalchemy.gui.inventory.ExtractInventory;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.gui.slot.CompatibleSlot;

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
