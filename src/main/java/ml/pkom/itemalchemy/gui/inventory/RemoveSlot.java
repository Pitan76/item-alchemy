package ml.pkom.itemalchemy.gui.inventory;

import ml.pkom.itemalchemy.gui.screens.AlchemyTableScreenHandler;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import ml.pkom.mcpitanlibarch.api.nbt.NbtTag;
import ml.pkom.mcpitanlibarch.api.util.ItemUtil;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.slot.Slot;

public class RemoveSlot extends Slot {
    public Player player;

    public RemoveSlot(Inventory inventory, int index, int x, int y, Player player) {
        super(inventory, index, x, y);
        this.player = player;
    }

    @Override
    public void setStack(ItemStack stack) {

        NbtTag playerNbt = NbtTag.create();
        player.getPlayerEntity().writeCustomDataToNbt(playerNbt);
        NbtCompound items = NbtTag.create();

        if (playerNbt.contains("itemalchemy")) {
            NbtCompound itemAlchemyTag = playerNbt.getCompound("itemalchemy");
            if (itemAlchemyTag.contains("registered_items")) {
                items = itemAlchemyTag.getCompound("registered_items");
            }
        }

        if (items.contains(ItemUtil.toID(stack.getItem()).toString())) {
            items.remove(ItemUtil.toID(stack.getItem()).toString());
            if (player.getCurrentScreenHandler() instanceof AlchemyTableScreenHandler) {
                AlchemyTableScreenHandler screenHandler = (AlchemyTableScreenHandler) player.getCurrentScreenHandler();
                screenHandler.extractInventory.placeExtractSlots();
            }
        }

        if (playerNbt.contains("itemalchemy")) {
            NbtCompound itemAlchemyTag = playerNbt.getCompound("itemalchemy");
            itemAlchemyTag.put("registered_items", items);
        } else {
            NbtCompound itemAlchemyTag = new NbtTag();
            itemAlchemyTag.put("registered_items", items);
            playerNbt.put("itemalchemy", itemAlchemyTag);
        }
        player.getPlayerEntity().readCustomDataFromNbt(playerNbt);



        player.offerOrDrop(stack.copy());
        if (player.getCurrentScreenHandler() instanceof AlchemyTableScreenHandler) {
            AlchemyTableScreenHandler screenHandler = (AlchemyTableScreenHandler) player.getCurrentScreenHandler();
            screenHandler.extractInventory.placeExtractSlots();
        }
        super.setStack(ItemStack.EMPTY);
    }
}
