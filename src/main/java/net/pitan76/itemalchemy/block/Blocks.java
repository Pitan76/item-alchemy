package net.pitan76.itemalchemy.block;

import net.minecraft.block.Block;
import net.pitan76.mcpitanlib.api.block.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.api.block.CompatibleMaterial;
import net.pitan76.mcpitanlib.api.block.ExtendBlock;
import net.pitan76.mcpitanlib.api.registry.result.RegistryResult;

import static net.pitan76.itemalchemy.ItemAlchemy._id;
import static net.pitan76.itemalchemy.ItemAlchemy.registry;

public class Blocks {

    public static RegistryResult<Block> ALCHEMY_TABLE;
    public static RegistryResult<Block> EMC_COLLECTOR_MK1;
    public static RegistryResult<Block> EMC_COLLECTOR_MK2;
    public static RegistryResult<Block> EMC_COLLECTOR_MK3;
    public static RegistryResult<Block> EMC_COLLECTOR_MK4;
    public static RegistryResult<Block> EMC_COLLECTOR_MK5;
    public static RegistryResult<Block> ALCHEMY_CHEST;
    public static RegistryResult<Block> EMC_CONDENSER;
    public static RegistryResult<Block> EMC_CONDENSER_MK2;
    public static RegistryResult<Block> EMC_REPEATER;
    public static RegistryResult<Block> AEGU;
    public static RegistryResult<Block> ADVANCED_AEGU;
    public static RegistryResult<Block> ULTIMATE_AEGU;

    public static RegistryResult<Block> DARK_MATTER_BLOCK;
    public static RegistryResult<Block> RED_MATTER_BLOCK;

    public static RegistryResult<Block> EMC_IMPORTER;
    public static RegistryResult<Block> EMC_EXPORTER;

    public static RegistryResult<Block> EMC_CABLE;

    public static RegistryResult<Block> EMC_BATTERY;

    public static void init() {
        ALCHEMY_TABLE = registry.registerBlock(_id("alchemy_table"), AlchemyTable::new);
        EMC_COLLECTOR_MK1 = registry.registerBlock(_id("emc_collector_mk1"), () -> new EMCCollector(10000));
        EMC_COLLECTOR_MK2 = registry.registerBlock(_id("emc_collector_mk2"), () -> new EMCCollector(30000));
        EMC_COLLECTOR_MK3 = registry.registerBlock(_id("emc_collector_mk3"), () -> new EMCCollector(60000));
        EMC_COLLECTOR_MK4 = registry.registerBlock(_id("emc_collector_mk4"), () -> new EMCCollector(250000));
        EMC_COLLECTOR_MK5 = registry.registerBlock(_id("emc_collector_mk5"), () -> new EMCCollector(2000000));
        ALCHEMY_CHEST = registry.registerBlock(_id("alchemy_chest"), AlchemyChest::new);
        EMC_CONDENSER = registry.registerBlock(_id("emc_condenser"), EMCCondenser::new);
        EMC_CONDENSER_MK2 = registry.registerBlock(_id("emc_condenser_mk2"), EMCCondenserMK2::new);
        EMC_REPEATER = registry.registerBlock(_id("emc_repeater"), EMCRepeater::new);
        AEGU = registry.registerBlock(_id("aegu"), () -> new AEGUBlock(40 / 10));
        ADVANCED_AEGU = registry.registerBlock(_id("advanced_aegu"), () -> new AEGUBlock(1000 / 10));
        ULTIMATE_AEGU = registry.registerBlock(_id("ultimate_aegu"), () -> new AEGUBlock(20000 / 10));

        DARK_MATTER_BLOCK = registry.registerBlock(_id("dark_matter_block"), () -> new ExtendBlock(CompatibleBlockSettings.of(CompatibleMaterial.STONE).strength(2.0f, 5.0f)));
        RED_MATTER_BLOCK = registry.registerBlock(_id("red_matter_block"), () -> new ExtendBlock(CompatibleBlockSettings.of(CompatibleMaterial.STONE).strength(2.0f, 5.0f)));

        EMC_IMPORTER = registry.registerBlock(_id("emc_importer"), () -> new EMCImporter(CompatibleBlockSettings.of(CompatibleMaterial.STONE).strength(2.0f, 5.0f)));
        EMC_EXPORTER = registry.registerBlock(_id("emc_exporter"), () -> new EMCExporter(CompatibleBlockSettings.of(CompatibleMaterial.STONE).strength(2.0f, 5.0f)));

        EMC_CABLE = registry.registerBlock(_id("emc_cable"), () -> new EMCCable(CompatibleBlockSettings.of(CompatibleMaterial.STONE).strength(2.0f, 5.0f)));
        EMC_BATTERY = registry.registerBlock(_id("emc_battery"), () -> new EMCBattery(100_000));
    }
}
