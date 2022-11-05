package ml.pkom.itemalchemy.gui;

import ml.pkom.itemalchemy.EMCManager;
import ml.pkom.itemalchemy.screens.AlchemyTableScreenHandler;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import ml.pkom.mcpitanlibarch.api.nbt.NbtTag;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.registry.Registry;

public class RegisterInventory extends SimpleInventory {
    public Player player;
    public RegisterInventory(int size, Player player) {
        super(size);
        this.player = player;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (!stack.isEmpty()) {

            NbtTag playerNbt = NbtTag.create();
            player.getPlayerEntity().writeCustomDataToNbt(playerNbt);
            NbtCompound items = NbtTag.create();

            if (playerNbt.contains("itemalchemy")) {
                NbtCompound itemAlchemyTag = playerNbt.getCompound("itemalchemy");
                if (itemAlchemyTag.contains("registered_items")) {
                    items = itemAlchemyTag.getCompound("registered_items");
                }
            }

            if (!items.contains(Registry.ITEM.getId(stack.getItem()).toString())) {
                items.putBoolean(Registry.ITEM.getId(stack.getItem()).toString(), true);
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
            //player.getPlayerEntity().saveNbt(playerNbt);

            if (slot == 50) {
                EMCManager.writeEmcToPlayer(player, stack);
            } else {
                player.offerOrDrop(stack.copy());
            }

            stack = ItemStack.EMPTY;
        }
        super.setStack(slot, stack);
    }
}
