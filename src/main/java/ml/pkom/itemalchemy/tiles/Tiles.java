package ml.pkom.itemalchemy.tiles;

import ml.pkom.itemalchemy.Blocks;
import ml.pkom.mcpitanlibarch.api.event.registry.RegistryEvent;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;

import static ml.pkom.itemalchemy.ItemAlchemy.id;
import static ml.pkom.itemalchemy.ItemAlchemy.registry;

public class Tiles {
    public static RegistryEvent<BlockEntityType<?>> EMC_COLLECTOR;

    public static void init() {
        EMC_COLLECTOR = registry.registerBlockEntityType(id("emc_collector"), () -> create(EMCCollectorTile::new, Blocks.EMC_COLLECTOR_MK1.getOrNull()));
    }

    public static <T extends BlockEntity> BlockEntityType<T> create(FabricBlockEntityTypeBuilder.Factory<T> supplier, Block... blocks) {
        return FabricBlockEntityTypeBuilder.create(supplier, blocks).build(null);
    }
}
