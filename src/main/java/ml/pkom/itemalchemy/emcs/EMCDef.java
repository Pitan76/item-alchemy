package ml.pkom.itemalchemy.emcs;

import ml.pkom.itemalchemy.EMCManager;
import ml.pkom.mcpitanlibarch.api.tag.TagKey;
import net.minecraft.item.Item;

public abstract class EMCDef {
    public abstract void addAll();

    public static void add(String id, long emc) {
        EMCManager.add(id, emc);
    }

    public static void add(Item item, long emc) {
        EMCManager.add(item, emc);
    }

    public static void add2(TagKey<Item> tagKey, long emc) {
        EMCManager.add2(tagKey, emc);
    }
}
