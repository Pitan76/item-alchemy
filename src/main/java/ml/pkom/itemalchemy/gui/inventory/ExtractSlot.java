package ml.pkom.itemalchemy.gui.inventory;

import ml.pkom.itemalchemy.EMCManager;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class ExtractSlot extends Slot {

    public ExtractInventory inventory;
    public Player player;

    public ExtractSlot(ExtractInventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.inventory = inventory;
        this.player = inventory.player;
    }

    @Override
    public boolean canTakeItems(PlayerEntity playerEntity) {
        return EMCManager.getEmcFromPlayer(player) >= EMCManager.get(getStack()) && super.canTakeItems(playerEntity);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }

    /*
    public ItemStack definedStack = ItemStack.EMPTY;

    @Override
    public void setStack(ItemStack stack) {
        if (!stack.isEmpty() && definedStack.isEmpty()) {
            definedStack = stack.copy();
            System.out.println(definedStack.getName().asString());
        }
        super.setStack(stack);
        if (!inventory.isSettingStack) {
            super.setStack(stack);
            if (definedStack != null) {
                EMCManager.decrementEmc(player, EMCManager.get(definedStack));
                inventory.isSettingStack = true;
                setStack(definedStack.copy());
                inventory.isSettingStack = false;
            }
        }
    }

 */
}
