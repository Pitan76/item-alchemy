package ml.pkom.itemalchemy;

import ml.pkom.itemalchemy.screens.AlchemyTableScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;

public class ScreenHandlers {
    public static ScreenHandlerType<AlchemyTableScreenHandler> ALCHEMY_TABLE = ScreenHandlerRegistry.registerSimple(ItemAlchemy.id("alchemy_table"), AlchemyTableScreenHandler::new);

    public static void init() {

    }
}
