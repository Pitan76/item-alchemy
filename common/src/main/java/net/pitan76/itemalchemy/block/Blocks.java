package net.pitan76.itemalchemy.block;

import net.pitan76.itemalchemy.block.pedestal.DMPedestal;
import net.pitan76.mcpitanlib.api.block.CompatibleMaterial;
import net.pitan76.mcpitanlib.api.block.v2.CompatBlock;
import net.pitan76.mcpitanlib.api.block.v2.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.midohra.block.SupplierTypedBlockWrapper;
import net.pitan76.mcpitanlib.midohra.block.TypedBlockWrapper;

import static net.pitan76.itemalchemy.ItemAlchemy._id;
import static net.pitan76.itemalchemy.ItemAlchemy.registry;

public class Blocks {

    public static SupplierTypedBlockWrapper<AlchemyTable> ALCHEMY_TABLE;
    public static SupplierTypedBlockWrapper<EMCCollector> EMC_COLLECTOR_MK1;
    public static SupplierTypedBlockWrapper<EMCCollector> EMC_COLLECTOR_MK2;
    public static SupplierTypedBlockWrapper<EMCCollector> EMC_COLLECTOR_MK3;
    public static SupplierTypedBlockWrapper<EMCCollector> EMC_COLLECTOR_MK4;
    public static SupplierTypedBlockWrapper<EMCCollector> EMC_COLLECTOR_MK5;
    public static SupplierTypedBlockWrapper<AlchemyChest> ALCHEMY_CHEST;
    public static SupplierTypedBlockWrapper<EMCCondenser> EMC_CONDENSER;
    public static SupplierTypedBlockWrapper<EMCCondenserMK2> EMC_CONDENSER_MK2;
    public static SupplierTypedBlockWrapper<EMCRepeater> EMC_REPEATER;
    public static SupplierTypedBlockWrapper<AEGUBlock> AEGU;
    public static SupplierTypedBlockWrapper<AEGUBlock> ADVANCED_AEGU;
    public static SupplierTypedBlockWrapper<AEGUBlock> ULTIMATE_AEGU;

    public static TypedBlockWrapper<CompatBlock> DARK_MATTER_BLOCK;
    public static TypedBlockWrapper<CompatBlock> RED_MATTER_BLOCK;

    public static SupplierTypedBlockWrapper<EMCImporter> EMC_IMPORTER;
    public static SupplierTypedBlockWrapper<EMCExporter> EMC_EXPORTER;

    public static TypedBlockWrapper<EMCCable> EMC_CABLE;

    public static SupplierTypedBlockWrapper<EMCBattery> EMC_BATTERY;

    public static SupplierTypedBlockWrapper<InterdictionTorch> INTERDICTION_TORCH;

    public static SupplierTypedBlockWrapper<DMPedestal> DM_PEDESTAL;

    public static void init() {
        ALCHEMY_TABLE = (SupplierTypedBlockWrapper<AlchemyTable>) registry.registerBlock(_id("alchemy_table"), () -> new AlchemyTable(_id("alchemy_table")));
        EMC_COLLECTOR_MK1 = (SupplierTypedBlockWrapper<EMCCollector>) registry.registerBlock(_id("emc_collector_mk1"), () -> new EMCCollector(_id("emc_collector_mk1"), 10000));
        EMC_COLLECTOR_MK2 = (SupplierTypedBlockWrapper<EMCCollector>) registry.registerBlock(_id("emc_collector_mk2"), () -> new EMCCollector(_id("emc_collector_mk2"), 30000));
        EMC_COLLECTOR_MK3 = (SupplierTypedBlockWrapper<EMCCollector>) registry.registerBlock(_id("emc_collector_mk3"), () -> new EMCCollector(_id("emc_collector_mk3"), 60000));
        EMC_COLLECTOR_MK4 = (SupplierTypedBlockWrapper<EMCCollector>) registry.registerBlock(_id("emc_collector_mk4"), () -> new EMCCollector(_id("emc_collector_mk4"), 250000));
        EMC_COLLECTOR_MK5 = (SupplierTypedBlockWrapper<EMCCollector>) registry.registerBlock(_id("emc_collector_mk5"), () -> new EMCCollector(_id("emc_collector_mk5"), 2000000));
        ALCHEMY_CHEST = (SupplierTypedBlockWrapper<AlchemyChest>) registry.registerBlock(_id("alchemy_chest"), () -> new AlchemyChest(_id("alchemy_chest")));
        EMC_CONDENSER = (SupplierTypedBlockWrapper<EMCCondenser>) registry.registerBlock(_id("emc_condenser"), () -> new EMCCondenser(_id("emc_condenser")));
        EMC_CONDENSER_MK2 = (SupplierTypedBlockWrapper<EMCCondenserMK2>) registry.registerBlock(_id("emc_condenser_mk2"), () -> new EMCCondenserMK2(_id("emc_condenser_mk2")));
        EMC_REPEATER = (SupplierTypedBlockWrapper<EMCRepeater>) registry.registerBlock(_id("emc_repeater"), () -> new EMCRepeater(_id("emc_repeater")));
        AEGU = (SupplierTypedBlockWrapper<AEGUBlock>) registry.registerBlock(_id("aegu"), () -> new AEGUBlock(_id("aegu"), 40 / 10));
        ADVANCED_AEGU = (SupplierTypedBlockWrapper<AEGUBlock>) registry.registerBlock(_id("advanced_aegu"), () -> new AEGUBlock(_id("advanced_aegu"), 1000 / 10));
        ULTIMATE_AEGU = (SupplierTypedBlockWrapper<AEGUBlock>) registry.registerBlock(_id("ultimate_aegu"), () -> new AEGUBlock(_id("ultimate_aegu"), 20000 / 10));

        DARK_MATTER_BLOCK = registry.registerBlock(_id("dark_matter_block"), () -> new CompatBlock(CompatibleBlockSettings.of(_id("dark_matter_block"), CompatibleMaterial.STONE).strength(2.0f, 5.0f)));
        RED_MATTER_BLOCK = registry.registerBlock(_id("red_matter_block"), () -> new CompatBlock(CompatibleBlockSettings.of(_id("red_matter_block"), CompatibleMaterial.STONE).strength(2.0f, 5.0f)));

        EMC_IMPORTER = (SupplierTypedBlockWrapper<EMCImporter>) registry.registerBlock(_id("emc_importer"), () -> new EMCImporter(CompatibleBlockSettings.of(_id("emc_importer"), CompatibleMaterial.STONE).strength(2.0f, 5.0f)));
        EMC_EXPORTER = (SupplierTypedBlockWrapper<EMCExporter>) registry.registerBlock(_id("emc_exporter"), () -> new EMCExporter(CompatibleBlockSettings.of(_id("emc_exporter"), CompatibleMaterial.STONE).strength(2.0f, 5.0f)));

        EMC_CABLE = registry.registerBlock(_id("emc_cable"), () -> new EMCCable(CompatibleBlockSettings.of(_id("emc_cable"), CompatibleMaterial.STONE).strength(2.0f, 5.0f)));
        EMC_BATTERY = (SupplierTypedBlockWrapper<EMCBattery>) registry.registerBlock(_id("emc_battery"), () -> new EMCBattery(_id("emc_battery"), 100_000));

        INTERDICTION_TORCH = (SupplierTypedBlockWrapper<InterdictionTorch>) registry.registerBlock(_id("interdiction_torch"), () -> new InterdictionTorch(_id("interdiction_torch")));

        DM_PEDESTAL = (SupplierTypedBlockWrapper<DMPedestal>) registry.registerBlock(_id("dm_pedestal"), () -> new DMPedestal(_id("dm_pedestal")));
    }
}
