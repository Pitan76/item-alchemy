package ml.pkom.itemalchemy;

import ml.pkom.itemalchemy.gui.screens.AlchemyTableScreenHandler;
import ml.pkom.itemalchemy.gui.screens.EMCCollectorScreenHandler;
import ml.pkom.itemalchemy.gui.screens.EMCCondenserScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;

public class ScreenHandlers {
    public static ScreenHandlerType<AlchemyTableScreenHandler> ALCHEMY_TABLE = ScreenHandlerRegistry.registerSimple(ItemAlchemy.id("alchemy_table"), AlchemyTableScreenHandler::new);
    public static ScreenHandlerType<EMCCollectorScreenHandler> EMC_COLLECTOR = ScreenHandlerRegistry.registerExtended(ItemAlchemy.id("emc_collector"), EMCCollectorScreenHandler::new);
    public static ScreenHandlerType<EMCCondenserScreenHandler> EMC_CONDENSER = ScreenHandlerRegistry.registerExtended(ItemAlchemy.id("emc_condenser"), EMCCondenserScreenHandler::new);

    public static void init() {

    }
}
