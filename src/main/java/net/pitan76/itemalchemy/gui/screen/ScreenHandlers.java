package net.pitan76.itemalchemy.gui.screen;

import net.pitan76.itemalchemy.ItemAlchemy;
import net.pitan76.mcpitanlib.api.gui.ExtendedScreenHandlerTypeBuilder;
import net.pitan76.mcpitanlib.api.gui.SimpleScreenHandlerTypeBuilder;
import net.minecraft.screen.ScreenHandlerType;

public class ScreenHandlers {
    public static ScreenHandlerType<AlchemyTableScreenHandler> ALCHEMY_TABLE = new SimpleScreenHandlerTypeBuilder<>(AlchemyTableScreenHandler::new).build();
    public static ScreenHandlerType<EMCCollectorScreenHandler> EMC_COLLECTOR = new ExtendedScreenHandlerTypeBuilder<>(EMCCollectorScreenHandler::new).build();
    public static ScreenHandlerType<EMCCondenserScreenHandler> EMC_CONDENSER = new ExtendedScreenHandlerTypeBuilder<>(EMCCondenserScreenHandler::new).build();

    public static ScreenHandlerType<AlchemyChestScreenHandler> ALCHEMY_CHEST = new SimpleScreenHandlerTypeBuilder<>(AlchemyChestScreenHandler::new).build();

    public static void init() {
        ItemAlchemy.registry.registerScreenHandlerType(ItemAlchemy.id("alchemy_table"), () -> ALCHEMY_TABLE);
        ItemAlchemy.registry.registerScreenHandlerType(ItemAlchemy.id("emc_collector"), () -> EMC_COLLECTOR);
        ItemAlchemy.registry.registerScreenHandlerType(ItemAlchemy.id("emc_condenser"), () -> EMC_CONDENSER);
        ItemAlchemy.registry.registerScreenHandlerType(ItemAlchemy.id("alchemy_chest"), () -> ALCHEMY_CHEST);
    }
}
