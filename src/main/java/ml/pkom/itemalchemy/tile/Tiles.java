package ml.pkom.itemalchemy.tile;

import ml.pkom.itemalchemy.block.Blocks;
import ml.pkom.mcpitanlibarch.api.event.registry.RegistryResult;
import ml.pkom.mcpitanlibarch.api.tile.BlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;

import static ml.pkom.itemalchemy.ItemAlchemy.id;
import static ml.pkom.itemalchemy.ItemAlchemy.registry;

public class Tiles {
    public static RegistryResult<BlockEntityType<?>> EMC_COLLECTOR;
    public static RegistryResult<BlockEntityType<?>> EMC_CONDENSER;
    //public static RegistryResult<BlockEntityType<?>> EMC_CONDENSER_MK2;
    public static RegistryResult<BlockEntityType<?>> AEGU;
    public static RegistryResult<BlockEntityType<?>> ALCHEMY_CHEST;

    public static void init() {
        EMC_COLLECTOR = registry.registerBlockEntityType(id("emc_collector"), () -> create(EMCCollectorTile::new, Blocks.EMC_COLLECTOR_MK1.getOrNull(), Blocks.EMC_COLLECTOR_MK2.getOrNull(), Blocks.EMC_COLLECTOR_MK3.getOrNull(), Blocks.EMC_COLLECTOR_MK4.getOrNull(), Blocks.EMC_COLLECTOR_MK5.getOrNull()));
        EMC_CONDENSER = registry.registerBlockEntityType(id("emc_condenser"), () -> create(EMCCondenserTile::new, Blocks.EMC_CONDENSER.getOrNull()));
        //EMC_CONDENSER_MK2 = registry.registerBlockEntityType(id("emc_condenser_mk2"), () -> create(EMCCondenserMK2Tile::new, Blocks.EMC_CONDENSER_MK2.getOrNull()));
        AEGU = registry.registerBlockEntityType(id("aegu"), () -> create(AEGUTile::new, Blocks.AEGU.getOrNull(), Blocks.ADVANCED_AEGU.getOrNull(), Blocks.ULTIMATE_AEGU.getOrNull()));
        ALCHEMY_CHEST = registry.registerBlockEntityType(id("alchemy_chest"), () -> create(AlchemyChestTile::new, Blocks.ALCHEMY_CHEST.getOrNull()));
    }

    public static <T extends BlockEntity> BlockEntityType<T> create(BlockEntityTypeBuilder.Factory<T> supplier, Block... blocks) {
        return BlockEntityTypeBuilder.create(supplier, blocks).build();
    }
}
