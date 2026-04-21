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
import net.pitan76.itemalchemy.manager.KleinStarRechargeManager;
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
import net.pitan76.mcpitanlib.api.event.v2.ItemEventRegistry;
import net.pitan76.mcpitanlib.api.registry.v2.CompatRegistryV2;
import net.pitan76.mcpitanlib.api.util.CompatIdentifier;
import net.pitan76.mcpitanlib.api.util.Logger;
import net.pitan76.mcpitanlib.midohra.registry.MidohraRegistry;

public class ItemAlchemy extends CommonModInitializer {

    public static final String MOD_ID = "itemalchemy";
    public static final String MOD_NAME = "ItemAlchemy";

    public static MidohraRegistry registry;
    public static CompatRegistryV2 registry2;

    public static ItemAlchemy INSTANCE;

    public static Logger logger;

    public void init() {
        INSTANCE = this;
        registry2 = super.registry;
        registry = MidohraRegistry.of(super.registry);
        logger = super.logger;

        ItemAlchemyConfig.initOnce();

        ItemGroups.init();
        Sounds.init();
        Blocks.init();
        Items.init();
        ScreenHandlers.init();
        Tiles.init();

        RecipeManagerRegistry.register(AlchemicalRecipeManager::new);

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

        // Register player tick for Klein Star recharging via InventoryTickEvent
        // Only process when the selected item is ticked to avoid multiple calls per player tick
        ItemEventRegistry.INVENTORY_TICK.register((e) -> {
            if (e.isClient()) return;
            // Only process recharge when entity is a player and this is the selected item
            if (e.isPlayer() && e.isSelected()) {
                KleinStarRechargeManager.tryRechargeItems(e.getPlayer());
            }
        });

        //EventRegistry.ServerLifecycle.serverStopped(EMCManager::exit);

        ItemStackActionEvent.register((stack) -> {
            if (!(stack.getItem() instanceof ItemCharge)) return;

            ItemStackActionEvent.setReturnValue(false);
        });

        ServerNetworks.init();

        // Registry commands
        CommandRegistry.register("itemalchemy", new ItemAlchemyCommand());

        registry2.registerFuel(() -> Items.ALCHEMICAL_FUEL.get(), 200 * 16);
        registry2.registerFuel(() -> Items.MOBIUS_FUEL.get(), 200 * 64);
        registry2.registerFuel(() -> Items.AETERNALIS_FUEL.get(), 200 * 128);
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
