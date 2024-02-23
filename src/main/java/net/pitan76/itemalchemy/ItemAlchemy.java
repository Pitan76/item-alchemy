package net.pitan76.itemalchemy;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.pitan76.itemalchemy.api.EMCUtil;
import net.pitan76.itemalchemy.block.Blocks;
import net.pitan76.itemalchemy.command.ItemAlchemyCommand;
import net.pitan76.itemalchemy.data.ServerState;
import net.pitan76.itemalchemy.emcs.itemalchemy.ItemAlchemyEMCDef;
import net.pitan76.itemalchemy.emcs.vanilla.VanillaEMCDef;
import net.pitan76.itemalchemy.gui.screen.AlchemyTableScreenHandler;
import net.pitan76.itemalchemy.gui.screen.ScreenHandlers;
import net.pitan76.itemalchemy.item.ItemGroups;
import net.pitan76.itemalchemy.item.Items;
import net.pitan76.itemalchemy.recipe.AlchemicalRecipeManager;
import net.pitan76.itemalchemy.sound.Sounds;
import net.pitan76.itemalchemy.tile.Tiles;
import net.pitan76.itemalchemy.util.ItemCharge;
import net.pitan76.itemalchemy.util.ItemUtils;
import net.pitan76.mcpitanlib.api.command.CommandRegistry;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.event.v0.EventRegistry;
import net.pitan76.mcpitanlib.api.event.v0.event.ItemStackActionEvent;
import net.pitan76.mcpitanlib.api.event.v1.RecipeManagerRegistry;
import net.pitan76.mcpitanlib.api.network.PacketByteUtil;
import net.pitan76.mcpitanlib.api.network.ServerNetworking;
import net.pitan76.mcpitanlib.api.registry.CompatRegistry;
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

        ServerNetworking.registerReceiver(id("network"), ((server, p, buf) -> {
            NbtCompound nbt = PacketByteUtil.readNbt(buf);
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
            String text = PacketByteUtil.readString(buf);
            NbtCompound translations = PacketByteUtil.readNbt(buf);
            Player player = new Player(p);
            AlchemyTableScreenHandler screenHandler = (AlchemyTableScreenHandler) player.getCurrentScreenHandler();

            // Sort
            screenHandler.setSearchText(text);
            screenHandler.setTranslations(translations);
            screenHandler.index = 0;
            screenHandler.sortBySearch();
        }));

        ServerNetworking.registerReceiver(id("tool_charge"), (server, p, buf) -> {
            Player player = new Player(p);
            ItemStack itemStack = ItemUtils.getCurrentHandItem(p);

            if(itemStack == null) {
                return;
            }

            if(itemStack.getItem() instanceof ItemCharge) {
                int chargeLevel = ItemUtils.getCharge(itemStack);

                int afterChargeLevel = player.isSneaking() ? chargeLevel - 1 : chargeLevel + 1;
                ItemUtils.setCharge(itemStack, afterChargeLevel);

                if (ItemUtils.getCharge(itemStack) == afterChargeLevel) {
                    player.getWorld().playSound(null, player.getBlockPos(), player.isSneaking() ? Sounds.UNCHARGE_SOUND.getOrNull() : Sounds.CHARGE_SOUND.getOrNull(), SoundCategory.PLAYERS, 0.15f, 0.4f + chargeLevel / 5f);
                }
            }
        });

        // Registry commands
        CommandRegistry.register("itemalchemy", new ItemAlchemyCommand());

        registry.allRegister();
    }
}
