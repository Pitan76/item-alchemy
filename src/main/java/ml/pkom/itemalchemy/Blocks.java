package ml.pkom.itemalchemy;

import ml.pkom.itemalchemy.blocks.AlchemyTable;
import ml.pkom.itemalchemy.blocks.EMCCollector;
import ml.pkom.itemalchemy.blocks.EMCCondenser;
import ml.pkom.mcpitanlibarch.api.event.registry.RegistryEvent;
import net.minecraft.block.Block;

import static ml.pkom.itemalchemy.ItemAlchemy.id;
import static ml.pkom.itemalchemy.ItemAlchemy.registry;

public class Blocks {

    public static RegistryEvent<Block> ALCHEMY_TABLE;
    public static RegistryEvent<Block> EMC_COLLECTOR_MK1;
    public static RegistryEvent<Block> EMC_COLLECTOR_MK2;
    public static RegistryEvent<Block> EMC_COLLECTOR_MK3;
    public static RegistryEvent<Block> EMC_CONDENSER;

    public static void init() {
        ALCHEMY_TABLE = registry.registerBlock(id("alchemy_table"), AlchemyTable::new);
        EMC_COLLECTOR_MK1 = registry.registerBlock(id("emc_collector_mk1"), EMCCollector::new);
        EMC_COLLECTOR_MK2 = registry.registerBlock(id("emc_collector_mk2"), EMCCollector::new);
        EMC_COLLECTOR_MK3 = registry.registerBlock(id("emc_collector_mk3"), EMCCollector::new);
        EMC_CONDENSER = registry.registerBlock(id("emc_condenser"), EMCCondenser::new);
    }
}
