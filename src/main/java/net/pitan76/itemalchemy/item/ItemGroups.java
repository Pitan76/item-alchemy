package net.pitan76.itemalchemy.item;

import net.pitan76.mcpitanlib.api.item.CreativeTabBuilder;
import net.pitan76.mcpitanlib.api.util.ItemStackUtil;

import static net.pitan76.itemalchemy.ItemAlchemy._id;
import static net.pitan76.itemalchemy.ItemAlchemy.registry;

public class ItemGroups {
    public static CreativeTabBuilder ITEM_ALCHEMY = CreativeTabBuilder.create(_id("item_alchemy")).setIcon(() -> ItemStackUtil.create(Items.PHILOSOPHER_STONE.getOrNull(), 1));

    public static void init() {
        registry.registerItemGroup(ITEM_ALCHEMY);
    }
}
