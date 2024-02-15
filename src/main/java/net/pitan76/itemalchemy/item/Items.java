package net.pitan76.itemalchemy.item;

import net.pitan76.itemalchemy.util.ChargeItemSettings;
import net.pitan76.itemalchemy.block.Blocks;
import net.pitan76.mcpitanlib.api.item.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.item.ExtendItem;
import net.pitan76.mcpitanlib.api.util.ItemUtil;
import net.minecraft.item.Item;
import net.minecraft.util.Rarity;
import net.pitan76.mcpitanlib.api.registry.result.RegistryResult;

import static net.pitan76.itemalchemy.ItemAlchemy.id;
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
    //public static RegistryResult<Item> EMC_CONDENSER_MK2;
    public static RegistryResult<Item> EMC_REPEATER;
    public static RegistryResult<Item> AEGU;
    public static RegistryResult<Item> ADVANCED_AEGU;
    public static RegistryResult<Item> ULTIMATE_AEGU;

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
        PHILOSOPHER_STONE = registry.registerItem(id("philosopher_stone"), () -> new PhilosopherStone(ChargeItemSettings.of().addGroup(ItemGroups.ITEM_ALCHEMY, id("philosopher_stone"))));
        ALCHEMY_TABLE = registry.registerItem(id("alchemy_table"), () -> ItemUtil.ofBlock(Blocks.ALCHEMY_TABLE.getOrNull(), CompatibleItemSettings.of().addGroup(ItemGroups.ITEM_ALCHEMY, id("alchemy_table"))));
        TOME_OF_KNOWLEDGE = registry.registerItem(id("tome_of_knowledge"), () -> new TomeOfKnowledge(CompatibleItemSettings.of().rarity(Rarity.EPIC).maxCount(1).addGroup(ItemGroups.ITEM_ALCHEMY, id("tome_of_knowledge"))));
        EMC_COLLECTOR_MK1 = registry.registerItem(id("emc_collector_mk1"), () -> ItemUtil.ofBlock(Blocks.EMC_COLLECTOR_MK1.getOrNull(), CompatibleItemSettings.of().addGroup(ItemGroups.ITEM_ALCHEMY, id("emc_collector_mk1"))));
        EMC_COLLECTOR_MK2 = registry.registerItem(id("emc_collector_mk2"), () -> ItemUtil.ofBlock(Blocks.EMC_COLLECTOR_MK2.getOrNull(), CompatibleItemSettings.of().addGroup(ItemGroups.ITEM_ALCHEMY, id("emc_collector_mk2"))));
        EMC_COLLECTOR_MK3 = registry.registerItem(id("emc_collector_mk3"), () -> ItemUtil.ofBlock(Blocks.EMC_COLLECTOR_MK3.getOrNull(), CompatibleItemSettings.of().addGroup(ItemGroups.ITEM_ALCHEMY, id("emc_collector_mk3"))));
        EMC_COLLECTOR_MK4 = registry.registerItem(id("emc_collector_mk4"), () -> ItemUtil.ofBlock(Blocks.EMC_COLLECTOR_MK4.getOrNull(), CompatibleItemSettings.of().addGroup(ItemGroups.ITEM_ALCHEMY, id("emc_collector_mk4"))));
        EMC_COLLECTOR_MK5 = registry.registerItem(id("emc_collector_mk5"), () -> ItemUtil.ofBlock(Blocks.EMC_COLLECTOR_MK5.getOrNull(), CompatibleItemSettings.of().addGroup(ItemGroups.ITEM_ALCHEMY, id("emc_collector_mk5"))));
        ALCHEMY_CHEST = registry.registerItem(id("alchemy_chest"), () -> ItemUtil.ofBlock(Blocks.ALCHEMY_CHEST.getOrNull(), CompatibleItemSettings.of().addGroup(ItemGroups.ITEM_ALCHEMY, id("alchemy_chest"))));
        EMC_CONDENSER = registry.registerItem(id("emc_condenser"), () -> ItemUtil.ofBlock(Blocks.EMC_CONDENSER.getOrNull(), CompatibleItemSettings.of().addGroup(ItemGroups.ITEM_ALCHEMY, id("emc_condenser"))));
        //EMC_CONDENSER_MK2 = registry.registerItem(id("emc_condenser_mk2"), () -> ItemUtil.ofBlock(Blocks.EMC_CONDENSER_MK2.getOrNull(), CompatibleItemSettings.of()));//.addGroup(ItemGroups.ITEM_ALCHEMY, id("emc_condenser_mk2"))));
        EMC_REPEATER = registry.registerItem(id("emc_repeater"), () -> ItemUtil.ofBlock(Blocks.EMC_REPEATER.getOrNull(), CompatibleItemSettings.of().addGroup(ItemGroups.ITEM_ALCHEMY, id("emc_repeater"))));
        AEGU = registry.registerItem(id("aegu"), () -> ItemUtil.ofBlock(Blocks.AEGU.getOrNull(), CompatibleItemSettings.of().addGroup(ItemGroups.ITEM_ALCHEMY, id("aegu"))));
        ADVANCED_AEGU = registry.registerItem(id("advanced_aegu"), () -> ItemUtil.ofBlock(Blocks.ADVANCED_AEGU.getOrNull(), CompatibleItemSettings.of().addGroup(ItemGroups.ITEM_ALCHEMY, id("advanced_aegu"))));
        ULTIMATE_AEGU = registry.registerItem(id("ultimate_aegu"), () -> ItemUtil.ofBlock(Blocks.ULTIMATE_AEGU.getOrNull(), CompatibleItemSettings.of().addGroup(ItemGroups.ITEM_ALCHEMY, id("ultimate_aegu"))));
        ALCHEMY_PAD = registry.registerItem(id("alchemy_pad"), () -> new AlchemyPad(CompatibleItemSettings.of().addGroup(ItemGroups.ITEM_ALCHEMY, id("alchemy_pad")).maxCount(1)));

        ALCHEMICAL_FUEL = registry.registerItem(id("alchemical_fuel"), () -> new ExtendItem(CompatibleItemSettings.of().addGroup(ItemGroups.ITEM_ALCHEMY, id("alchemical_fuel"))));
        MOBIUS_FUEL = registry.registerItem(id("mobius_fuel"), () -> new ExtendItem(CompatibleItemSettings.of().addGroup(ItemGroups.ITEM_ALCHEMY, id("mobius_fuel"))));
        AETERNALIS_FUEL = registry.registerItem(id("aeternalis_fuel"), () -> new ExtendItem(CompatibleItemSettings.of().addGroup(ItemGroups.ITEM_ALCHEMY, id("aeternalis_fuel"))));
        LOW_COVALENCE_DUST = registry.registerItem(id("low_covalence_dust"), () -> new ExtendItem(CompatibleItemSettings.of().addGroup(ItemGroups.ITEM_ALCHEMY, id("low_covalence_dust"))));
        MIDDLE_COVALENCE_DUST = registry.registerItem(id("middle_covalence_dust"), () -> new ExtendItem(CompatibleItemSettings.of().addGroup(ItemGroups.ITEM_ALCHEMY, id("middle_covalence_dust"))));
        HIGH_COVALENCE_DUST = registry.registerItem(id("high_covalence_dust"), () -> new ExtendItem(CompatibleItemSettings.of().addGroup(ItemGroups.ITEM_ALCHEMY, id("high_covalence_dust"))));
        DARK_MATTER = registry.registerItem(id("dark_matter"), () -> new ExtendItem(CompatibleItemSettings.of().addGroup(ItemGroups.ITEM_ALCHEMY, id("dark_matter"))));
        RED_MATTER = registry.registerItem(id("red_matter"), () -> new ExtendItem(CompatibleItemSettings.of().addGroup(ItemGroups.ITEM_ALCHEMY, id("red_matter"))));
        DARK_MATTER_BLOCK = registry.registerItem(id("dark_matter_block"), () -> ItemUtil.ofBlock(Blocks.DARK_MATTER_BLOCK.getOrNull(), CompatibleItemSettings.of().addGroup(ItemGroups.ITEM_ALCHEMY, id("dark_matter_block"))));
        RED_MATTER_BLOCK = registry.registerItem(id("red_matter_block"), () -> ItemUtil.ofBlock(Blocks.RED_MATTER_BLOCK.getOrNull(), CompatibleItemSettings.of().addGroup(ItemGroups.ITEM_ALCHEMY, id("red_matter_block"))));

        DARK_MATTER_SWORD = registry.registerItem(id("dark_matter_sword"), () -> new AlchemicalSword(AlchemicalToolMaterials.DARK_MATTER, 3, -2.4f, ChargeItemSettings.of().addGroup(ItemGroups.ITEM_ALCHEMY, id("dark_matter_sword"))));
        DARK_MATTER_PICKAXE = registry.registerItem(id("dark_matter_pickaxe"), () -> new AlchemicalPickaxe(AlchemicalToolMaterials.DARK_MATTER, 1, -2.8f, ChargeItemSettings.of().addGroup(ItemGroups.ITEM_ALCHEMY, id("dark_matter_pickaxe"))));
        DARK_MATTER_AXE = registry.registerItem(id("dark_matter_axe"), () -> new AlchemicalAxe(AlchemicalToolMaterials.DARK_MATTER, 5.0f, -3.0f, ChargeItemSettings.of().addGroup(ItemGroups.ITEM_ALCHEMY, id("dark_matter_axe"))));
        DARK_MATTER_SHOVEL = registry.registerItem(id("dark_matter_shovel"), () -> new AlchemicalShovel(AlchemicalToolMaterials.DARK_MATTER, 1.5F, -3f, ChargeItemSettings.of().addGroup(ItemGroups.ITEM_ALCHEMY, id("dark_matter_shovel"))));
        DARK_MATTER_HOE = registry.registerItem(id("dark_matter_hoe"), () -> new AlchemicalHoe(AlchemicalToolMaterials.DARK_MATTER, -3, 0f, ChargeItemSettings.of().addGroup(ItemGroups.ITEM_ALCHEMY, id("dark_matter_hoe"))));
        RED_MATTER_SWORD = registry.registerItem(id("red_matter_sword"), () -> new AlchemicalSword(AlchemicalToolMaterials.RED_MATTER, 3, -2.4f, ChargeItemSettings.of().addGroup(ItemGroups.ITEM_ALCHEMY, id("red_matter_sword"))));
        RED_MATTER_PICKAXE = registry.registerItem(id("red_matter_pickaxe"), () -> new AlchemicalPickaxe(AlchemicalToolMaterials.RED_MATTER, 1, -2.8f, ChargeItemSettings.of().addGroup(ItemGroups.ITEM_ALCHEMY, id("red_matter_pickaxe"))));
        RED_MATTER_AXE = registry.registerItem(id("red_matter_axe"), () -> new AlchemicalAxe(AlchemicalToolMaterials.RED_MATTER, 5.0f, -3.0f, ChargeItemSettings.of().addGroup(ItemGroups.ITEM_ALCHEMY, id("red_matter_axe"))));
        RED_MATTER_SHOVEL = registry.registerItem(id("red_matter_shovel"), () -> new AlchemicalShovel(AlchemicalToolMaterials.RED_MATTER, 1.5F, -3f, ChargeItemSettings.of().addGroup(ItemGroups.ITEM_ALCHEMY, id("red_matter_shovel"))));
        RED_MATTER_HOE = registry.registerItem(id("red_matter_hoe"), () -> new AlchemicalHoe(AlchemicalToolMaterials.RED_MATTER, -3, 0f, ChargeItemSettings.of().addGroup(ItemGroups.ITEM_ALCHEMY, id("red_matter_hoe"))));
        //net.minecraft.item.Items.DIAMOND_SWORD
    }
}
