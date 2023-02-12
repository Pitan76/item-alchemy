package ml.pkom.itemalchemy;

import ml.pkom.itemalchemy.gui.screens.AlchemyChestScreenHandler;
import ml.pkom.itemalchemy.gui.screens.AlchemyTableScreenHandler;
import ml.pkom.itemalchemy.gui.screens.EMCCollectorScreenHandler;
import ml.pkom.itemalchemy.gui.screens.EMCCondenserScreenHandler;
import ml.pkom.mcpitanlibarch.api.gui.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;

import static ml.pkom.itemalchemy.ItemAlchemy.registry;

public class ScreenHandlers {
    public static ScreenHandlerType<AlchemyTableScreenHandler> ALCHEMY_TABLE = new ScreenHandlerType<>(AlchemyTableScreenHandler::new);
//    public static ScreenHandlerType<EMCCollectorScreenHandler> EMC_COLLECTOR = new ExtendedScreenHandlerType<>(EMCCollectorScreenHandler::new);
//    public static ScreenHandlerType<EMCCondenserScreenHandler> EMC_CONDENSER = new ExtendedScreenHandlerType<>(EMCCondenserScreenHandler::new);

    public static ScreenHandlerType<EMCCollectorScreenHandler> EMC_COLLECTOR = ScreenHandlerRegistry.registerExtended(ItemAlchemy.id("emc_collector"), EMCCollectorScreenHandler::new);
    public static ScreenHandlerType<EMCCondenserScreenHandler> EMC_CONDENSER = ScreenHandlerRegistry.registerExtended(ItemAlchemy.id("emc_condenser"), EMCCondenserScreenHandler::new);
    public static ScreenHandlerType<AlchemyChestScreenHandler> ALCHEMY_CHEST = new ScreenHandlerType<>(AlchemyChestScreenHandler::new);

    public static void init() {
        registry.registerScreenHandlerType(ItemAlchemy.id("alchemy_table"), () -> ALCHEMY_TABLE);
        //registry.registerScreenHandlerType(ItemAlchemy.id("emc_collector"), () -> EMC_COLLECTOR);
        //registry.registerScreenHandlerType(ItemAlchemy.id("emc_condenser"), () -> EMC_CONDENSER);
        registry.registerScreenHandlerType(ItemAlchemy.id("alchemy_chest"), () -> ALCHEMY_CHEST);
    }
}
