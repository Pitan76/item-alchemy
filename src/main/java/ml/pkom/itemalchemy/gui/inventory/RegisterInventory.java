package ml.pkom.itemalchemy.gui.inventory;

import ml.pkom.itemalchemy.EMCManager;
import ml.pkom.itemalchemy.gui.screen.AlchemyTableScreenHandler;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import ml.pkom.mcpitanlibarch.api.nbt.NbtTag;
import ml.pkom.mcpitanlibarch.api.util.ItemUtil;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class RegisterInventory extends SimpleInventory {
    public Player player;
    public RegisterInventory(int size, Player player) {
        super(size);
        this.player = player;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (!stack.isEmpty()) {

            NbtCompound playerNbt = EMCManager.writePlayerNbt(player);

            NbtCompound items = NbtTag.create();

            if (playerNbt.contains("itemalchemy")) {
                NbtCompound itemAlchemyTag = playerNbt.getCompound("itemalchemy");
                if (itemAlchemyTag.contains("registered_items")) {
                    items = itemAlchemyTag.getCompound("registered_items");
                }
            }

            if (!items.contains(ItemUtil.toID(stack.getItem()).toString())) {
                items.putBoolean(ItemUtil.toID(stack.getItem()).toString(), true);
            }

            if (playerNbt.contains("itemalchemy")) {
                NbtCompound itemAlchemyTag = playerNbt.getCompound("itemalchemy");
                itemAlchemyTag.put("registered_items", items);
            } else {
                NbtCompound itemAlchemyTag = NbtTag.create();
                itemAlchemyTag.put("registered_items", items);
                playerNbt.put("itemalchemy", itemAlchemyTag);
            }
            EMCManager.readPlayerNbt(player, playerNbt);
            //player.getPlayerEntity().saveNbt(playerNbt);

            if (slot == 50) {
                if (!player.getWorld().isClient) {
                    EMCManager.writeEmcToPlayer(player, stack);
                }
            } else {
                player.offerOrDrop(stack.copy());
            }

            if (player.getCurrentScreenHandler() instanceof AlchemyTableScreenHandler) {
                AlchemyTableScreenHandler screenHandler = (AlchemyTableScreenHandler) player.getCurrentScreenHandler();
                screenHandler.extractInventory.placeExtractSlots();
            }

            stack = ItemStack.EMPTY;
        }
        super.setStack(slot, stack);
    }
}
