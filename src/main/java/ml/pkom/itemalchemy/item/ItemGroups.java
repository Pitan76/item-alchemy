package ml.pkom.itemalchemy.item;


import ml.pkom.itemalchemy.ItemAlchemy;
import net.minecraft.item.ItemGroup;

import static ml.pkom.itemalchemy.ItemAlchemy.id;
import static ml.pkom.itemalchemy.ItemAlchemy.registry;

public class ItemGroups {
    public static ItemGroup ITEM_ALCHEMY = ItemAlchemy.ITEM_ALCHEMY;

    public static void init() {
        registry.registerItemGroup(id("item_alchemy"), () -> ITEM_ALCHEMY);
    }
}
