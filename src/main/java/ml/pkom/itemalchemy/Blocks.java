package ml.pkom.itemalchemy;

import ml.pkom.itemalchemy.blocks.AlchemyTable;
import ml.pkom.mcpitanlibarch.api.event.registry.RegistryEvent;
import net.minecraft.block.Block;

import static ml.pkom.itemalchemy.ItemAlchemy.id;
import static ml.pkom.itemalchemy.ItemAlchemy.registry;

public class Blocks {

    public static RegistryEvent<Block> ALCHEMY_TABLE;

    public static void init() {
        ALCHEMY_TABLE = registry.registerBlock(id("alchemy_table"), () -> new AlchemyTable());
    }
}
