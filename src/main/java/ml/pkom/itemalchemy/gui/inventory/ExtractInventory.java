package ml.pkom.itemalchemy.gui.inventory;

import ml.pkom.itemalchemy.EMCManager;
import ml.pkom.itemalchemy.gui.screens.AlchemyTableScreenHandler;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import ml.pkom.mcpitanlibarch.api.nbt.NbtTag;
import ml.pkom.mcpitanlibarch.api.util.ItemUtil;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtractInventory extends SimpleInventory {
    public Player player;

    public boolean isSettingStack = true;

    @Nullable
    public AlchemyTableScreenHandler screenHandler;

    public ExtractInventory(int size, Player player, @Nullable AlchemyTableScreenHandler screenHandler) {
        super(size);
        this.screenHandler = screenHandler;

        this.player = player;
        placeExtractSlots();

    }

    public void placeExtractSlots() {
        if (player.getPlayerEntity() instanceof ServerPlayerEntity)
            EMCManager.syncS2C((ServerPlayerEntity) player.getPlayerEntity());
        isSettingStack = true;

        definedStacks.clear();
        int index = screenHandler != null ?  screenHandler.index : 0;

        NbtTag nbtTag = NbtTag.create();
        player.getPlayerEntity().writeCustomDataToNbt(nbtTag);

        NbtCompound items = NbtTag.create();

        if (nbtTag.contains("itemalchemy")) {
            NbtCompound itemAlchemyTag = nbtTag.getCompound("itemalchemy");
            if (itemAlchemyTag.contains("registered_items")) {
                items = itemAlchemyTag.getCompound("registered_items");
            }
        }

        List<String> keys = new ArrayList<>(items.getKeys());

        if (!keys.isEmpty()) {
            for (int i = 0 ; i < 13; i++) {
                int id_index = i + (index * 13);
                if (keys.size() < id_index + 1) {
                    setStack(i + 64, ItemStack.EMPTY);
                    continue;
                }
                Identifier id = new Identifier(keys.get(id_index));
                //System.out.println(id);
                if (!ItemUtil.isExist(id)) continue;
                ItemStack itemStack = new ItemStack(ItemUtil.fromId(id), 1);
                setStack(i + 64, itemStack);
            }
        }
        isSettingStack = false;
    }

    public Map<Integer, ItemStack> definedStacks = new HashMap<>();

    @Override
    public void setStack(int slot, ItemStack stack) {
        ItemStack definedStack = definedStacks.get(slot);
        if (!stack.isEmpty() && !definedStacks.containsKey(slot)) {
            definedStack = stack.copy();
            definedStacks.put(slot, definedStack);
        }
        super.setStack(slot, stack);
        if (!isSettingStack) {
            super.setStack(slot, stack);
            if (definedStack != null && stack.isEmpty()) {
                EMCManager.decrementEmc(player, EMCManager.get(definedStack));
                super.setStack(slot, definedStack.copy());
            }
        }
    }
}
