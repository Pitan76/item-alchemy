package ml.pkom.itemalchemy;

import ml.pkom.itemalchemy.items.AlchemyPad;
import ml.pkom.itemalchemy.items.PhilosopherStone;
import ml.pkom.mcpitanlibarch.api.event.registry.RegistryEvent;
import ml.pkom.mcpitanlibarch.api.item.ExtendSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

import static ml.pkom.itemalchemy.ItemAlchemy.id;
import static ml.pkom.itemalchemy.ItemAlchemy.registry;

public class Items {

    public static RegistryEvent<Item> PHILOSOPHER_STONE;
    public static RegistryEvent<Item> ALCHEMY_PAD;

    public static RegistryEvent<Item> ALCHEMY_TABLE;
    public static RegistryEvent<Item> EMC_COLLECTOR_MK1;
    public static RegistryEvent<Item> EMC_COLLECTOR_MK2;
    public static RegistryEvent<Item> EMC_COLLECTOR_MK3;
    public static RegistryEvent<Item> EMC_COLLECTOR_MK4;
    public static RegistryEvent<Item> EMC_COLLECTOR_MK5;
    public static RegistryEvent<Item> ALCHEMY_CHEST;
    public static RegistryEvent<Item> EMC_CONDENSER;
    //public static RegistryEvent<Item> EMC_CONDENSER_MK2;
    public static RegistryEvent<Item> EMC_REPEATER;
    public static RegistryEvent<Item> AEGU;
    public static RegistryEvent<Item> ADVANCED_AEGU;
    public static RegistryEvent<Item> ULTIMATE_AEGU;

    // Material
    public static RegistryEvent<Item> ALCHEMICAL_FUEL;
    public static RegistryEvent<Item> MOBIUS_FUEL;
    public static RegistryEvent<Item> AETERNALIS_FUEL;
    public static RegistryEvent<Item> LOW_COVALENCE_DUST;
    public static RegistryEvent<Item> MIDDLE_COVALENCE_DUST;
    public static RegistryEvent<Item> HIGH_COVALENCE_DUST;
    public static RegistryEvent<Item> DARK_MATTER;
    public static RegistryEvent<Item> RED_MATTER;
    public static RegistryEvent<Item> DARK_MATTER_BLOCK;
    public static RegistryEvent<Item> RED_MATTER_BLOCK;

    /*
    public static RegistryEvent<Item> DARK_MATTER_SWORD;
    public static RegistryEvent<Item> DARK_MATTER_PICKAXE;
    public static RegistryEvent<Item> DARK_MATTER_AXE;
    public static RegistryEvent<Item> DARK_MATTER_SHOVEL;
    public static RegistryEvent<Item> DARK_MATTER_HOE;
     */

    public static void init() {
        PHILOSOPHER_STONE = registry.registerItem(id("philosopher_stone"), () -> new PhilosopherStone(new ExtendSettings().addGroup(ItemGroups.ITEM_ALCHEMY, id("philosopher_stone")).maxDamage(256)));
        ALCHEMY_TABLE = registry.registerItem(id("alchemy_table"), () -> new BlockItem(Blocks.ALCHEMY_TABLE.getOrNull(), new ExtendSettings().addGroup(ItemGroups.ITEM_ALCHEMY, id("alchemy_table"))));
        EMC_COLLECTOR_MK1 = registry.registerItem(id("emc_collector_mk1"), () -> new BlockItem(Blocks.EMC_COLLECTOR_MK1.getOrNull(), new ExtendSettings().addGroup(ItemGroups.ITEM_ALCHEMY, id("emc_collector_mk1"))));
        EMC_COLLECTOR_MK2 = registry.registerItem(id("emc_collector_mk2"), () -> new BlockItem(Blocks.EMC_COLLECTOR_MK2.getOrNull(), new ExtendSettings().addGroup(ItemGroups.ITEM_ALCHEMY, id("emc_collector_mk2"))));
        EMC_COLLECTOR_MK3 = registry.registerItem(id("emc_collector_mk3"), () -> new BlockItem(Blocks.EMC_COLLECTOR_MK3.getOrNull(), new ExtendSettings().addGroup(ItemGroups.ITEM_ALCHEMY, id("emc_collector_mk3"))));
        EMC_COLLECTOR_MK4 = registry.registerItem(id("emc_collector_mk4"), () -> new BlockItem(Blocks.EMC_COLLECTOR_MK4.getOrNull(), new ExtendSettings().addGroup(ItemGroups.ITEM_ALCHEMY, id("emc_collector_mk4"))));
        EMC_COLLECTOR_MK5 = registry.registerItem(id("emc_collector_mk5"), () -> new BlockItem(Blocks.EMC_COLLECTOR_MK5.getOrNull(), new ExtendSettings().addGroup(ItemGroups.ITEM_ALCHEMY, id("emc_collector_mk5"))));
        ALCHEMY_CHEST = registry.registerItem(id("alchemy_chest"), () -> new BlockItem(Blocks.ALCHEMY_CHEST.getOrNull(), new ExtendSettings().addGroup(ItemGroups.ITEM_ALCHEMY, id("alchemy_chest"))));
        EMC_CONDENSER = registry.registerItem(id("emc_condenser"), () -> new BlockItem(Blocks.EMC_CONDENSER.getOrNull(), new ExtendSettings().addGroup(ItemGroups.ITEM_ALCHEMY, id("emc_condenser"))));
        //EMC_CONDENSER_MK2 = registry.registerItem(id("emc_condenser_mk2"), () -> new BlockItem(Blocks.EMC_CONDENSER_MK2.getOrNull(), new ExtendSettings()));//.addGroup(ItemGroups.ITEM_ALCHEMY, id("emc_condenser_mk2"))));
        EMC_REPEATER = registry.registerItem(id("emc_repeater"), () -> new BlockItem(Blocks.EMC_REPEATER.getOrNull(), new ExtendSettings().addGroup(ItemGroups.ITEM_ALCHEMY, id("emc_repeater"))));
        AEGU = registry.registerItem(id("aegu"), () -> new BlockItem(Blocks.AEGU.getOrNull(), new ExtendSettings().addGroup(ItemGroups.ITEM_ALCHEMY, id("aegu"))));
        ADVANCED_AEGU = registry.registerItem(id("advanced_aegu"), () -> new BlockItem(Blocks.ADVANCED_AEGU.getOrNull(), new ExtendSettings().addGroup(ItemGroups.ITEM_ALCHEMY, id("advanced_aegu"))));
        ULTIMATE_AEGU = registry.registerItem(id("ultimate_aegu"), () -> new BlockItem(Blocks.ULTIMATE_AEGU.getOrNull(), new ExtendSettings().addGroup(ItemGroups.ITEM_ALCHEMY, id("ultimate_aegu"))));
        ALCHEMY_PAD = registry.registerItem(id("alchemy_pad"), () -> new AlchemyPad(new ExtendSettings().addGroup(ItemGroups.ITEM_ALCHEMY, id("alchemy_pad")).maxCount(1)));

        ALCHEMICAL_FUEL = registry.registerItem(id("alchemical_fuel"), () -> new Item(new ExtendSettings().addGroup(ItemGroups.ITEM_ALCHEMY, id("alchemical_fuel"))));
        MOBIUS_FUEL = registry.registerItem(id("mobius_fuel"), () -> new Item(new ExtendSettings().addGroup(ItemGroups.ITEM_ALCHEMY, id("mobius_fuel"))));
        AETERNALIS_FUEL = registry.registerItem(id("aeternalis_fuel"), () -> new Item(new ExtendSettings().addGroup(ItemGroups.ITEM_ALCHEMY, id("aeternalis_fuel"))));
        LOW_COVALENCE_DUST = registry.registerItem(id("low_covalence_dust"), () -> new Item(new ExtendSettings().addGroup(ItemGroups.ITEM_ALCHEMY, id("low_covalence_dust"))));
        MIDDLE_COVALENCE_DUST = registry.registerItem(id("middle_covalence_dust"), () -> new Item(new ExtendSettings().addGroup(ItemGroups.ITEM_ALCHEMY, id("middle_covalence_dust"))));
        HIGH_COVALENCE_DUST = registry.registerItem(id("high_covalence_dust"), () -> new Item(new ExtendSettings().addGroup(ItemGroups.ITEM_ALCHEMY, id("high_covalence_dust"))));
        DARK_MATTER = registry.registerItem(id("dark_matter"), () -> new Item(new ExtendSettings().addGroup(ItemGroups.ITEM_ALCHEMY, id("dark_matter"))));
        RED_MATTER = registry.registerItem(id("red_matter"), () -> new Item(new ExtendSettings().addGroup(ItemGroups.ITEM_ALCHEMY, id("red_matter"))));
        DARK_MATTER_BLOCK = registry.registerItem(id("dark_matter_block"), () -> new BlockItem(Blocks.DARK_MATTER_BLOCK.getOrNull(), new ExtendSettings().addGroup(ItemGroups.ITEM_ALCHEMY, id("dark_matter_block"))));
        RED_MATTER_BLOCK = registry.registerItem(id("red_matter_block"), () -> new BlockItem(Blocks.RED_MATTER_BLOCK.getOrNull(), new ExtendSettings().addGroup(ItemGroups.ITEM_ALCHEMY, id("red_matter_block"))));

        //DARK_MATTER_SWORD = registry.registerItem(id("dark_matter_sword"))
    }
}
