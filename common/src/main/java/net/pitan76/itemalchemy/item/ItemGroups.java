package net.pitan76.itemalchemy.item;

import net.pitan76.mcpitanlib.api.item.CreativeTabBuilder;

import static net.pitan76.itemalchemy.ItemAlchemy._id;
import static net.pitan76.itemalchemy.ItemAlchemy.registry2;

public class ItemGroups {
    public static CreativeTabBuilder ITEM_ALCHEMY = CreativeTabBuilder.create(_id("item_alchemy")).setIconM(() -> Items.PHILOSOPHER_STONE.createStack(1));

    public static void init() {
        registry2.registerItemGroup(ITEM_ALCHEMY);
    }
}
