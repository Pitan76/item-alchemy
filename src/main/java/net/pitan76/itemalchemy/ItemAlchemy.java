package net.pitan76.itemalchemy;

import net.pitan76.itemalchemy.api.EMCUtil;
import net.pitan76.itemalchemy.block.Blocks;
import net.pitan76.itemalchemy.command.ItemAlchemyCommand;
import net.pitan76.itemalchemy.config.ItemAlchemyConfig;
import net.pitan76.itemalchemy.data.ServerState;
import net.pitan76.itemalchemy.emc.itemalchemy.ItemAlchemyEMCDef;
import net.pitan76.itemalchemy.emc.vanilla.VanillaEMCDef;
import net.pitan76.itemalchemy.gui.screen.ScreenHandlers;
import net.pitan76.itemalchemy.item.ItemGroups;
import net.pitan76.itemalchemy.item.Items;
import net.pitan76.itemalchemy.network.ServerNetworks;
import net.pitan76.itemalchemy.recipe.AlchemicalRecipeManager;
import net.pitan76.itemalchemy.sound.Sounds;
import net.pitan76.itemalchemy.tile.Tiles;
import net.pitan76.itemalchemy.util.ItemCharge;
import net.pitan76.mcpitanlib.api.CommonModInitializer;
import net.pitan76.mcpitanlib.api.command.CommandRegistry;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.event.v0.EventRegistry;
import net.pitan76.mcpitanlib.api.event.v0.event.ItemStackActionEvent;
import net.pitan76.mcpitanlib.api.event.v1.RecipeManagerRegistry;
import net.pitan76.mcpitanlib.api.registry.v2.CompatRegistryV2;
import net.pitan76.mcpitanlib.api.util.CompatIdentifier;
import net.pitan76.mcpitanlib.api.util.Logger;

public class ItemAlchemy extends CommonModInitializer {

    public static final String MOD_ID = "itemalchemy";
    public static final String MOD_NAME = "ItemAlchemy";

    public static CompatRegistryV2 registry;

    public static ItemAlchemy INSTANCE;

    public static Logger logger;

    public void init() {
        INSTANCE = this;
        registry = super.registry;
        logger = super.logger;

        ItemAlchemyConfig.initOnce();

        RecipeManagerRegistry.register(AlchemicalRecipeManager::new);

        ItemGroups.init();
        Sounds.init();
        Blocks.init();
        Items.init();
        ScreenHandlers.init();
        Tiles.init();

        EMCUtil.addDef(new VanillaEMCDef(), new ItemAlchemyEMCDef());

        EventRegistry.ServerLifecycle.serverStarted(EMCManager::init);

        EventRegistry.ServerConnection.join((p) -> {
            if (p == null) return;

            Player player = new Player(p);

            EMCManager.syncS2C_emc_map(player);
            ServerState serverState = ServerState.getServerState(player.getWorld().getServer());

            serverState.createPlayer(player);

            EMCManager.syncS2C(player);
        });

        //EventRegistry.ServerLifecycle.serverStopped(EMCManager::exit);

        ItemStackActionEvent.register((stack) -> {
            if (!(stack.getItem() instanceof ItemCharge)) return;

            ItemStackActionEvent.setReturnValue(false);
        });

        ServerNetworks.init();

        // Registry commands
        CommandRegistry.register("itemalchemy", new ItemAlchemyCommand());

        registry.registerFuel(() -> Items.ALCHEMICAL_FUEL.getOrNull(), 200 * 16);
        registry.registerFuel(() -> Items.MOBIUS_FUEL.getOrNull(), 200 * 64);
        registry.registerFuel(() -> Items.AETERNALIS_FUEL.getOrNull(), 200 * 128);
    }

    @Override
    public String getId() {
        return MOD_ID;
    }

    @Override
    public String getName() {
        return MOD_NAME;
    }

    public static CompatIdentifier _id(String path) {
        return INSTANCE.compatId(path);
    }
}
