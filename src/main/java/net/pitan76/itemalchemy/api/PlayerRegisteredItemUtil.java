package net.pitan76.itemalchemy.api;

import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.itemalchemy.data.ModState;
import net.pitan76.itemalchemy.data.ServerState;
import net.pitan76.itemalchemy.data.TeamState;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.util.CompatIdentifier;
import net.pitan76.mcpitanlib.api.util.PersistentStateUtil;
import net.pitan76.mcpitanlib.api.util.WorldUtil;
import net.pitan76.mcpitanlib.api.util.item.ItemUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlayerRegisteredItemUtil {
    public static List<String> getItemsAsString(Player player) {
        Optional<TeamState> teamState = ModState.getModState(player.getWorld().getServer()).getTeamByPlayer(player.getUUID());

        return teamState.<List<String>>map(state -> new ArrayList<>(state.registeredItems)).orElseGet(ArrayList::new);

    }

    public static List<Item> getItems(Player player) {
        List<Item> items = new ArrayList<>();
        for (String id : getItemsAsString(player)) {
            items.add(ItemUtil.fromId(CompatIdentifier.of(id)));
        }

        return items;
    }

    public static void setItems(Player player, List<Item> items) {
        List<String> ids = new ArrayList<>();
        for (Item item : items) {
            ids.add(ItemUtil.toId(item).toString());
        }
        setItemsForString(player, ids);
    }

    public static void setItemsForString(Player player, List<String> list) {
        Optional<MinecraftServer> server = WorldUtil.getServer(player.getWorld());
        if (!server.isPresent()) return;

        Optional<TeamState> teamState = ModState.getModState(server.get()).getTeamByPlayer(player.getUUID());
        if (!teamState.isPresent()) return;

        teamState.get().registeredItems = list;

        if (!player.isClient()) {
            PersistentStateUtil.markDirty(ServerState.getServerState(server.get()));
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
