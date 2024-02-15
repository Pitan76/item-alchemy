package net.pitan76.itemalchemy.tile;

import net.pitan76.itemalchemy.block.Blocks;
import net.pitan76.itemalchemy.ItemAlchemy;
import net.pitan76.mcpitanlib.api.registry.result.RegistryResult;
import net.pitan76.mcpitanlib.api.tile.BlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;

public class Tiles {
    public static RegistryResult<BlockEntityType<?>> EMC_COLLECTOR;
    public static RegistryResult<BlockEntityType<?>> EMC_CONDENSER;
    //public static RegistryResult<BlockEntityType<?>> EMC_CONDENSER_MK2;
    public static RegistryResult<BlockEntityType<?>> AEGU;
    public static RegistryResult<BlockEntityType<?>> ALCHEMY_CHEST;

    public static void init() {
        EMC_COLLECTOR = ItemAlchemy.registry.registerBlockEntityType(ItemAlchemy.id("emc_collector"), () -> create(EMCCollectorTile::new, Blocks.EMC_COLLECTOR_MK1.getOrNull(), Blocks.EMC_COLLECTOR_MK2.getOrNull(), Blocks.EMC_COLLECTOR_MK3.getOrNull(), Blocks.EMC_COLLECTOR_MK4.getOrNull(), Blocks.EMC_COLLECTOR_MK5.getOrNull()));
        EMC_CONDENSER = ItemAlchemy.registry.registerBlockEntityType(ItemAlchemy.id("emc_condenser"), () -> create(EMCCondenserTile::new, Blocks.EMC_CONDENSER.getOrNull()));
        //EMC_CONDENSER_MK2 = registry.registerBlockEntityType(id("emc_condenser_mk2"), () -> create(EMCCondenserMK2Tile::new, Blocks.EMC_CONDENSER_MK2.getOrNull()));
        AEGU = ItemAlchemy.registry.registerBlockEntityType(ItemAlchemy.id("aegu"), () -> create(AEGUTile::new, Blocks.AEGU.getOrNull(), Blocks.ADVANCED_AEGU.getOrNull(), Blocks.ULTIMATE_AEGU.getOrNull()));
        ALCHEMY_CHEST = ItemAlchemy.registry.registerBlockEntityType(ItemAlchemy.id("alchemy_chest"), () -> create(AlchemyChestTile::new, Blocks.ALCHEMY_CHEST.getOrNull()));
    }

    public static <T extends BlockEntity> BlockEntityType<T> create(BlockEntityTypeBuilder.Factory<T> supplier, Block... blocks) {
        return BlockEntityTypeBuilder.create(supplier, blocks).build();
    }
}
