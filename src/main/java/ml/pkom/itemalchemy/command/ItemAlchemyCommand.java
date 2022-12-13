package ml.pkom.itemalchemy.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import ml.pkom.itemalchemy.EMCManager;
import ml.pkom.itemalchemy.ItemAlchemy;
import ml.pkom.itemalchemy.gui.AlchemyTableScreenHandlerFactory;
import ml.pkom.itemalchemy.gui.screens.AlchemyTableScreenHandler;
import ml.pkom.mcpitanlibarch.api.command.AbstractCommand;
import ml.pkom.mcpitanlibarch.api.event.ServerCommandEvent;
import ml.pkom.mcpitanlibarch.api.util.TextUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import static ml.pkom.itemalchemy.EMCManager.*;

public class ItemAlchemyCommand extends AbstractCommand {

    @Override
    public void init() {
        /*
        addArgumentCommand("setemc", new AbstractCommand() {
            @Override
            public void init() {

            }

            @Override
            public void execute(ServerCommandEvent event) {

            }
        });
         */
        addArgumentCommand("reloademc", new AbstractCommand() {
            @Override
            public void init() {

            }

            @Override
            public void execute(ServerCommandEvent event) {
                if (!event.getWorld().isClient()) {
                    //EMCManager.init(event.getEntity().getServer(), (ServerWorld) event.getWorld());

                    System.out.println("reload emc manager");
                    if (!EMCManager.getMap().isEmpty()) EMCManager.setMap(new LinkedHashMap<>());

                    File dir = new File(FabricLoader.getInstance().getConfigDir().toFile(), ItemAlchemy.MOD_ID);
                    if (!dir.exists()) dir.mkdirs();
                    File file = new File(dir, "emc_config.json");

                    if (file.exists() && config.load(file)) {
                        for (Map.Entry<String, Object> entry : config.configMap.entrySet()) {
                            if (entry.getValue() instanceof Long) {
                                add(entry.getKey(), (Long) entry.getValue());
                            }
                            if (entry.getValue() instanceof Integer) {
                                add(entry.getKey(), Long.valueOf((Integer) entry.getValue()));
                            }
                            if (entry.getValue() instanceof Double) {
                                add(entry.getKey(), (Math.round((Double) entry.getValue())));
                            }
                            if (entry.getValue() instanceof String) {
                                add(entry.getKey(), Long.parseLong((String) entry.getValue()));
                            }
                        }
                    } else {
                        defaultMap();
                        for (Map.Entry<String, Long> entry : getMap().entrySet()) {
                            config.set(entry.getKey(), entry.getValue());
                        }
                        config.save(file);
                    }

                    setEmcFromRecipes(event.getWorld());

                    event.sendSuccess(TextUtil.literal("[ItemAlchemy] Reloaded emc_config.json"), false);
                }
            }
        });

        addArgumentCommand("opentable", new AbstractCommand() {
            @Override
            public void init() {

            }

            @Override
            public void execute(ServerCommandEvent event) {
                if (!event.getWorld().isClient()) {
                    try {
                        event.getPlayer().openGuiScreen(new AlchemyTableScreenHandlerFactory());
                    } catch (CommandSyntaxException e) {
                        event.sendFailure(TextUtil.literal("[ItemAlchemy] " + e.getMessage()));
                    }
                }
            }
        });

    }

    @Override
    public void execute(ServerCommandEvent event) {
        event.sendSuccess(TextUtil.literal("[ItemAlchemy]\n- /itemalchemy reloademc...Reload emc_config.json"), false);
    }
}
