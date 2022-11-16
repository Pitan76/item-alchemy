package ml.pkom.itemalchemy;

import ml.pkom.itemalchemy.items.AlchemyPad;
import ml.pkom.itemalchemy.items.PhilosopherStone;
import ml.pkom.mcpitanlibarch.api.event.registry.RegistryEvent;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

import static ml.pkom.itemalchemy.ItemAlchemy.*;

public class Items {

    public static RegistryEvent<Item> PHILOSOPHER_STONE;
    public static RegistryEvent<Item> ALCHEMY_PAD;

    public static RegistryEvent<Item> ALCHEMY_TABLE;
    public static RegistryEvent<Item> EMC_COLLECTOR_MK1;
    public static RegistryEvent<Item> EMC_COLLECTOR_MK2;
    public static RegistryEvent<Item> EMC_COLLECTOR_MK3;
    public static RegistryEvent<Item> EMC_CONDENSER;

    public static void init() {
        PHILOSOPHER_STONE = registry.registerItem(id("philosopher_stone"), () -> new PhilosopherStone(new FabricItemSettings().maxDamage(256).group(ItemGroups.ITEM_ALCHEMY)));
        ALCHEMY_TABLE = registry.registerItem(id("alchemy_table"), () -> new BlockItem(Blocks.ALCHEMY_TABLE.getOrNull(), new Item.Settings().group(ItemGroups.ITEM_ALCHEMY)));
        EMC_COLLECTOR_MK1 = registry.registerItem(id("emc_collector_mk1"), () -> new BlockItem(Blocks.EMC_COLLECTOR_MK1.getOrNull(), new Item.Settings().group(ItemGroups.ITEM_ALCHEMY)));
        EMC_COLLECTOR_MK2 = registry.registerItem(id("emc_collector_mk2"), () -> new BlockItem(Blocks.EMC_COLLECTOR_MK2.getOrNull(), new Item.Settings().group(ItemGroups.ITEM_ALCHEMY)));
        EMC_COLLECTOR_MK3 = registry.registerItem(id("emc_collector_mk3"), () -> new BlockItem(Blocks.EMC_COLLECTOR_MK3.getOrNull(), new Item.Settings().group(ItemGroups.ITEM_ALCHEMY)));
        EMC_CONDENSER = registry.registerItem(id("emc_condenser"), () -> new BlockItem(Blocks.EMC_CONDENSER.getOrNull(), new Item.Settings().group(ItemGroups.ITEM_ALCHEMY)));
        ALCHEMY_PAD = registry.registerItem(id("alchemy_pad"), () -> new AlchemyPad(new FabricItemSettings().maxCount(1).group(ItemGroups.ITEM_ALCHEMY)));
    }
}
