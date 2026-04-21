package net.pitan76.itemalchemy.tile;

import net.pitan76.itemalchemy.block.Blocks;
import net.pitan76.mcpitanlib.api.tile.v2.BlockEntityTypeBuilder;
import net.pitan76.mcpitanlib.midohra.block.entity.TypedBlockEntityTypeWrapper;

import static net.pitan76.itemalchemy.ItemAlchemy._id;
import static net.pitan76.itemalchemy.ItemAlchemy.registry;

public class Tiles {
    public static TypedBlockEntityTypeWrapper<EMCCollectorTile> EMC_COLLECTOR;
    public static TypedBlockEntityTypeWrapper<EMCCondenserTile> EMC_CONDENSER;
    public static TypedBlockEntityTypeWrapper<EMCCondenserMK2Tile> EMC_CONDENSER_MK2;
    public static TypedBlockEntityTypeWrapper<AEGUTile> AEGU;
    public static TypedBlockEntityTypeWrapper<AlchemyChestTile> ALCHEMY_CHEST;
    public static TypedBlockEntityTypeWrapper<EMCImporterTile> EMC_IMPORTER;
    public static TypedBlockEntityTypeWrapper<EMCExporterTile> EMC_EXPORTER;
    public static TypedBlockEntityTypeWrapper<EMCBatteryTile> EMC_BATTERY;
    public static TypedBlockEntityTypeWrapper<InterdictionTorchTile> INTERDICTION_TORCH;
    public static TypedBlockEntityTypeWrapper<DMPedestalTile> DM_PEDESTAL;

    public static void init() {
        EMC_COLLECTOR = registry.registerBlockEntityType(_id("emc_collector"), BlockEntityTypeBuilder.create(EMCCollectorTile::new, list -> {
            list.add(Blocks.EMC_COLLECTOR_MK1.get());
            list.add(Blocks.EMC_COLLECTOR_MK2.get());
            list.add(Blocks.EMC_COLLECTOR_MK3.get());
            list.add(Blocks.EMC_COLLECTOR_MK4.get());
            list.add(Blocks.EMC_COLLECTOR_MK5.get());
        }));
        EMC_CONDENSER = registry.registerBlockEntityType(_id("emc_condenser"), BlockEntityTypeBuilder.create(EMCCondenserTile::new, Blocks.EMC_CONDENSER.asNonTyped()));
        EMC_CONDENSER_MK2 = registry.registerBlockEntityType(_id("emc_condenser_mk2"), BlockEntityTypeBuilder.create(EMCCondenserMK2Tile::new, Blocks.EMC_CONDENSER_MK2.asNonTyped()));
        AEGU = registry.registerBlockEntityType(_id("aegu"), BlockEntityTypeBuilder.create(AEGUTile::new, list -> {
            list.add(Blocks.AEGU.get());
            list.add(Blocks.ADVANCED_AEGU.get());
            list.add(Blocks.ULTIMATE_AEGU.get());
        }));
        ALCHEMY_CHEST = registry.registerBlockEntityType(_id("alchemy_chest"), BlockEntityTypeBuilder.create(AlchemyChestTile::new, Blocks.ALCHEMY_CHEST.asNonTyped()));
        EMC_IMPORTER = registry.registerBlockEntityType(_id("emc_importer"), BlockEntityTypeBuilder.create(EMCImporterTile::new, Blocks.EMC_IMPORTER.asNonTyped()));
        EMC_EXPORTER = registry.registerBlockEntityType(_id("emc_exporter"), BlockEntityTypeBuilder.create(EMCExporterTile::new, Blocks.EMC_EXPORTER.asNonTyped()));
        EMC_BATTERY = registry.registerBlockEntityType(_id("emc_battery"), BlockEntityTypeBuilder.create(EMCBatteryTile::new, Blocks.EMC_BATTERY.asNonTyped()));
        INTERDICTION_TORCH = registry.registerBlockEntityType(_id("interdiction_torch"), BlockEntityTypeBuilder.create(InterdictionTorchTile::new, Blocks.INTERDICTION_TORCH.asNonTyped()));
        DM_PEDESTAL = registry.registerBlockEntityType(_id("dm_pedestal"), BlockEntityTypeBuilder.create(DMPedestalTile::new, Blocks.DM_PEDESTAL.asNonTyped()));
    }
}
