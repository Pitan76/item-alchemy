package net.pitan76.itemalchemy.tile;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.pitan76.itemalchemy.block.Blocks;
import net.pitan76.mcpitanlib.api.registry.result.RegistryResult;
import net.pitan76.mcpitanlib.api.tile.BlockEntityTypeBuilder;

import static net.pitan76.itemalchemy.ItemAlchemy._id;
import static net.pitan76.itemalchemy.ItemAlchemy.registry;

public class Tiles {
    public static RegistryResult<BlockEntityType<?>> EMC_COLLECTOR;
    public static RegistryResult<BlockEntityType<?>> EMC_CONDENSER;
    //public static RegistryResult<BlockEntityType<?>> EMC_CONDENSER_MK2;
    public static RegistryResult<BlockEntityType<?>> AEGU;
    public static RegistryResult<BlockEntityType<?>> ALCHEMY_CHEST;
    public static RegistryResult<BlockEntityType<?>> EMC_IMPORTER;
    public static RegistryResult<BlockEntityType<?>> EMC_EXPORTER;
    public static RegistryResult<BlockEntityType<?>> EMC_BATTERY;

    public static void init() {
        EMC_COLLECTOR = registry.registerBlockEntityType(_id("emc_collector"), () -> create(EMCCollectorTile::new, Blocks.EMC_COLLECTOR_MK1.getOrNull(), Blocks.EMC_COLLECTOR_MK2.getOrNull(), Blocks.EMC_COLLECTOR_MK3.getOrNull(), Blocks.EMC_COLLECTOR_MK4.getOrNull(), Blocks.EMC_COLLECTOR_MK5.getOrNull()));
        EMC_CONDENSER = registry.registerBlockEntityType(_id("emc_condenser"), () -> create(EMCCondenserTile::new, Blocks.EMC_CONDENSER.getOrNull()));
        //EMC_CONDENSER_MK2 = registry.registerBlockEntityType(id("emc_condenser_mk2"), () -> create(EMCCondenserMK2Tile::new, Blocks.EMC_CONDENSER_MK2.getOrNull()));
        AEGU = registry.registerBlockEntityType(_id("aegu"), () -> create(AEGUTile::new, Blocks.AEGU.getOrNull(), Blocks.ADVANCED_AEGU.getOrNull(), Blocks.ULTIMATE_AEGU.getOrNull()));
        ALCHEMY_CHEST = registry.registerBlockEntityType(_id("alchemy_chest"), () -> create(AlchemyChestTile::new, Blocks.ALCHEMY_CHEST.getOrNull()));
        EMC_IMPORTER = registry.registerBlockEntityType(_id("emc_importer"), () -> create(EMCImporterTile::new, Blocks.EMC_IMPORTER.getOrNull()));
        EMC_EXPORTER = registry.registerBlockEntityType(_id("emc_exporter"), () -> create(EMCExporterTile::new, Blocks.EMC_EXPORTER.getOrNull()));
    }

    public static <T extends BlockEntity> BlockEntityType<T> create(BlockEntityTypeBuilder.Factory<T> supplier, Block... blocks) {
        return BlockEntityTypeBuilder.create(supplier, blocks).build();
    }
}
