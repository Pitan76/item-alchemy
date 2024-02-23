package net.pitan76.itemalchemy.gui.screen;

import net.minecraft.screen.ScreenHandlerType;
import net.pitan76.itemalchemy.ItemAlchemy;
import net.pitan76.mcpitanlib.api.gui.ExtendedScreenHandlerTypeBuilder;
import net.pitan76.mcpitanlib.api.gui.SimpleScreenHandlerTypeBuilder;

import static net.pitan76.itemalchemy.ItemAlchemy.registry;

public class ScreenHandlers {
    public static ScreenHandlerType<AlchemyTableScreenHandler> ALCHEMY_TABLE = new SimpleScreenHandlerTypeBuilder<>(AlchemyTableScreenHandler::new).build();
    public static ScreenHandlerType<EMCCollectorScreenHandler> EMC_COLLECTOR = new ExtendedScreenHandlerTypeBuilder<>(EMCCollectorScreenHandler::new).build();
    public static ScreenHandlerType<EMCCondenserScreenHandler> EMC_CONDENSER = new ExtendedScreenHandlerTypeBuilder<>(EMCCondenserScreenHandler::new).build();

    public static ScreenHandlerType<AlchemyChestScreenHandler> ALCHEMY_CHEST = new SimpleScreenHandlerTypeBuilder<>(AlchemyChestScreenHandler::new).build();

    public static void init() {
        registry.registerScreenHandlerType(ItemAlchemy.id("alchemy_table"), () -> ALCHEMY_TABLE);
        registry.registerScreenHandlerType(ItemAlchemy.id("emc_collector"), () -> EMC_COLLECTOR);
        registry.registerScreenHandlerType(ItemAlchemy.id("emc_condenser"), () -> EMC_CONDENSER);
        registry.registerScreenHandlerType(ItemAlchemy.id("alchemy_chest"), () -> ALCHEMY_CHEST);
    }
}
