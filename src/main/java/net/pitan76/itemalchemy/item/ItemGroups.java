package net.pitan76.itemalchemy.item;


import net.pitan76.itemalchemy.ItemAlchemy;
import net.minecraft.item.ItemGroup;

import static net.pitan76.itemalchemy.ItemAlchemy.id;
import static net.pitan76.itemalchemy.ItemAlchemy.registry;

public class ItemGroups {
    public static ItemGroup ITEM_ALCHEMY = ItemAlchemy.ITEM_ALCHEMY;

    public static void init() {
        registry.registerItemGroup(id("item_alchemy"), () -> ITEM_ALCHEMY);
    }
}
