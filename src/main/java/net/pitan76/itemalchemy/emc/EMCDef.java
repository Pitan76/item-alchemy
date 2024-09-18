package net.pitan76.itemalchemy.emc;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.mcpitanlib.api.tag.TagKey;
import net.pitan76.mcpitanlib.api.util.IdentifierUtil;
import net.pitan76.mcpitanlib.api.util.ItemUtil;

public abstract class EMCDef {
    public abstract void addAll();

    public static void add(String id, long emc) {
        EMCManager.add(id, emc);
    }

    public static void add(Item item, long emc) {
        EMCManager.add(item, emc);
    }

    public static void addByTag(TagKey<Item> tagKey, long emc) {
        EMCManager.add2(tagKey, emc);
    }

    public static void addByTag(Identifier identifier, long emc) {
        ItemUtil.getItems(identifier).forEach(item -> {
            add(item, emc);
        });
    }

    public static void addByTag(String id, long emc) {
        addByTag(IdentifierUtil.id(id), emc);
    }
}
