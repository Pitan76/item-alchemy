package net.pitan76.itemalchemy;

import net.minecraft.util.Identifier;
import net.pitan76.itemalchemy.api.EMCUtil;
import net.pitan76.itemalchemy.block.Blocks;
import net.pitan76.itemalchemy.command.ItemAlchemyCommand;
import net.pitan76.itemalchemy.data.ServerState;
import net.pitan76.itemalchemy.emcs.itemalchemy.ItemAlchemyEMCDef;
import net.pitan76.itemalchemy.emcs.vanilla.VanillaEMCDef;
import net.pitan76.itemalchemy.gui.screen.ScreenHandlers;
import net.pitan76.itemalchemy.item.ItemGroups;
import net.pitan76.itemalchemy.item.Items;
import net.pitan76.itemalchemy.network.ServerNetworks;
import net.pitan76.itemalchemy.recipe.AlchemicalRecipeManager;
import net.pitan76.itemalchemy.sound.Sounds;
import net.pitan76.itemalchemy.tile.Tiles;
import net.pitan76.itemalchemy.util.ItemCharge;
import net.pitan76.mcpitanlib.api.command.CommandRegistry;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.event.v0.EventRegistry;
import net.pitan76.mcpitanlib.api.event.v0.event.ItemStackActionEvent;
import net.pitan76.mcpitanlib.api.event.v1.RecipeManagerRegistry;
import net.pitan76.mcpitanlib.api.registry.CompatRegistry;
import net.pitan76.mcpitanlib.api.util.IdentifierUtil;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemAlchemy {

    public static final String MOD_ID = "itemalchemy";
    public static final String MOD_NAME = "ItemAlchemy";

    public static Logger LOGGER = LogManager.getLogger();
    public static void log(Level level, String message){
        LOGGER.log(level, "[" + MOD_NAME + "] " + message);
    }

    public static Identifier id(String id) {
        return IdentifierUtil.id(MOD_ID, id);
    }

    public static CompatRegistry registry = CompatRegistry.createRegistry(MOD_ID);

    public static void init() {
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
            if (p != null) {
                Player player = new Player(p);
                EMCManager.syncS2C_emc_map(p);
                ServerState serverState = ServerState.getServerState(player.getWorld().getServer());

                serverState.createPlayer(player);

                EMCManager.syncS2C(p);
            }
        });

        EventRegistry.ServerLifecycle.serverStopped(EMCManager::exit);

        ItemStackActionEvent.register((stack) -> {
            if (stack.getItem() instanceof ItemCharge) {
                ItemStackActionEvent.setReturnValue(false);
            }
        });

        ServerNetworks.init();

        // Registry commands
        CommandRegistry.register("itemalchemy", new ItemAlchemyCommand());

        registry.allRegister();
    }
}
