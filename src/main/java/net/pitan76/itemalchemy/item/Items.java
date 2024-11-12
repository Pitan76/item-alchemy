package net.pitan76.itemalchemy.item;

import net.minecraft.item.Item;
import net.pitan76.itemalchemy.block.Blocks;
import net.pitan76.itemalchemy.util.ChargeItemSettings;
import net.pitan76.mcpitanlib.api.item.v2.CompatItem;
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.registry.result.RegistryResult;
import net.pitan76.mcpitanlib.api.util.CompatRarity;
import net.pitan76.mcpitanlib.api.util.item.ItemUtil;

import static net.pitan76.itemalchemy.ItemAlchemy._id;
import static net.pitan76.itemalchemy.ItemAlchemy.registry;

public class Items {

    public static RegistryResult<Item> PHILOSOPHER_STONE;
    public static RegistryResult<Item> ALCHEMY_PAD;

    public static RegistryResult<Item> TOME_OF_KNOWLEDGE;

    public static RegistryResult<Item> ALCHEMY_TABLE;
    public static RegistryResult<Item> EMC_COLLECTOR_MK1;
    public static RegistryResult<Item> EMC_COLLECTOR_MK2;
    public static RegistryResult<Item> EMC_COLLECTOR_MK3;
    public static RegistryResult<Item> EMC_COLLECTOR_MK4;
    public static RegistryResult<Item> EMC_COLLECTOR_MK5;
    public static RegistryResult<Item> ALCHEMY_CHEST;
    public static RegistryResult<Item> EMC_CONDENSER;
    public static RegistryResult<Item> EMC_CONDENSER_MK2;
    public static RegistryResult<Item> EMC_REPEATER;
    public static RegistryResult<Item> AEGU;
    public static RegistryResult<Item> ADVANCED_AEGU;
    public static RegistryResult<Item> ULTIMATE_AEGU;
    public static RegistryResult<Item> EMC_IMPORTER;
    public static RegistryResult<Item> EMC_EXPORTER;
    public static RegistryResult<Item> EMC_CABLE;
    public static RegistryResult<Item> EMC_BATTERY;

    // Material
    public static RegistryResult<Item> ALCHEMICAL_FUEL;
    public static RegistryResult<Item> MOBIUS_FUEL;
    public static RegistryResult<Item> AETERNALIS_FUEL;
    public static RegistryResult<Item> LOW_COVALENCE_DUST;
    public static RegistryResult<Item> MIDDLE_COVALENCE_DUST;
    public static RegistryResult<Item> HIGH_COVALENCE_DUST;
    public static RegistryResult<Item> DARK_MATTER;
    public static RegistryResult<Item> RED_MATTER;
    public static RegistryResult<Item> DARK_MATTER_BLOCK;
    public static RegistryResult<Item> RED_MATTER_BLOCK;

    public static RegistryResult<Item> IA_WRENCH;
    public static RegistryResult<Item> DIVINING_ROD_LV1;
    public static RegistryResult<Item> DIVINING_ROD_LV2;
    public static RegistryResult<Item> DIVINING_ROD_LV3;

    public static RegistryResult<Item> RING;
    public static RegistryResult<Item> PICKUP_RING;

    public static RegistryResult<Item> DARK_MATTER_SWORD;
    public static RegistryResult<Item> DARK_MATTER_PICKAXE;
    public static RegistryResult<Item> DARK_MATTER_AXE;
    public static RegistryResult<Item> DARK_MATTER_SHOVEL;
    public static RegistryResult<Item> DARK_MATTER_HOE;

    public static RegistryResult<Item> RED_MATTER_SWORD;
    public static RegistryResult<Item> RED_MATTER_PICKAXE;
    public static RegistryResult<Item> RED_MATTER_AXE;
    public static RegistryResult<Item> RED_MATTER_SHOVEL;
    public static RegistryResult<Item> RED_MATTER_HOE;

    public static void init() {
        PHILOSOPHER_STONE = registry.registerItem(_id("philosopher_stone"), () -> new PhilosopherStone(ChargeItemSettings.of(_id("philosopher_stone")).addGroup(ItemGroups.ITEM_ALCHEMY).rarity(CompatRarity.EPIC).recipeRemainder(Items.PHILOSOPHER_STONE.getOrNull())));
        ALCHEMY_TABLE = registry.registerItem(_id("alchemy_table"), () -> ItemUtil.create(Blocks.ALCHEMY_TABLE.getOrNull(), CompatibleItemSettings.of(_id("alchemy_table")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        TOME_OF_KNOWLEDGE = registry.registerItem(_id("tome_of_knowledge"), () -> new TomeOfKnowledge(CompatibleItemSettings.of(_id("tome_of_knowledge")).rarity(CompatRarity.EPIC).maxCount(1).addGroup(ItemGroups.ITEM_ALCHEMY)));
        EMC_COLLECTOR_MK1 = registry.registerItem(_id("emc_collector_mk1"), () -> ItemUtil.create(Blocks.EMC_COLLECTOR_MK1.getOrNull(), CompatibleItemSettings.of(_id("emc_collector_mk1")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        EMC_COLLECTOR_MK2 = registry.registerItem(_id("emc_collector_mk2"), () -> ItemUtil.create(Blocks.EMC_COLLECTOR_MK2.getOrNull(), CompatibleItemSettings.of(_id("emc_collector_mk2")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        EMC_COLLECTOR_MK3 = registry.registerItem(_id("emc_collector_mk3"), () -> ItemUtil.create(Blocks.EMC_COLLECTOR_MK3.getOrNull(), CompatibleItemSettings.of(_id("emc_collector_mk3")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        EMC_COLLECTOR_MK4 = registry.registerItem(_id("emc_collector_mk4"), () -> ItemUtil.create(Blocks.EMC_COLLECTOR_MK4.getOrNull(), CompatibleItemSettings.of(_id("emc_collector_mk4")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        EMC_COLLECTOR_MK5 = registry.registerItem(_id("emc_collector_mk5"), () -> ItemUtil.create(Blocks.EMC_COLLECTOR_MK5.getOrNull(), CompatibleItemSettings.of(_id("emc_collector_mk5")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        ALCHEMY_CHEST = registry.registerItem(_id("alchemy_chest"), () -> ItemUtil.create(Blocks.ALCHEMY_CHEST.getOrNull(), CompatibleItemSettings.of(_id("alchemy_chest")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        EMC_CONDENSER = registry.registerItem(_id("emc_condenser"), () -> ItemUtil.create(Blocks.EMC_CONDENSER.getOrNull(), CompatibleItemSettings.of(_id("emc_condenser")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        EMC_CONDENSER_MK2 = registry.registerItem(_id("emc_condenser_mk2"), () -> ItemUtil.create(Blocks.EMC_CONDENSER_MK2.getOrNull(), CompatibleItemSettings.of(_id("emc_condenser_mk2")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        EMC_REPEATER = registry.registerItem(_id("emc_repeater"), () -> ItemUtil.create(Blocks.EMC_REPEATER.getOrNull(), CompatibleItemSettings.of(_id("emc_repeater")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        AEGU = registry.registerItem(_id("aegu"), () -> ItemUtil.create(Blocks.AEGU.getOrNull(), CompatibleItemSettings.of(_id("aegu")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        ADVANCED_AEGU = registry.registerItem(_id("advanced_aegu"), () -> ItemUtil.create(Blocks.ADVANCED_AEGU.getOrNull(), CompatibleItemSettings.of(_id("advanced_aegu")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        ULTIMATE_AEGU = registry.registerItem(_id("ultimate_aegu"), () -> ItemUtil.create(Blocks.ULTIMATE_AEGU.getOrNull(), CompatibleItemSettings.of(_id("ultimate_aegu")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        EMC_IMPORTER = registry.registerItem(_id("emc_importer"), () -> ItemUtil.create(Blocks.EMC_IMPORTER.getOrNull(), CompatibleItemSettings.of(_id("emc_importer")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        EMC_EXPORTER = registry.registerItem(_id("emc_exporter"), () -> ItemUtil.create(Blocks.EMC_EXPORTER.getOrNull(), CompatibleItemSettings.of(_id("emc_exporter")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        EMC_CABLE = registry.registerItem(_id("emc_cable"), () -> ItemUtil.create(Blocks.EMC_CABLE.getOrNull(), CompatibleItemSettings.of(_id("emc_cable")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        EMC_BATTERY = registry.registerItem(_id("emc_battery"), () -> ItemUtil.create(Blocks.EMC_BATTERY.getOrNull(), CompatibleItemSettings.of(_id("emc_battery")).addGroup(ItemGroups.ITEM_ALCHEMY)));

        ALCHEMY_PAD = registry.registerItem(_id("alchemy_pad"), () -> new AlchemyPad(CompatibleItemSettings.of(_id("alchemy_pad")).addGroup(ItemGroups.ITEM_ALCHEMY).maxCount(1)));

        ALCHEMICAL_FUEL = registry.registerItem(_id("alchemical_fuel"), () -> new CompatItem(CompatibleItemSettings.of(_id("alchemical_fuel")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        MOBIUS_FUEL = registry.registerItem(_id("mobius_fuel"), () -> new CompatItem(CompatibleItemSettings.of(_id("mobius_fuel")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        AETERNALIS_FUEL = registry.registerItem(_id("aeternalis_fuel"), () -> new CompatItem(CompatibleItemSettings.of(_id("aeternalis_fuel")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        LOW_COVALENCE_DUST = registry.registerItem(_id("low_covalence_dust"), () -> new CompatItem(CompatibleItemSettings.of(_id("low_covalence_dust")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        MIDDLE_COVALENCE_DUST = registry.registerItem(_id("middle_covalence_dust"), () -> new CompatItem(CompatibleItemSettings.of(_id("middle_covalence_dust")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        HIGH_COVALENCE_DUST = registry.registerItem(_id("high_covalence_dust"), () -> new CompatItem(CompatibleItemSettings.of(_id("high_covalence_dust")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        DARK_MATTER = registry.registerItem(_id("dark_matter"), () -> new CompatItem(CompatibleItemSettings.of(_id("dark_matter")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        RED_MATTER = registry.registerItem(_id("red_matter"), () -> new CompatItem(CompatibleItemSettings.of(_id("red_matter")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        DARK_MATTER_BLOCK = registry.registerItem(_id("dark_matter_block"), () -> ItemUtil.create(Blocks.DARK_MATTER_BLOCK.getOrNull(), CompatibleItemSettings.of(_id("dark_matter_block")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        RED_MATTER_BLOCK = registry.registerItem(_id("red_matter_block"), () -> ItemUtil.create(Blocks.RED_MATTER_BLOCK.getOrNull(), CompatibleItemSettings.of(_id("red_matter_block")).addGroup(ItemGroups.ITEM_ALCHEMY)));

        IA_WRENCH = registry.registerItem(_id("wrench"), () -> new Wrench(CompatibleItemSettings.of(_id("wrench")).addGroup(ItemGroups.ITEM_ALCHEMY).maxCount(1)));
        DIVINING_ROD_LV1 = registry.registerItem(_id("divining_rod_lv1"), () -> new DiviningRod(1, CompatibleItemSettings.of(_id("divining_rod_lv1")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        DIVINING_ROD_LV2 = registry.registerItem(_id("divining_rod_lv2"), () -> new DiviningRod(2, CompatibleItemSettings.of(_id("divining_rod_lv2")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        DIVINING_ROD_LV3 = registry.registerItem(_id("divining_rod_lv3"), () -> new DiviningRod(3, CompatibleItemSettings.of(_id("divining_rod_lv3")).addGroup(ItemGroups.ITEM_ALCHEMY)));

        RING = registry.registerItem(_id("ring"), () -> new Ring(CompatibleItemSettings.of(_id("ring")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        PICKUP_RING = registry.registerItem(_id("pickup_ring"), () -> new PickupRing(CompatibleItemSettings.of(_id("pickup_ring")).addGroup(ItemGroups.ITEM_ALCHEMY)));

        DARK_MATTER_SWORD = registry.registerItem(_id("dark_matter_sword"), () -> new AlchemicalSword(AlchemicalToolMaterials.DARK_MATTER, 3, -2.4f, ChargeItemSettings.of(_id("dark_matter_sword")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        DARK_MATTER_PICKAXE = registry.registerItem(_id("dark_matter_pickaxe"), () -> new AlchemicalPickaxe(AlchemicalToolMaterials.DARK_MATTER, 1, -2.8f, ChargeItemSettings.of(_id("dark_matter_pickaxe")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        DARK_MATTER_AXE = registry.registerItem(_id("dark_matter_axe"), () -> new AlchemicalAxe(AlchemicalToolMaterials.DARK_MATTER, 5.0f, -3.0f, ChargeItemSettings.of(_id("dark_matter_axe")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        DARK_MATTER_SHOVEL = registry.registerItem(_id("dark_matter_shovel"), () -> new AlchemicalShovel(AlchemicalToolMaterials.DARK_MATTER, 1.5F, -3f, ChargeItemSettings.of(_id("dark_matter_shovel")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        DARK_MATTER_HOE = registry.registerItem(_id("dark_matter_hoe"), () -> new AlchemicalHoe(AlchemicalToolMaterials.DARK_MATTER, -3, 0f, ChargeItemSettings.of(_id("dark_matter_hoe")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        RED_MATTER_SWORD = registry.registerItem(_id("red_matter_sword"), () -> new AlchemicalSword(AlchemicalToolMaterials.RED_MATTER, 3, -2.4f, ChargeItemSettings.of(_id("red_matter_sword")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        RED_MATTER_PICKAXE = registry.registerItem(_id("red_matter_pickaxe"), () -> new AlchemicalPickaxe(AlchemicalToolMaterials.RED_MATTER, 1, -2.8f, ChargeItemSettings.of(_id("red_matter_pickaxe")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        RED_MATTER_AXE = registry.registerItem(_id("red_matter_axe"), () -> new AlchemicalAxe(AlchemicalToolMaterials.RED_MATTER, 5.0f, -3.0f, ChargeItemSettings.of(_id("red_matter_axe")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        RED_MATTER_SHOVEL = registry.registerItem(_id("red_matter_shovel"), () -> new AlchemicalShovel(AlchemicalToolMaterials.RED_MATTER, 1.5F, -3f, ChargeItemSettings.of(_id("red_matter_shovel")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        RED_MATTER_HOE = registry.registerItem(_id("red_matter_hoe"), () -> new AlchemicalHoe(AlchemicalToolMaterials.RED_MATTER, -3, 0f, ChargeItemSettings.of(_id("red_matter_hoe")).addGroup(ItemGroups.ITEM_ALCHEMY)));
    }
}
