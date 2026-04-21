package net.pitan76.itemalchemy.item;

import net.pitan76.itemalchemy.block.Blocks;
import net.pitan76.itemalchemy.util.ChargeItemSettings;
import net.pitan76.mcpitanlib.api.item.v2.CompatItem;
import net.pitan76.mcpitanlib.api.item.ArmorEquipmentType;
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.util.CompatRarity;
import net.pitan76.mcpitanlib.midohra.item.ItemWrapper;
import net.pitan76.mcpitanlib.midohra.item.TypedItemWrapper;

import static net.pitan76.itemalchemy.ItemAlchemy._id;
import static net.pitan76.itemalchemy.ItemAlchemy.registry;

public class Items {

    public static TypedItemWrapper<PhilosopherStone> PHILOSOPHER_STONE;
    public static TypedItemWrapper<AlchemyPad> ALCHEMY_PAD;

    public static TypedItemWrapper<TomeOfKnowledge> TOME_OF_KNOWLEDGE;

    public static ItemWrapper ALCHEMY_TABLE;
    public static ItemWrapper EMC_COLLECTOR_MK1;
    public static ItemWrapper EMC_COLLECTOR_MK2;
    public static ItemWrapper EMC_COLLECTOR_MK3;
    public static ItemWrapper EMC_COLLECTOR_MK4;
    public static ItemWrapper EMC_COLLECTOR_MK5;
    public static ItemWrapper ALCHEMY_CHEST;
    public static ItemWrapper EMC_CONDENSER;
    public static ItemWrapper EMC_CONDENSER_MK2;
    public static ItemWrapper EMC_REPEATER;
    public static ItemWrapper AEGU;
    public static ItemWrapper ADVANCED_AEGU;
    public static ItemWrapper ULTIMATE_AEGU;
    public static ItemWrapper EMC_IMPORTER;
    public static ItemWrapper EMC_EXPORTER;
    public static ItemWrapper EMC_CABLE;
    public static ItemWrapper EMC_BATTERY;

    public static ItemWrapper INTERDICTION_TORCH;

    public static ItemWrapper DM_PEDESTAL;
    public static TypedItemWrapper<WatchOfFlowingTime> WATCH_OF_FLOWING_TIME;

    // Material
    public static TypedItemWrapper<CompatItem> ALCHEMICAL_FUEL;
    public static TypedItemWrapper<CompatItem> MOBIUS_FUEL;
    public static TypedItemWrapper<CompatItem> AETERNALIS_FUEL;
    public static TypedItemWrapper<CompatItem> LOW_COVALENCE_DUST;
    public static TypedItemWrapper<CompatItem> MIDDLE_COVALENCE_DUST;
    public static TypedItemWrapper<CompatItem> HIGH_COVALENCE_DUST;
    public static TypedItemWrapper<CompatItem> DARK_MATTER;
    public static TypedItemWrapper<CompatItem> RED_MATTER;
    public static ItemWrapper DARK_MATTER_BLOCK;
    public static ItemWrapper RED_MATTER_BLOCK;

    public static TypedItemWrapper<Wrench> IA_WRENCH;
    public static TypedItemWrapper<DiviningRod> DIVINING_ROD_LV1;
    public static TypedItemWrapper<DiviningRod> DIVINING_ROD_LV2;
    public static TypedItemWrapper<DiviningRod> DIVINING_ROD_LV3;

    public static TypedItemWrapper<Ring> RING;
    public static TypedItemWrapper<BlackHoleBand> BLACK_HOLE_BAND;

    public static ItemWrapper DARK_MATTER_SWORD;
    public static ItemWrapper DARK_MATTER_PICKAXE;
    public static ItemWrapper DARK_MATTER_AXE;
    public static ItemWrapper DARK_MATTER_SHOVEL;
    public static ItemWrapper DARK_MATTER_HOE;

    public static ItemWrapper RED_MATTER_SWORD;
    public static ItemWrapper RED_MATTER_PICKAXE;
    public static ItemWrapper RED_MATTER_AXE;
    public static ItemWrapper RED_MATTER_SHOVEL;
    public static ItemWrapper RED_MATTER_HOE;

    // Dark Matter Armor
    public static ItemWrapper DARK_MATTER_HELMET;
    public static ItemWrapper DARK_MATTER_CHESTPLATE;
    public static ItemWrapper DARK_MATTER_LEGGINGS;
    public static ItemWrapper DARK_MATTER_BOOTS;

    // Red Matter Armor
    public static ItemWrapper RED_MATTER_HELMET;
    public static ItemWrapper RED_MATTER_CHESTPLATE;
    public static ItemWrapper RED_MATTER_LEGGINGS;
    public static ItemWrapper RED_MATTER_BOOTS;

    // Repair Talisman
    public static TypedItemWrapper<RepairTalisman> REPAIR_TALISMAN;

    // Klein Stars
    public static TypedItemWrapper<KleinStar> KLEIN_STAR_EIN;
    public static TypedItemWrapper<KleinStar> KLEIN_STAR_ZWEI;
    public static TypedItemWrapper<KleinStar> KLEIN_STAR_DREI;
    public static TypedItemWrapper<KleinStar> KLEIN_STAR_VIER;
    public static TypedItemWrapper<KleinStar> KLEIN_STAR_SPHERE;
    public static TypedItemWrapper<KleinStar> KLEIN_STAR_OMEGA;

    public static void init() {
        PHILOSOPHER_STONE = registry.registerItem(_id("philosopher_stone"), () -> new PhilosopherStone(ChargeItemSettings.of(_id("philosopher_stone")).addGroup(ItemGroups.ITEM_ALCHEMY).rarity(CompatRarity.EPIC)));
        ALCHEMY_TABLE = registry.registerBlockItem(_id("alchemy_table"), Blocks.ALCHEMY_TABLE, CompatibleItemSettings.of(_id("alchemy_table")).addGroup(ItemGroups.ITEM_ALCHEMY));
        TOME_OF_KNOWLEDGE = registry.registerItem(_id("tome_of_knowledge"), () -> new TomeOfKnowledge(CompatibleItemSettings.of(_id("tome_of_knowledge")).rarity(CompatRarity.EPIC).maxCount(1).addGroup(ItemGroups.ITEM_ALCHEMY)));
        EMC_COLLECTOR_MK1 = registry.registerBlockItem(_id("emc_collector_mk1"), Blocks.EMC_COLLECTOR_MK1, CompatibleItemSettings.of(_id("emc_collector_mk1")).addGroup(ItemGroups.ITEM_ALCHEMY));
        EMC_COLLECTOR_MK2 = registry.registerBlockItem(_id("emc_collector_mk2"), Blocks.EMC_COLLECTOR_MK2, CompatibleItemSettings.of(_id("emc_collector_mk2")).addGroup(ItemGroups.ITEM_ALCHEMY));
        EMC_COLLECTOR_MK3 = registry.registerBlockItem(_id("emc_collector_mk3"), Blocks.EMC_COLLECTOR_MK3, CompatibleItemSettings.of(_id("emc_collector_mk3")).addGroup(ItemGroups.ITEM_ALCHEMY));
        EMC_COLLECTOR_MK4 = registry.registerBlockItem(_id("emc_collector_mk4"), Blocks.EMC_COLLECTOR_MK4, CompatibleItemSettings.of(_id("emc_collector_mk4")).addGroup(ItemGroups.ITEM_ALCHEMY));
        EMC_COLLECTOR_MK5 = registry.registerBlockItem(_id("emc_collector_mk5"), Blocks.EMC_COLLECTOR_MK5, CompatibleItemSettings.of(_id("emc_collector_mk5")).addGroup(ItemGroups.ITEM_ALCHEMY));
        ALCHEMY_CHEST = registry.registerBlockItem(_id("alchemy_chest"), Blocks.ALCHEMY_CHEST, CompatibleItemSettings.of(_id("alchemy_chest")).addGroup(ItemGroups.ITEM_ALCHEMY));
        EMC_CONDENSER = registry.registerBlockItem(_id("emc_condenser"), Blocks.EMC_CONDENSER, CompatibleItemSettings.of(_id("emc_condenser")).addGroup(ItemGroups.ITEM_ALCHEMY));
        EMC_CONDENSER_MK2 = registry.registerBlockItem(_id("emc_condenser_mk2"), Blocks.EMC_CONDENSER_MK2, CompatibleItemSettings.of(_id("emc_condenser_mk2")).addGroup(ItemGroups.ITEM_ALCHEMY));
        EMC_REPEATER = registry.registerBlockItem(_id("emc_repeater"), Blocks.EMC_REPEATER, CompatibleItemSettings.of(_id("emc_repeater")).addGroup(ItemGroups.ITEM_ALCHEMY));
        AEGU = registry.registerBlockItem(_id("aegu"), Blocks.AEGU, CompatibleItemSettings.of(_id("aegu")).addGroup(ItemGroups.ITEM_ALCHEMY));
        ADVANCED_AEGU = registry.registerBlockItem(_id("advanced_aegu"), Blocks.ADVANCED_AEGU, CompatibleItemSettings.of(_id("advanced_aegu")).addGroup(ItemGroups.ITEM_ALCHEMY));
        ULTIMATE_AEGU = registry.registerBlockItem(_id("ultimate_aegu"), Blocks.ULTIMATE_AEGU, CompatibleItemSettings.of(_id("ultimate_aegu")).addGroup(ItemGroups.ITEM_ALCHEMY));
        EMC_IMPORTER = registry.registerBlockItem(_id("emc_importer"), Blocks.EMC_IMPORTER, CompatibleItemSettings.of(_id("emc_importer")).addGroup(ItemGroups.ITEM_ALCHEMY));
        EMC_EXPORTER = registry.registerBlockItem(_id("emc_exporter"), Blocks.EMC_EXPORTER, CompatibleItemSettings.of(_id("emc_exporter")).addGroup(ItemGroups.ITEM_ALCHEMY));
        EMC_CABLE = registry.registerBlockItem(_id("emc_cable"), Blocks.EMC_CABLE, CompatibleItemSettings.of(_id("emc_cable")).addGroup(ItemGroups.ITEM_ALCHEMY));
        EMC_BATTERY = registry.registerBlockItem(_id("emc_battery"), Blocks.EMC_BATTERY, CompatibleItemSettings.of(_id("emc_battery")).addGroup(ItemGroups.ITEM_ALCHEMY));

        INTERDICTION_TORCH = registry.registerBlockItem(_id("interdiction_torch"), Blocks.INTERDICTION_TORCH, CompatibleItemSettings.of(_id("interdiction_torch")).addGroup(ItemGroups.ITEM_ALCHEMY));

        DM_PEDESTAL = registry.registerBlockItem(_id("dm_pedestal"), Blocks.DM_PEDESTAL, CompatibleItemSettings.of(_id("dm_pedestal")).addGroup(ItemGroups.ITEM_ALCHEMY));
        WATCH_OF_FLOWING_TIME = registry.registerItem(_id("watch_of_flowing_time"), () -> new WatchOfFlowingTime(CompatibleItemSettings.of(_id("watch_of_flowing_time")).addGroup(ItemGroups.ITEM_ALCHEMY).maxCount(1)));

        ALCHEMY_PAD = registry.registerItem(_id("alchemy_pad"), () -> new AlchemyPad(CompatibleItemSettings.of(_id("alchemy_pad")).addGroup(ItemGroups.ITEM_ALCHEMY).maxCount(1)));

        ALCHEMICAL_FUEL = registry.registerItem(_id("alchemical_fuel"), () -> new CompatItem(CompatibleItemSettings.of(_id("alchemical_fuel")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        MOBIUS_FUEL = registry.registerItem(_id("mobius_fuel"), () -> new CompatItem(CompatibleItemSettings.of(_id("mobius_fuel")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        AETERNALIS_FUEL = registry.registerItem(_id("aeternalis_fuel"), () -> new CompatItem(CompatibleItemSettings.of(_id("aeternalis_fuel")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        LOW_COVALENCE_DUST = registry.registerItem(_id("low_covalence_dust"), () -> new CompatItem(CompatibleItemSettings.of(_id("low_covalence_dust")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        MIDDLE_COVALENCE_DUST = registry.registerItem(_id("middle_covalence_dust"), () -> new CompatItem(CompatibleItemSettings.of(_id("middle_covalence_dust")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        HIGH_COVALENCE_DUST = registry.registerItem(_id("high_covalence_dust"), () -> new CompatItem(CompatibleItemSettings.of(_id("high_covalence_dust")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        DARK_MATTER = registry.registerItem(_id("dark_matter"), () -> new CompatItem(CompatibleItemSettings.of(_id("dark_matter")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        RED_MATTER = registry.registerItem(_id("red_matter"), () -> new CompatItem(CompatibleItemSettings.of(_id("red_matter")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        DARK_MATTER_BLOCK = registry.registerBlockItem(_id("dark_matter_block"), Blocks.DARK_MATTER_BLOCK, CompatibleItemSettings.of(_id("dark_matter_block")).addGroup(ItemGroups.ITEM_ALCHEMY));
        RED_MATTER_BLOCK = registry.registerBlockItem(_id("red_matter_block"), Blocks.RED_MATTER_BLOCK, CompatibleItemSettings.of(_id("red_matter_block")).addGroup(ItemGroups.ITEM_ALCHEMY));

        IA_WRENCH = registry.registerItem(_id("wrench"), () -> new Wrench(CompatibleItemSettings.of(_id("wrench")).addGroup(ItemGroups.ITEM_ALCHEMY).maxCount(1)));
        DIVINING_ROD_LV1 = registry.registerItem(_id("divining_rod_lv1"), () -> new DiviningRod(1, CompatibleItemSettings.of(_id("divining_rod_lv1")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        DIVINING_ROD_LV2 = registry.registerItem(_id("divining_rod_lv2"), () -> new DiviningRod(2, CompatibleItemSettings.of(_id("divining_rod_lv2")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        DIVINING_ROD_LV3 = registry.registerItem(_id("divining_rod_lv3"), () -> new DiviningRod(3, CompatibleItemSettings.of(_id("divining_rod_lv3")).addGroup(ItemGroups.ITEM_ALCHEMY)));

        RING = registry.registerItem(_id("ring"), () -> new Ring(CompatibleItemSettings.of(_id("ring")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        BLACK_HOLE_BAND = registry.registerItem(_id("black_hole_band"), () -> new BlackHoleBand(CompatibleItemSettings.of(_id("black_hole_band")).addGroup(ItemGroups.ITEM_ALCHEMY)));

        DARK_MATTER_SWORD = registry.registerRawItem(_id("dark_matter_sword"), () -> new AlchemicalSword(AlchemicalToolMaterials.DARK_MATTER, 3, -2.4f, ChargeItemSettings.of(_id("dark_matter_sword")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        DARK_MATTER_PICKAXE = registry.registerRawItem(_id("dark_matter_pickaxe"), () -> new AlchemicalPickaxe(AlchemicalToolMaterials.DARK_MATTER, 1, -2.8f, ChargeItemSettings.of(_id("dark_matter_pickaxe")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        DARK_MATTER_AXE = registry.registerRawItem(_id("dark_matter_axe"), () -> new AlchemicalAxe(AlchemicalToolMaterials.DARK_MATTER, 5.0f, -3.0f, ChargeItemSettings.of(_id("dark_matter_axe")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        DARK_MATTER_SHOVEL = registry.registerRawItem(_id("dark_matter_shovel"), () -> new AlchemicalShovel(AlchemicalToolMaterials.DARK_MATTER, 1.5F, -3f, ChargeItemSettings.of(_id("dark_matter_shovel")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        DARK_MATTER_HOE = registry.registerRawItem(_id("dark_matter_hoe"), () -> new AlchemicalHoe(AlchemicalToolMaterials.DARK_MATTER, -3, 0f, ChargeItemSettings.of(_id("dark_matter_hoe")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        RED_MATTER_SWORD = registry.registerRawItem(_id("red_matter_sword"), () -> new AlchemicalSword(AlchemicalToolMaterials.RED_MATTER, 3, -2.4f, ChargeItemSettings.of(_id("red_matter_sword")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        RED_MATTER_PICKAXE = registry.registerRawItem(_id("red_matter_pickaxe"), () -> new AlchemicalPickaxe(AlchemicalToolMaterials.RED_MATTER, 1, -2.8f, ChargeItemSettings.of(_id("red_matter_pickaxe")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        RED_MATTER_AXE = registry.registerRawItem(_id("red_matter_axe"), () -> new AlchemicalAxe(AlchemicalToolMaterials.RED_MATTER, 5.0f, -3.0f, ChargeItemSettings.of(_id("red_matter_axe")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        RED_MATTER_SHOVEL = registry.registerRawItem(_id("red_matter_shovel"), () -> new AlchemicalShovel(AlchemicalToolMaterials.RED_MATTER, 1.5F, -3f, ChargeItemSettings.of(_id("red_matter_shovel")).addGroup(ItemGroups.ITEM_ALCHEMY)));
        RED_MATTER_HOE = registry.registerRawItem(_id("red_matter_hoe"), () -> new AlchemicalHoe(AlchemicalToolMaterials.RED_MATTER, -3, 0f, ChargeItemSettings.of(_id("red_matter_hoe")).addGroup(ItemGroups.ITEM_ALCHEMY)));

        // Dark Matter Armor
        DARK_MATTER_HELMET = registry.registerRawItem(_id("dark_matter_helmet"), () -> new DarkMatterArmor(ArmorEquipmentType.HEAD, CompatibleItemSettings.of(_id("dark_matter_helmet")).addGroup(ItemGroups.ITEM_ALCHEMY).maxCount(1)));
        DARK_MATTER_CHESTPLATE = registry.registerRawItem(_id("dark_matter_chestplate"), () -> new DarkMatterArmor(ArmorEquipmentType.CHEST, CompatibleItemSettings.of(_id("dark_matter_chestplate")).addGroup(ItemGroups.ITEM_ALCHEMY).maxCount(1)));
        DARK_MATTER_LEGGINGS = registry.registerRawItem(_id("dark_matter_leggings"), () -> new DarkMatterArmor(ArmorEquipmentType.LEGS, CompatibleItemSettings.of(_id("dark_matter_leggings")).addGroup(ItemGroups.ITEM_ALCHEMY).maxCount(1)));
        DARK_MATTER_BOOTS = registry.registerRawItem(_id("dark_matter_boots"), () -> new DarkMatterArmor(ArmorEquipmentType.FEET, CompatibleItemSettings.of(_id("dark_matter_boots")).addGroup(ItemGroups.ITEM_ALCHEMY).maxCount(1)));

        // Red Matter Armor
        RED_MATTER_HELMET = registry.registerRawItem(_id("red_matter_helmet"), () -> new RedMatterArmor(ArmorEquipmentType.HEAD, CompatibleItemSettings.of(_id("red_matter_helmet")).addGroup(ItemGroups.ITEM_ALCHEMY).maxCount(1)));
        RED_MATTER_CHESTPLATE = registry.registerRawItem(_id("red_matter_chestplate"), () -> new RedMatterArmor(ArmorEquipmentType.CHEST, CompatibleItemSettings.of(_id("red_matter_chestplate")).addGroup(ItemGroups.ITEM_ALCHEMY).maxCount(1)));
        RED_MATTER_LEGGINGS = registry.registerRawItem(_id("red_matter_leggings"), () -> new RedMatterArmor(ArmorEquipmentType.LEGS, CompatibleItemSettings.of(_id("red_matter_leggings")).addGroup(ItemGroups.ITEM_ALCHEMY).maxCount(1)));
        RED_MATTER_BOOTS = registry.registerRawItem(_id("red_matter_boots"), () -> new RedMatterArmor(ArmorEquipmentType.FEET, CompatibleItemSettings.of(_id("red_matter_boots")).addGroup(ItemGroups.ITEM_ALCHEMY).maxCount(1)));

        // Repair Talisman
        REPAIR_TALISMAN = registry.registerItem(_id("repair_talisman"), () -> new RepairTalisman(CompatibleItemSettings.of(_id("repair_talisman")).addGroup(ItemGroups.ITEM_ALCHEMY).maxCount(1)));

        // Klein Stars
        KLEIN_STAR_EIN = registry.registerItem(_id("klein_star_ein"), () -> new KleinStar(KleinStar.Tier.EIN, CompatibleItemSettings.of(_id("klein_star_ein")).addGroup(ItemGroups.ITEM_ALCHEMY).maxCount(1)));
        KLEIN_STAR_ZWEI = registry.registerItem(_id("klein_star_zwei"), () -> new KleinStar(KleinStar.Tier.ZWEI, CompatibleItemSettings.of(_id("klein_star_zwei")).addGroup(ItemGroups.ITEM_ALCHEMY).maxCount(1)));
        KLEIN_STAR_DREI = registry.registerItem(_id("klein_star_drei"), () -> new KleinStar(KleinStar.Tier.DREI, CompatibleItemSettings.of(_id("klein_star_drei")).addGroup(ItemGroups.ITEM_ALCHEMY).maxCount(1)));
        KLEIN_STAR_VIER = registry.registerItem(_id("klein_star_vier"), () -> new KleinStar(KleinStar.Tier.VIER, CompatibleItemSettings.of(_id("klein_star_vier")).addGroup(ItemGroups.ITEM_ALCHEMY).maxCount(1)));
        KLEIN_STAR_SPHERE = registry.registerItem(_id("klein_star_sphere"), () -> new KleinStar(KleinStar.Tier.SPHERE, CompatibleItemSettings.of(_id("klein_star_sphere")).addGroup(ItemGroups.ITEM_ALCHEMY).maxCount(1)));
        KLEIN_STAR_OMEGA = registry.registerItem(_id("klein_star_omega"), () -> new KleinStar(KleinStar.Tier.OMEGA, CompatibleItemSettings.of(_id("klein_star_omega")).addGroup(ItemGroups.ITEM_ALCHEMY).maxCount(1)));
    }
}
