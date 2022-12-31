package ml.pkom.itemalchemy;

import ml.pkom.itemalchemy.blocks.*;
import ml.pkom.mcpitanlibarch.api.block.ExtendBlock;
import ml.pkom.mcpitanlibarch.api.event.registry.RegistryEvent;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;

import static ml.pkom.itemalchemy.ItemAlchemy.id;
import static ml.pkom.itemalchemy.ItemAlchemy.registry;

public class Blocks {

    public static RegistryEvent<Block> ALCHEMY_TABLE;
    public static RegistryEvent<Block> EMC_COLLECTOR_MK1;
    public static RegistryEvent<Block> EMC_COLLECTOR_MK2;
    public static RegistryEvent<Block> EMC_COLLECTOR_MK3;
    public static RegistryEvent<Block> EMC_COLLECTOR_MK4;
    public static RegistryEvent<Block> EMC_COLLECTOR_MK5;
    public static RegistryEvent<Block> EMC_CONDENSER;
    public static RegistryEvent<Block> EMC_REPEATER;
    public static RegistryEvent<Block> AEGU;
    public static RegistryEvent<Block> ADVANCED_AEGU;
    public static RegistryEvent<Block> ULTIMATE_AEGU;

    public static RegistryEvent<Block> DARK_MATTER_BLOCK;
    public static RegistryEvent<Block> RED_MATTER_BLOCK;

    public static void init() {
        ALCHEMY_TABLE = registry.registerBlock(id("alchemy_table"), AlchemyTable::new);
        EMC_COLLECTOR_MK1 = registry.registerBlock(id("emc_collector_mk1"), () -> new EMCCollector(10000));
        EMC_COLLECTOR_MK2 = registry.registerBlock(id("emc_collector_mk2"), () -> new EMCCollector(30000));
        EMC_COLLECTOR_MK3 = registry.registerBlock(id("emc_collector_mk3"), () -> new EMCCollector(60000));
        EMC_COLLECTOR_MK4 = registry.registerBlock(id("emc_collector_mk4"), () -> new EMCCollector(250000));
        EMC_COLLECTOR_MK5 = registry.registerBlock(id("emc_collector_mk5"), () -> new EMCCollector(2000000));
        EMC_CONDENSER = registry.registerBlock(id("emc_condenser"), EMCCondenser::new);
        EMC_REPEATER = registry.registerBlock(id("emc_repeater"), EMCRepeater::new);
        AEGU = registry.registerBlock(id("aegu"), () -> new AEGUBlock(40 / 10));
        ADVANCED_AEGU = registry.registerBlock(id("advanced_aegu"), () -> new AEGUBlock(1000 / 10));
        ULTIMATE_AEGU = registry.registerBlock(id("ultimate_aegu"), () -> new AEGUBlock(20000 / 10));

        DARK_MATTER_BLOCK = registry.registerBlock(id("dark_matter_block"), () -> new ExtendBlock(AbstractBlock.Settings.of(Material.STONE).strength(2.0f, 5.0f)));
        RED_MATTER_BLOCK = registry.registerBlock(id("red_matter_block"), () -> new ExtendBlock(AbstractBlock.Settings.of(Material.STONE).strength(2.0f, 5.0f)));
    }
}
