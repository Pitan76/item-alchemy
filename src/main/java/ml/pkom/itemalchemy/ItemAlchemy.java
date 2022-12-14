package ml.pkom.itemalchemy;

import ml.pkom.itemalchemy.command.ItemAlchemyCommand;
import ml.pkom.itemalchemy.gui.screens.AlchemyTableScreenHandler;
import ml.pkom.itemalchemy.tiles.Tiles;
import ml.pkom.mcpitanlibarch.api.command.CommandRegistry;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import ml.pkom.mcpitanlibarch.api.item.CreativeTabBuilder;
import ml.pkom.mcpitanlibarch.api.registry.ArchRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
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
        return new Identifier(MOD_ID, id);
    }

    public static ArchRegistry registry = ArchRegistry.createRegistry(MOD_ID);

    public static ItemGroup ITEM_ALCHEMY = new CreativeTabBuilder(id("item_alchemy")).setIcon(() -> new ItemStack(Items.PHILOSOPHER_STONE.getOrNull(), 1)).build();

    public static void init() {
        Sounds.init();
        Blocks.init();
        Items.init();
        ScreenHandlers.init();
        ItemGroups.init();
        Tiles.init();

        ServerLifecycleEvents.SERVER_STARTED.register(EMCManager::init);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (handler.getPlayer() != null) {
                EMCManager.syncS2C_emc_map(handler.getPlayer());
                //System.out.println(handler.getPlayer().getName().getString() + ", syncS2Cemcmap");
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(id("network"), ((server, p, handler, buf, sender) -> {
            NbtCompound nbt = buf.readNbt();
            if (nbt.contains("control")) {
                Player player = new Player(p);
                int ctrl = nbt.getInt("control");
                if (ctrl == 0) {
                    if (!(player.getCurrentScreenHandler() instanceof AlchemyTableScreenHandler)) return;
                    AlchemyTableScreenHandler screenHandler = (AlchemyTableScreenHandler) player.getCurrentScreenHandler();
                    screenHandler.prevExtractSlots();
                }
                if (ctrl == 1) {
                    if (!(player.getCurrentScreenHandler() instanceof AlchemyTableScreenHandler)) return;
                    AlchemyTableScreenHandler screenHandler = (AlchemyTableScreenHandler) player.getCurrentScreenHandler();
                    screenHandler.nextExtractSlots();
                }
            }
        }));

        ServerPlayNetworking.registerGlobalReceiver(id("search"), ((server, p, handler, buf, sender) -> {
            String text = buf.readString();
            Player player = new Player(p);
            AlchemyTableScreenHandler screenHandler = (AlchemyTableScreenHandler) player.getCurrentScreenHandler();

            // Sort
            screenHandler.setSearchText(text);
            screenHandler.index = 0;
            screenHandler.sortBySearch();
        }));

        registry.allRegister();

        CommandRegistry.register("itemalchemy", new ItemAlchemyCommand());
    }
}
