package net.pitan76.itemalchemy;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class FabricMain implements ModInitializer {
    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        ItemAlchemy.init();
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ItemAlchemyClient.init();
        }
    }
}
