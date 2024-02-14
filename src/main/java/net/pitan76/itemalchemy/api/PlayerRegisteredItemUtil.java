package net.pitan76.itemalchemy.api;

import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.itemalchemy.data.ModState;
import net.pitan76.itemalchemy.data.ServerState;
import net.pitan76.itemalchemy.data.TeamState;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.pitan76.mcpitanlib.api.util.ItemUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlayerRegisteredItemUtil {
    public static List<String> getItemsAsString(Player player) {
        Optional<TeamState> teamState = ModState.getModState(player.getWorld().getServer()).getTeamByPlayer(player.getUUID());

        if(!teamState.isPresent()) {
            return new ArrayList<>();
        }

        return new ArrayList<>(teamState.get().registeredItems);
    }

    public static List<Item> getItems(Player player) {
        List<Item> items = new ArrayList<>();
        for (String id : getItemsAsString(player)) {
            items.add(ItemUtil.fromId(new Identifier(id)));
        }

        return items;
    }

    public static void setItems(Player player, List<Item> items) {
        List<String> ids = new ArrayList<>();
        for (Item item : items) {
            ids.add(ItemUtil.toID(item).toString());
        }
        setItemsForString(player, ids);
    }

    public static void setItemsForString(Player player, List<String> list) {
        Optional<TeamState> teamState = ModState.getModState(player.getWorld().getServer()).getTeamByPlayer(player.getUUID());

        if(!teamState.isPresent()) {
            return;
        }

        teamState.get().registeredItems = list;

        if(!player.isClient()) {
            ServerState.getServerState(player.getWorld().getServer()).markDirty();
        }
    }

    public static int count(Player player) {
        return getItemsAsString(player).size();
    }

    public static void add(Player player, String id) {
        List<String> ids = getItemsAsString(player);
        ids.add(id);
        setItemsForString(player, ids);
    }

    public static void add(Player player, Item item) {
        List<Item> items = getItems(player);
        items.add(item);
        setItems(player, items);
    }

    public static void addAll(Player player) {
        List<String> items = new ArrayList<>(EMCManager.getMap().keySet());

        setItemsForString(player, items);
    }

    public static void remove(Player player, String id) {
        List<String> ids = getItemsAsString(player);
        ids.remove(id);
        setItemsForString(player, ids);
    }

    public static void remove(Player player, Item item) {
        List<Item> items = getItems(player);
        items.remove(item);
        setItems(player, items);
    }

    public static void removeAll(Player player) {
        // Use empty list
        setItems(player, new ArrayList<>());
    }

    public static boolean contains(Player player, String id) {
        return getItemsAsString(player).contains(id);
    }
    public static boolean contains(Player player, Item item) {
        return getItems(player).contains(item);
    }
}
