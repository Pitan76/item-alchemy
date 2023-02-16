package ml.pkom.itemalchemy;

import ml.pkom.itemalchemy.gui.screens.AlchemyChestScreenHandler;
import ml.pkom.itemalchemy.gui.screens.AlchemyTableScreenHandler;
import ml.pkom.itemalchemy.gui.screens.EMCCollectorScreenHandler;
import ml.pkom.itemalchemy.gui.screens.EMCCondenserScreenHandler;
import ml.pkom.mcpitanlibarch.api.gui.ExtendedScreenHandlerTypeBuilder;
import net.minecraft.screen.ScreenHandlerType;

import static ml.pkom.itemalchemy.ItemAlchemy.registry;

public class ScreenHandlers {
    public static ScreenHandlerType<AlchemyTableScreenHandler> ALCHEMY_TABLE = new ScreenHandlerType<>(AlchemyTableScreenHandler::new);
    public static ScreenHandlerType<EMCCollectorScreenHandler> EMC_COLLECTOR = new ExtendedScreenHandlerTypeBuilder<>(EMCCollectorScreenHandler::new).build();
    public static ScreenHandlerType<EMCCondenserScreenHandler> EMC_CONDENSER = new ExtendedScreenHandlerTypeBuilder<>(EMCCondenserScreenHandler::new).build();

    public static ScreenHandlerType<AlchemyChestScreenHandler> ALCHEMY_CHEST = new ScreenHandlerType<>(AlchemyChestScreenHandler::new);

    public static void init() {
        registry.registerScreenHandlerType(ItemAlchemy.id("alchemy_table"), () -> ALCHEMY_TABLE);
        registry.registerScreenHandlerType(ItemAlchemy.id("emc_collector"), () -> EMC_COLLECTOR);
        registry.registerScreenHandlerType(ItemAlchemy.id("emc_condenser"), () -> EMC_CONDENSER);
        registry.registerScreenHandlerType(ItemAlchemy.id("alchemy_chest"), () -> ALCHEMY_CHEST);
    }
}
