package net.pitan76.itemalchemy.block;

import net.pitan76.itemalchemy.block.pedestal.DMPedestal;
import net.pitan76.mcpitanlib.api.block.CompatibleMaterial;
import net.pitan76.mcpitanlib.api.block.v2.CompatBlock;
import net.pitan76.mcpitanlib.api.block.v2.CompatibleBlockSettings;
import net.pitan76.mcpitanlib.midohra.block.ITypedBlockWrapper;
import net.pitan76.mcpitanlib.midohra.block.SupplierITypedBlockWrapper;

import static net.pitan76.itemalchemy.ItemAlchemy._id;
import static net.pitan76.itemalchemy.ItemAlchemy.registry;

public class Blocks {

    public static SupplierITypedBlockWrapper<AlchemyTable> ALCHEMY_TABLE;
    public static SupplierITypedBlockWrapper<EMCCollector> EMC_COLLECTOR_MK1;
    public static SupplierITypedBlockWrapper<EMCCollector> EMC_COLLECTOR_MK2;
    public static SupplierITypedBlockWrapper<EMCCollector> EMC_COLLECTOR_MK3;
    public static SupplierITypedBlockWrapper<EMCCollector> EMC_COLLECTOR_MK4;
    public static SupplierITypedBlockWrapper<EMCCollector> EMC_COLLECTOR_MK5;
    public static SupplierITypedBlockWrapper<AlchemyChest> ALCHEMY_CHEST;
    public static SupplierITypedBlockWrapper<EMCCondenser> EMC_CONDENSER;
    public static SupplierITypedBlockWrapper<EMCCondenserMK2> EMC_CONDENSER_MK2;
    public static SupplierITypedBlockWrapper<EMCRepeater> EMC_REPEATER;
    public static SupplierITypedBlockWrapper<AEGUBlock> AEGU;
    public static SupplierITypedBlockWrapper<AEGUBlock> ADVANCED_AEGU;
    public static SupplierITypedBlockWrapper<AEGUBlock> ULTIMATE_AEGU;

    public static ITypedBlockWrapper<CompatBlock> DARK_MATTER_BLOCK;
    public static ITypedBlockWrapper<CompatBlock> RED_MATTER_BLOCK;

    public static SupplierITypedBlockWrapper<EMCImporter> EMC_IMPORTER;
    public static SupplierITypedBlockWrapper<EMCExporter> EMC_EXPORTER;

    public static ITypedBlockWrapper<EMCCable> EMC_CABLE;

    public static SupplierITypedBlockWrapper<EMCBattery> EMC_BATTERY;

    public static SupplierITypedBlockWrapper<InterdictionTorch> INTERDICTION_TORCH;

    public static SupplierITypedBlockWrapper<DMPedestal> DM_PEDESTAL;

    public static void init() {
        ALCHEMY_TABLE = (SupplierITypedBlockWrapper<AlchemyTable>) registry.registerBlock(_id("alchemy_table"), () -> new AlchemyTable(_id("alchemy_table")));
        EMC_COLLECTOR_MK1 = (SupplierITypedBlockWrapper<EMCCollector>) registry.registerBlock(_id("emc_collector_mk1"), () -> new EMCCollector(_id("emc_collector_mk1"), 10000));
        EMC_COLLECTOR_MK2 = (SupplierITypedBlockWrapper<EMCCollector>) registry.registerBlock(_id("emc_collector_mk2"), () -> new EMCCollector(_id("emc_collector_mk2"), 30000));
        EMC_COLLECTOR_MK3 = (SupplierITypedBlockWrapper<EMCCollector>) registry.registerBlock(_id("emc_collector_mk3"), () -> new EMCCollector(_id("emc_collector_mk3"), 60000));
        EMC_COLLECTOR_MK4 = (SupplierITypedBlockWrapper<EMCCollector>) registry.registerBlock(_id("emc_collector_mk4"), () -> new EMCCollector(_id("emc_collector_mk4"), 250000));
        EMC_COLLECTOR_MK5 = (SupplierITypedBlockWrapper<EMCCollector>) registry.registerBlock(_id("emc_collector_mk5"), () -> new EMCCollector(_id("emc_collector_mk5"), 2000000));
        ALCHEMY_CHEST = (SupplierITypedBlockWrapper<AlchemyChest>) registry.registerBlock(_id("alchemy_chest"), () -> new AlchemyChest(_id("alchemy_chest")));
        EMC_CONDENSER = (SupplierITypedBlockWrapper<EMCCondenser>) registry.registerBlock(_id("emc_condenser"), () -> new EMCCondenser(_id("emc_condenser")));
        EMC_CONDENSER_MK2 = (SupplierITypedBlockWrapper<EMCCondenserMK2>) registry.registerBlock(_id("emc_condenser_mk2"), () -> new EMCCondenserMK2(_id("emc_condenser_mk2")));
        EMC_REPEATER = (SupplierITypedBlockWrapper<EMCRepeater>) registry.registerBlock(_id("emc_repeater"), () -> new EMCRepeater(_id("emc_repeater")));
        AEGU = (SupplierITypedBlockWrapper<AEGUBlock>) registry.registerBlock(_id("aegu"), () -> new AEGUBlock(_id("aegu"), 40 / 10));
        ADVANCED_AEGU = (SupplierITypedBlockWrapper<AEGUBlock>) registry.registerBlock(_id("advanced_aegu"), () -> new AEGUBlock(_id("advanced_aegu"), 1000 / 10));
        ULTIMATE_AEGU = (SupplierITypedBlockWrapper<AEGUBlock>) registry.registerBlock(_id("ultimate_aegu"), () -> new AEGUBlock(_id("ultimate_aegu"), 20000 / 10));

        DARK_MATTER_BLOCK = registry.registerBlock(_id("dark_matter_block"), () -> new CompatBlock(CompatibleBlockSettings.of(_id("dark_matter_block"), CompatibleMaterial.STONE).strength(2.0f, 5.0f)));
        RED_MATTER_BLOCK = registry.registerBlock(_id("red_matter_block"), () -> new CompatBlock(CompatibleBlockSettings.of(_id("red_matter_block"), CompatibleMaterial.STONE).strength(2.0f, 5.0f)));

        EMC_IMPORTER = (SupplierITypedBlockWrapper<EMCImporter>) registry.registerBlock(_id("emc_importer"), () -> new EMCImporter(CompatibleBlockSettings.of(_id("emc_importer"), CompatibleMaterial.STONE).strength(2.0f, 5.0f)));
        EMC_EXPORTER = (SupplierITypedBlockWrapper<EMCExporter>) registry.registerBlock(_id("emc_exporter"), () -> new EMCExporter(CompatibleBlockSettings.of(_id("emc_exporter"), CompatibleMaterial.STONE).strength(2.0f, 5.0f)));

        EMC_CABLE = registry.registerBlock(_id("emc_cable"), () -> new EMCCable(CompatibleBlockSettings.of(_id("emc_cable"), CompatibleMaterial.STONE).strength(2.0f, 5.0f)));
        EMC_BATTERY = (SupplierITypedBlockWrapper<EMCBattery>) registry.registerBlock(_id("emc_battery"), () -> new EMCBattery(_id("emc_battery"), 100_000));

        INTERDICTION_TORCH = (SupplierITypedBlockWrapper<InterdictionTorch>) registry.registerBlock(_id("interdiction_torch"), () -> new InterdictionTorch(_id("interdiction_torch")));

        DM_PEDESTAL = (SupplierITypedBlockWrapper<DMPedestal>) registry.registerBlock(_id("dm_pedestal"), () -> new DMPedestal(_id("dm_pedestal")));
    }
}
