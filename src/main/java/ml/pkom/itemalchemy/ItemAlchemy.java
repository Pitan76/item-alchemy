package ml.pkom.itemalchemy;

import ml.pkom.itemalchemy.block.Blocks;
import ml.pkom.itemalchemy.command.ItemAlchemyCommand;
import ml.pkom.itemalchemy.gui.screen.AlchemyTableScreenHandler;
import ml.pkom.itemalchemy.gui.screen.ScreenHandlers;
import ml.pkom.itemalchemy.util.ItemCharge;
import ml.pkom.itemalchemy.item.ItemGroups;
import ml.pkom.itemalchemy.item.Items;
import ml.pkom.itemalchemy.sound.Sounds;
import ml.pkom.itemalchemy.tile.Tiles;
import ml.pkom.itemalchemy.util.ItemUtils;
import ml.pkom.mcpitanlibarch.api.command.CommandRegistry;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import ml.pkom.mcpitanlibarch.api.event.v0.EventRegistry;
import ml.pkom.mcpitanlibarch.api.item.CreativeTabBuilder;
import ml.pkom.mcpitanlibarch.api.network.ServerNetworking;
import ml.pkom.mcpitanlibarch.api.registry.ArchRegistry;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
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

    public static ItemGroup ITEM_ALCHEMY = CreativeTabBuilder.create(id("item_alchemy")).setIcon(() -> new ItemStack(Items.PHILOSOPHER_STONE.getOrNull(), 1)).build();

    public static void init() {

        ItemGroups.init();
        Sounds.init();
        Blocks.init();
        Items.init();
        ScreenHandlers.init();
        Tiles.init();

        EventRegistry.ServerLifecycle.serverStarted(EMCManager::init);

        EventRegistry.ServerConnection.join((player) -> {
            if (player != null) {
                EMCManager.syncS2C_emc_map(player);
            }
        });

        EventRegistry.ServerLifecycle.serverStopped(EMCManager::exit);

        ServerNetworking.registerReceiver(id("network"), ((server, p, buf) -> {
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

        ServerNetworking.registerReceiver(id("search"), ((server, p, buf) -> {
            String text = buf.readString();
            Player player = new Player(p);
            AlchemyTableScreenHandler screenHandler = (AlchemyTableScreenHandler) player.getCurrentScreenHandler();

            // Sort
            screenHandler.setSearchText(text);
            screenHandler.index = 0;
            screenHandler.sortBySearch();
        }));

        ServerNetworking.registerReceiver(id("tool_charge"), (server, player, buf) -> {
            ItemStack itemStack = ItemUtils.getCurrentHandItem(player);

            if(itemStack == null) {
                return;
            }

            if(itemStack.getItem() instanceof ItemCharge) {
                int chargeLevel = ItemUtils.getCharge(itemStack);

                int afterChargeLevel = player.isSneaking() ? chargeLevel - 1 : chargeLevel + 1;
                ItemUtils.setCharge(itemStack, afterChargeLevel);

                if (ItemUtils.getCharge(itemStack) == afterChargeLevel) {
                    player.world.playSound(null, player.getBlockPos(), player.isSneaking() ? Sounds.UNCHARGE_SOUND.getOrNull() : Sounds.CHARGE_SOUND.getOrNull(), SoundCategory.PLAYERS, 0.15f, 0.4f + chargeLevel / 5f);
                }
            }
        });

        // Registry commands
        CommandRegistry.register("itemalchemy", new ItemAlchemyCommand());

        registry.allRegister();
    }
}
