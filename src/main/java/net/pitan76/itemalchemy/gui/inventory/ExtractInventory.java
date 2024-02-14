package net.pitan76.itemalchemy.gui.inventory;

import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.itemalchemy.data.ModState;
import net.pitan76.itemalchemy.data.TeamState;
import net.pitan76.itemalchemy.gui.screen.AlchemyTableScreenHandler;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import ml.pkom.mcpitanlibarch.api.util.ItemUtil;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
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
        TeamState teamState = ModState
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
        int notExists = 0;

        for (int i = 0; i < 13; i++) {
            int id_index = i + (index * 13) + notExists;
            if (keys.size() < id_index + 1) {
                setStack(i + 64, ItemStack.EMPTY);
                continue;
            }
            Identifier id = new Identifier(keys.get(id_index));
            //System.out.println(id);
            if (!ItemUtil.isExist(id)) {
                i--;
                notExists++;
                continue;
            }
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
