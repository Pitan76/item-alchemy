package ml.pkom.itemalchemy.gui.inventory;

import ml.pkom.itemalchemy.EMCManager;
import ml.pkom.itemalchemy.ItemAlchemyClient;
import ml.pkom.itemalchemy.data.PlayerState;
import ml.pkom.itemalchemy.data.ServerState;
import ml.pkom.itemalchemy.data.TeamState;
import ml.pkom.itemalchemy.gui.screen.AlchemyTableScreenHandler;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import ml.pkom.mcpitanlibarch.api.nbt.NbtTag;
import ml.pkom.mcpitanlibarch.api.util.ItemUtil;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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
        TeamState teamState = EMCManager
                .getModState(player.getWorld().getServer())
                .getTeamByPlayer(player.getUUID())
                .get();

        placeExtractSlots(teamState.registeredItems);
    }

    public void placeExtractSlots(List<String> keys) {
        if (player.getPlayerEntity() instanceof ServerPlayerEntity)
            EMCManager.syncS2C((ServerPlayerEntity) player.getPlayerEntity());
        isSettingStack = true;

        definedStacks.clear();
        int index = screenHandler != null ?  screenHandler.index : 0;

        for (int i = 0; i < 13; i++) {
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
        isSettingStack = false;
    }

    public Map<Integer, ItemStack> definedStacks = new HashMap<>();

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (!stack.isEmpty() && !definedStacks.containsKey(slot)) {
            //definedStack = stack.copy();
            definedStacks.put(slot, stack.copy());
            super.setStack(slot, stack);
        } else if (isSettingStack) {
            super.setStack(slot, ItemStack.EMPTY);
        } else {
            super.setStack(slot, stack);
        }
        super.setStack(slot, stack);
    }
}
