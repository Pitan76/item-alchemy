package ml.pkom.itemalchemy.api;

import ml.pkom.itemalchemy.EMCManager;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import ml.pkom.mcpitanlibarch.api.util.ItemUtil;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class PlayerRegisteredItemUtil {
    public static List<String> getItemsAsString(Player player) {

        NbtCompound playerNbt = EMCManager.writePlayerNbt(player);

        NbtCompound items = new NbtCompound();

        if (playerNbt.contains("itemalchemy")) {
            NbtCompound itemAlchemyTag = playerNbt.getCompound("itemalchemy");
            if (itemAlchemyTag.contains("registered_items")) {
                items = itemAlchemyTag.getCompound("registered_items");
            }
        }
        if (items.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(items.getKeys());
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
        NbtCompound playerNbt = EMCManager.writePlayerNbt(player);
        NbtCompound items = new NbtCompound();
        for (String id : list) {
            items.putBoolean(id, true);
        }

        if (playerNbt.contains("itemalchemy")) {
            NbtCompound itemAlchemyTag = playerNbt.getCompound("itemalchemy");
            itemAlchemyTag.put("registered_items", items);
        } else {
            NbtCompound itemAlchemyTag = new NbtCompound();
            itemAlchemyTag.put("registered_items", items);
            playerNbt.put("itemalchemy", itemAlchemyTag);
        }
        EMCManager.readPlayerNbt(player, playerNbt);
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

    public static boolean contains(Player player, String id) {
        return getItemsAsString(player).contains(id);
    }
    public static boolean contains(Player player, Item item) {
        return getItems(player).contains(item);
    }
}
