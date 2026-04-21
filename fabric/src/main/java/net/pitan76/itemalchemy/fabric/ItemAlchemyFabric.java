package net.pitan76.itemalchemy.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class ItemAlchemyFabric implements ModInitializer {
    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        new ItemAlchemy();
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ItemAlchemyClient.init();
        }
    }
}
