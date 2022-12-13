package ml.pkom.itemalchemy.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import ml.pkom.itemalchemy.EMCManager;
import ml.pkom.itemalchemy.ItemAlchemy;
import ml.pkom.itemalchemy.gui.AlchemyTableScreenHandlerFactory;
import ml.pkom.mcpitanlibarch.api.command.CommandSettings;
import ml.pkom.mcpitanlibarch.api.command.LiteralCommand;
import ml.pkom.mcpitanlibarch.api.command.argument.IntegerCommand;
import ml.pkom.mcpitanlibarch.api.command.argument.ItemCommand;
import ml.pkom.mcpitanlibarch.api.event.IntegerCommandEvent;
import ml.pkom.mcpitanlibarch.api.event.ItemCommandEvent;
import ml.pkom.mcpitanlibarch.api.event.ServerCommandEvent;
import ml.pkom.mcpitanlibarch.api.util.ItemUtil;
import ml.pkom.mcpitanlibarch.api.util.TextUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.Item;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import static ml.pkom.itemalchemy.EMCManager.*;

public class ItemAlchemyCommand extends LiteralCommand {

    @Override
    public void init() {
        addArgumentCommand("reloademc", new LiteralCommand() {
            @Override
            public void init(CommandSettings settings) {
                settings.permissionLevel(2);
            }

            @Override
            public void execute(ServerCommandEvent event) {
                if (!event.getWorld().isClient()) {
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

        addArgumentCommand("opentable", new LiteralCommand() {
            @Override
            public void init(CommandSettings settings) {
                settings.permissionLevel(2);
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

        addArgumentCommand("setemc", new LiteralCommand() {
            @Override
            public void init(CommandSettings settings) {
                settings.permissionLevel(2);
                addArgumentCommand("item", new ItemCommand() {

                    @Override
                    public void init(CommandSettings settings) {
                        addArgumentCommand("emc", new IntegerCommand() {
                            @Override
                            public void init(CommandSettings settings) {
                                settings.permissionLevel(2);
                            }

                            @Override
                            public void execute(IntegerCommandEvent event) {
                                Item item = ItemStackArgumentType.getItemStackArgument(event.context, "item").getItem();
                                EMCManager.set(item, event.getValue());
                            }

                            @Override
                            public String getArgumentName() {
                                return "emc";
                            }
                        });
                    }

                    @Override
                    public void execute(ItemCommandEvent event) {
                        event.sendSuccess(TextUtil.literal("[ItemAlchemy] " + ItemUtil.toID(event.getValue()) + ": " + EMCManager.get(event.getValue()) + "EMC"), false);
                    }

                    @Override
                    public String getArgumentName() {
                        return "item";
                    }
                });
            }

            @Override
            public void execute(ServerCommandEvent event) {
                if (!event.getWorld().isClient()) {
                    event.sendSuccess(TextUtil.literal("[ItemAlchemy] Example: /itemalchemy setemc [Item] [EMC]"), false);
                }
            }
        });

    }

    @Override
    public void execute(ServerCommandEvent event) {
        event.sendSuccess(TextUtil.literal("[ItemAlchemy]\n- /itemalchemy reloademc...Reload emc_config.json"), false);
    }
}
