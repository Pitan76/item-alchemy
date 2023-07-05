package ml.pkom.itemalchemy.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import ml.pkom.easyapi.FileControl;
import ml.pkom.itemalchemy.EMCManager;
import ml.pkom.itemalchemy.ItemAlchemy;
import ml.pkom.itemalchemy.api.TeamUtil;
import ml.pkom.itemalchemy.data.ServerState;
import ml.pkom.itemalchemy.data.TeamState;
import ml.pkom.itemalchemy.gui.AlchemyTableScreenHandlerFactory;
import ml.pkom.mcpitanlibarch.api.command.CommandSettings;
import ml.pkom.mcpitanlibarch.api.command.LiteralCommand;
import ml.pkom.mcpitanlibarch.api.command.argument.IntegerCommand;
import ml.pkom.mcpitanlibarch.api.command.argument.ItemCommand;
import ml.pkom.mcpitanlibarch.api.command.argument.PlayerCommand;
import ml.pkom.mcpitanlibarch.api.command.argument.StringCommand;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import ml.pkom.mcpitanlibarch.api.event.*;
import ml.pkom.mcpitanlibarch.api.util.ItemUtil;
import ml.pkom.mcpitanlibarch.api.util.TextUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

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

        addArgumentCommand("resetemc", new LiteralCommand() {
            @Override
            public void init(CommandSettings settings) {
                settings.permissionLevel(2);
            }

            @Override
            public void execute(ServerCommandEvent event) {
                if (!event.getWorld().isClient()) {

                    File dir = new File(FabricLoader.getInstance().getConfigDir().toFile(), ItemAlchemy.MOD_ID);
                    if (!dir.exists()) dir.mkdirs();
                    File file = new File(dir, "emc_config.json");
                    if (file.exists()) {
                        String fileName = "emc_config_backup_" + DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss").format(LocalDateTime.now()) + ".json";
                        FileControl.fileRename(file, new File(dir, fileName));
                        event.sendSuccess(TextUtil.literal("[ItemAlchemy] Backup emc_config.json as " + fileName), false);
                    }

                    System.out.println("reload emc manager");
                    if (!EMCManager.getMap().isEmpty()) EMCManager.setMap(new LinkedHashMap<>());

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

                    event.sendSuccess(TextUtil.literal("[ItemAlchemy] Set all emc to default emc"), false);
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
                                for (Map.Entry<String, Long> entry : getMap().entrySet()) {
                                    config.set(entry.getKey(), entry.getValue());
                                }
                                config.save(getConfigFile());
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

        addArgumentCommand("team", new LiteralCommand() {
            @Override
            public void init(CommandSettings settings) {
                addArgumentCommand("create", new LiteralCommand() {
                    @Override
                    public void init(CommandSettings settings) {
                        addArgumentCommand("name", new StringCommand() {
                            @Override
                            public void execute(StringCommandEvent event) {
                                try {
                                    if (!event.getWorld().isClient()) {
                                        if(TeamUtil.createTeam(event.getPlayer(), event.getValue(), true)) {
                                            event.sendSuccess(TextUtil.literal("[ItemAlchemy] Created Team"), false);

                                            return;
                                        }

                                        event.sendFailure(TextUtil.literal("[ItemAlchemy] Failed Create Team"));
                                    }
                                } catch (CommandSyntaxException e) {
                                    event.sendFailure(TextUtil.literal("[ItemAlchemy] " + e.getMessage()));
                                }
                            }

                            @Override
                            public String getArgumentName() {
                                return "name";
                            }

                        });
                    }

                    @Override
                    public void execute(ServerCommandEvent event) {
                        event.sendSuccess(TextUtil.literal("[ItemAlchemy] Example: /itemalchemy team create [Team Name]"), false);
                    }
                });

                addArgumentCommand("join", new LiteralCommand() {

                    @Override
                    public void init(CommandSettings settings) {
                        addArgumentCommand("team", new StringCommand() {
                            @Override
                            public void execute(StringCommandEvent event) {
                                try {
                                    if (!event.getWorld().isClient()) {
                                        if(TeamUtil.joinTeam(event.getPlayer(), event.getValue())) {
                                            event.sendFailure(TextUtil.literal("[ItemAlchemy] Joined Team"));

                                            return;
                                        }

                                        event.sendSuccess(TextUtil.literal("[ItemAlchemy] Failed join"), false);
                                    }
                                } catch (CommandSyntaxException e) {
                                    event.sendFailure(TextUtil.literal("[ItemAlchemy] " + e.getMessage()));
                                }
                            }

                            @Override
                            public String getArgumentName() {
                                return "team";
                            }
                        });
                    }

                    @Override
                    public void execute(ServerCommandEvent event) {
                        event.sendSuccess(TextUtil.literal("[ItemAlchemy] Example: /itemalchemy team join [Team Name]"), false);
                    }
                });

                addArgumentCommand("leave", new LiteralCommand() {
                    @Override
                    public void execute(ServerCommandEvent event) {
                        try {
                            if (!event.getWorld().isClient()) {
                                if(TeamUtil.leaveTeam(event.getPlayer())) {
                                    event.sendSuccess(TextUtil.literal("[ItemAlchemy] Leaved team"), false);

                                    return;
                                }

                                event.sendFailure(TextUtil.literal("[ItemAlchemy] Failed leave"));
                            }
                        } catch (CommandSyntaxException e) {
                            event.sendFailure(TextUtil.literal("[ItemAlchemy] " + e.getMessage()));
                        }
                    }
                });

                addArgumentCommand("kick", new LiteralCommand() {
                    @Override
                    public void init(CommandSettings settings) {
                        addArgumentCommand("player", new PlayerCommand() {
                            @Override
                            public void execute(PlayerCommandEvent event) {
                                try {
                                    if (!event.getWorld().isClient()) {
                                        Player player = event.getPlayer();
                                        Player targetPlayer = new Player((PlayerEntity) event.getValue());

                                        ServerState serverState = ServerState.getServerState(player.getWorld().getServer());
                                        Optional<TeamState> teamState = serverState.getTeamByPlayer(player.getUUID());

                                        if(!teamState.isPresent()) {
                                            event.sendFailure(TextUtil.literal("[ItemAlchemy] Not Found Team"));

                                            return;
                                        }

                                        if(teamState.get().owner != player.getUUID()) {
                                            event.sendFailure(TextUtil.literal("[ItemAlchemy] You don't have permission"));

                                            return;
                                        }

                                        if(!serverState.getPlayer(targetPlayer.getUUID()).filter(playerState -> playerState.teamID == teamState.get().teamID).isPresent()) {
                                            event.sendFailure(TextUtil.literal("[ItemAlchemy] You don't have permission"));

                                            return;
                                        }

                                        if(TeamUtil.leaveTeam(targetPlayer)) {
                                            event.sendSuccess(TextUtil.literal("[ItemAlchemy] Kicked " + player.getName()), false);

                                            return;
                                        }

                                        event.sendFailure(TextUtil.literal("[ItemAlchemy] Failed leave"));
                                    }
                                } catch (CommandSyntaxException e) {
                                    event.sendFailure(TextUtil.literal("[ItemAlchemy] " + e.getMessage()));
                                }
                            }

                            @Override
                            public String getArgumentName() {
                                return "player";
                            }
                        });
                    }

                    @Override
                    public void execute(ServerCommandEvent event) {
                        event.sendSuccess(TextUtil.literal("[ItemAlchemy] Example: /itemalchemy team kick [Player Name]"), false);
                    }
                });
            }

            @Override
            public void execute(ServerCommandEvent event) {
                event.sendSuccess(TextUtil.literal("[ItemAlchemy] Example:"), false);
                event.sendSuccess(TextUtil.literal("/itemalchemy team create [Team Name]"), false);
                event.sendSuccess(TextUtil.literal("/itemalchemy team join [Team Name]"), false);
                event.sendSuccess(TextUtil.literal("/itemalchemy team kick [Player Name]"), false);
                event.sendSuccess(TextUtil.literal("/itemalchemy team leave"), false);
            }
        });
    }

    @Override
    public void execute(ServerCommandEvent event) {
        event.sendSuccess(TextUtil.literal("[ItemAlchemy]\n- /itemalchemy reloademc...Reload emc_config.json"), false);
    }
}
