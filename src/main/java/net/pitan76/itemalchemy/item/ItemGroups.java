package net.pitan76.itemalchemy.item;


import net.minecraft.item.ItemStack;
import net.pitan76.mcpitanlib.api.item.CreativeTabBuilder;

import static net.pitan76.itemalchemy.ItemAlchemy.id;
import static net.pitan76.itemalchemy.ItemAlchemy.registry;

public class ItemGroups {
    public static CreativeTabBuilder ITEM_ALCHEMY = CreativeTabBuilder.create(id("item_alchemy")).setIcon(() -> new ItemStack(Items.PHILOSOPHER_STONE.getOrNull(), 1));

    public static void init() {
        registry.registerItemGroup(ITEM_ALCHEMY);
    }
}
