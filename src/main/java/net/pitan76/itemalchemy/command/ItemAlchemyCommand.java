package net.pitan76.itemalchemy.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.pitan76.easyapi.FileControl;
import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.itemalchemy.ItemAlchemy;
import net.pitan76.itemalchemy.api.TeamUtil;
import net.pitan76.itemalchemy.data.PlayerState;
import net.pitan76.itemalchemy.data.ServerState;
import net.pitan76.itemalchemy.data.TeamState;
import net.pitan76.itemalchemy.gui.AlchemyTableScreenHandlerFactory;
import net.pitan76.mcpitanlib.api.command.CommandSettings;
import net.pitan76.mcpitanlib.api.command.LiteralCommand;
import net.pitan76.mcpitanlib.api.command.argument.IntegerCommand;
import net.pitan76.mcpitanlib.api.command.argument.ItemCommand;
import net.pitan76.mcpitanlib.api.command.argument.PlayerCommand;
import net.pitan76.mcpitanlib.api.command.argument.StringCommand;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.event.*;
import net.pitan76.mcpitanlib.api.util.*;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
                if (!event.isClient()) {
                    ItemAlchemy.INSTANCE.info("Reload EMCManager");
                    if (!EMCManager.getMap().isEmpty()) EMCManager.setMap(new LinkedHashMap<>());

                    File dir = new File(PlatformUtil.getConfigFolderAsFile(), ItemAlchemy.MOD_ID);
                    if (!dir.exists()) dir.mkdirs();
                    File file = new File(dir, "emc_config.json");

                    if (file.exists() && EMCManager.config.load(file)) {
                        for (Map.Entry<String, Object> entry : EMCManager.config.configMap.entrySet()) {
                            if (entry.getValue() instanceof Long) {
                                EMCManager.add(entry.getKey(), (Long) entry.getValue());
                            }
                            if (entry.getValue() instanceof Integer) {
                                EMCManager.add(entry.getKey(), Long.valueOf((Integer) entry.getValue()));
                            }
                            if (entry.getValue() instanceof Double) {
                                EMCManager.add(entry.getKey(), (Math.round((Double) entry.getValue())));
                            }
                            if (entry.getValue() instanceof String) {
                                EMCManager.add(entry.getKey(), Long.parseLong((String) entry.getValue()));
                            }
                        }
                    } else {
                        EMCManager.defaultMap();
                        for (Map.Entry<String, Long> entry : EMCManager.getMap().entrySet()) {
                            EMCManager.config.set(entry.getKey(), entry.getValue());
                        }
                        EMCManager.config.save(file);
                    }

                    EMCManager.setEmcFromRecipes(event.getWorld());

                    event.sendSuccess("[ItemAlchemy] Reloaded emc_config.json");
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
                if (!event.isClient()) {
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
                if (!event.isClient()) {

                    File dir = new File(PlatformUtil.getConfigFolderAsFile(), ItemAlchemy.MOD_ID);
                    if (!dir.exists()) dir.mkdirs();
                    
                    File file = new File(dir, "emc_config.json");
                    if (file.exists()) {
                        String fileName = "emc_config_backup_" + DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss").format(LocalDateTime.now()) + ".json";
                        FileControl.fileRename(file, new File(dir, fileName));
                        event.sendSuccess("[ItemAlchemy] Backup emc_config.json as " + fileName);
                    }

                    ItemAlchemy.logger.info("Reload EMCManager");

                    if (!EMCManager.getMap().isEmpty()) EMCManager.setMap(new LinkedHashMap<>());

                    if (file.exists() && EMCManager.config.load(file)) {
                        for (Map.Entry<String, Object> entry : EMCManager.config.configMap.entrySet()) {
                            if (entry.getValue() instanceof Long) {
                                EMCManager.add(entry.getKey(), (Long) entry.getValue());
                            }
                            if (entry.getValue() instanceof Integer) {
                                EMCManager.add(entry.getKey(), Long.valueOf((Integer) entry.getValue()));
                            }
                            if (entry.getValue() instanceof Double) {
                                EMCManager.add(entry.getKey(), (Math.round((Double) entry.getValue())));
                            }
                            if (entry.getValue() instanceof String) {
                                EMCManager.add(entry.getKey(), Long.parseLong((String) entry.getValue()));
                            }
                        }
                    } else {
                        EMCManager.defaultMap();
                        for (Map.Entry<String, Long> entry : EMCManager.getMap().entrySet()) {
                            EMCManager.config.set(entry.getKey(), entry.getValue());
                        }
                        EMCManager.config.save(file);
                    }

                    EMCManager.setEmcFromRecipes(event.getWorld());

                    event.sendSuccess("[ItemAlchemy] Set all emc to default emc");
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
                                Item item = CommandUtil.getItemArgument("item", event);
                                EMCManager.set(item, event.getValue());
                                for (Map.Entry<String, Long> entry : EMCManager.getMap().entrySet()) {
                                    EMCManager.config.set(entry.getKey(), entry.getValue());
                                }
                                EMCManager.config.save(EMCManager.getConfigFile());
                            }

                            @Override
                            public String getArgumentName() {
                                return "emc";
                            }
                        });
                    }

                    @Override
                    public void execute(ItemCommandEvent event) {
                        event.sendSuccess("[ItemAlchemy] " + ItemUtil.toCompatID(event.getValue()) + ": " + EMCManager.get(event.getValue()) + "EMC");
                    }

                    @Override
                    public String getArgumentName() {
                        return "item";
                    }
                });
            }

            @Override
            public void execute(ServerCommandEvent event) {
                if (!event.isClient()) {
                    event.sendSuccess("[ItemAlchemy] Example: /itemalchemy setemc [Item] [EMC]");
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
                                    if (!event.isClient()) {
                                        if (TeamUtil.createTeam(event.getPlayer(), event.getValue(), true)) {
                                            event.sendSuccess("[ItemAlchemy] Created Team");
                                            return;
                                        }

                                        event.sendFailure(TextUtil.literal("[ItemAlchemy]§c Failed Create Team"));
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
                        event.sendSuccess("[ItemAlchemy] Example: /itemalchemy team create [Team Name]");
                    }
                });

                addArgumentCommand("join", new LiteralCommand() {

                    @Override
                    public void init(CommandSettings settings) {
                        addArgumentCommand("team", new StringCommand() {
                            @Override
                            public void execute(StringCommandEvent event) {
                                try {
                                    if (!event.isClient()) {
                                        if (TeamUtil.joinTeam(event.getPlayer(), event.getValue())) {
                                            event.sendFailure(TextUtil.literal("[ItemAlchemy] Joined Team"));
                                            return;
                                        }

                                        event.sendSuccess("[ItemAlchemy]§c Failed join");
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
                        event.sendSuccess("[ItemAlchemy] Example: /itemalchemy team join [Team Name]");
                    }
                });

                addArgumentCommand("leave", new LiteralCommand() {
                    @Override
                    public void execute(ServerCommandEvent event) {
                        try {
                            if (!event.isClient()) {
                                if (TeamUtil.leaveTeam(event.getPlayer())) {
                                    event.sendSuccess("[ItemAlchemy] Leaved team");
                                    return;
                                }

                                event.sendFailure(TextUtil.literal("[ItemAlchemy]§c Failed leave"));
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
                                if (event.isClient()) return;

                                try {
                                    Player player = event.getPlayer();
                                    Player targetPlayer = new Player((PlayerEntity) event.getValue());

                                    ServerState serverState = ServerState.getServerState(player.getWorld().getServer());

                                    Optional<TeamState> senderTeam = serverState.getTeamByPlayer(player.getUUID());
                                    Optional<TeamState> targetTeam = serverState.getTeamByPlayer(targetPlayer.getUUID());

                                    if (!senderTeam.isPresent() || !targetTeam.isPresent()) {
                                        event.sendFailure(TextUtil.literal("[ItemAlchemy] Not Found Team"));
                                        return;
                                    }

                                    if (senderTeam.get().owner != player.getUUID()) {
                                        event.sendFailure(TextUtil.literal("[ItemAlchemy] You don't have permission"));
                                        return;
                                    }

                                    if (senderTeam.get().teamID != targetTeam.get().teamID) {
                                        event.sendFailure(TextUtil.literal("[ItemAlchemy] " + targetPlayer.getName() + " is not in your team"));
                                        return;
                                    }

                                    if (TeamUtil.kickTeam(player.getWorld().getServer(), targetTeam.get().teamID, targetPlayer.getUUID())) {
                                        event.sendSuccess("[ItemAlchemy] Kicked " + targetPlayer.getName());
                                        return;
                                    }

                                    event.sendFailure(TextUtil.literal("[ItemAlchemy]§c Failed leave"));

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
                        event.sendSuccess("[ItemAlchemy] Example: /itemalchemy team kick [Player Name]");
                    }
                });

                addArgumentCommand("list", new LiteralCommand() {

                    @Override
                    public void execute(ServerCommandEvent event) {
                        World world = event.getWorld();
                        if (WorldUtil.isClient(world)) return;
                        ServerState serverState = ServerState.getServerState(world.getServer());
                        event.sendSuccess("[ItemAlchemy] Team List");
                        for (TeamState state : serverState.teams) {
                            Optional<MinecraftServer> optionalServer = WorldUtil.getServer(world);
                            if (!optionalServer.isPresent()) continue;

                            PlayerEntity playerEntity = optionalServer.get().getPlayerManager().getPlayer(state.owner);
                            if (playerEntity == null) continue;
                            Player player = new Player(playerEntity);

                            event.sendSuccess("- §a§l" + state.name + "§7 - §rOwner: §c" + player.getName() + "§r");
                        }
                    }
                });

                addArgumentCommand("members", new LiteralCommand() {
                    @Override
                    public void init() {
                        addArgumentCommand("team", new StringCommand() {
                            @Override
                            public String getArgumentName() {
                                return "team";
                            }

                            @Override
                            public void execute(StringCommandEvent event) {
                                World world = event.getWorld();
                                if (event.isClient()) return;

                                ServerState serverState = ServerState.getServerState(world.getServer());
                                Optional<TeamState> teamState = serverState.getTeamByName(event.getValue());
                                if(!teamState.isPresent()) {
                                    event.sendSuccess("[ItemAlchemy]§c Not found the team named \"" + event.getValue() + "\"");
                                    return;
                                }

                                event.sendSuccess("[ItemAlchemy] §a§l" + event.getValue() + "§r's Members List");

                                List<PlayerState> playerStates = serverState.players.stream().filter(state -> state.teamID.equals(teamState.get().teamID)).collect(Collectors.toList());

                                for (PlayerState state : playerStates) {
                                    PlayerEntity playerEntity = world.getServer().getPlayerManager().getPlayer(state.playerUUID);
                                    if (playerEntity == null) continue;
                                    Player player = new Player(playerEntity);
                                    event.sendSuccess("- §c" + player.getName());
                                }
                            }
                        });
                    }

                    @Override
                    public void execute(ServerCommandEvent event) {
                        event.sendSuccess("[ItemAlchemy] Example: /itemalchemy team members [Team Name]");
                    }
                });
            }

            @Override
            public void execute(ServerCommandEvent event) {
                event.sendSuccess("[ItemAlchemy] Example:");
                event.sendSuccess("/itemalchemy team create [Team Name]");
                event.sendSuccess("/itemalchemy team join [Team Name]");
                event.sendSuccess("/itemalchemy team kick [Player Name]");
                event.sendSuccess("/itemalchemy team leave");
                event.sendSuccess("/itemalchemy team list");
                event.sendSuccess("/itemalchemy team members [Team Name]");
            }
        });

        addArgumentCommand("ranking", new LiteralCommand() {

            @Override
            public void init(CommandSettings settings) {
                super.init(settings);
                settings.permissionLevel(2);

                addArgumentCommand("broadcast", new LiteralCommand() {
                    @Override
                    public void execute(ServerCommandEvent e) {
                        if (e.isClient()) return;
                        World world = e.getWorld();
                        ServerState serverState = ServerState.getServerState(world.getServer());
                        List<PlayerState> playerStates = serverState.players.stream().sorted((o1, o2) -> Long.compare(o2.getTeamState(world).get().storedEMC, o1.getTeamState(world).get().storedEMC)).collect(Collectors.toList());

                        e.sendSuccess("[ItemAlchemy] EMC Ranking");
                        for (int i = 0; i < 10; i++) {
                            if (playerStates.size() <= i) break;
                            Player player = PlayerManagerUtil.getPlayerByUUID(world, playerStates.get(i).playerUUID);
                            if (player.getEntity() == null) continue;

                            boolean isOwner = playerStates.get(i).playerUUID == serverState.getTeamByPlayer(player.getUUID()).get().owner;

                            List<Player> players = PlayerManagerUtil.getPlayers(world);
                            if (isOwner) {
                                for (Player p : players) {
                                    p.sendMessage(TextUtil.literal((i + 1) + ". §a" + player.getName() + " §c(Owner) §r: §a" + playerStates.get(i).getTeamState(world).get().storedEMC + "EMC"));
                                }
                            } else {
                                for (Player p : players) {
                                    p.sendMessage(TextUtil.literal((i + 1) + ". §a" + player.getName() + " §c(Member) §r: §a" + playerStates.get(i).getTeamState(world).get().storedEMC + "EMC"));
                                }
                            }

                        }
                    }
                });
            }

            @Override
            public void execute(ServerCommandEvent e) {
                if (e.isClient()) return;
                World world = e.getWorld();
                ServerState serverState = ServerState.getServerState(world.getServer());
                List<PlayerState> playerStates = serverState.players.stream().sorted((o1, o2) -> Long.compare(o2.getTeamState(world).get().storedEMC, o1.getTeamState(world).get().storedEMC)).collect(Collectors.toList());

                e.sendSuccess("[ItemAlchemy] EMC Ranking");
                for (int i = 0; i < 10; i++) {
                    if (playerStates.size() <= i) break;
                    Player player = PlayerManagerUtil.getPlayerByUUID(world, playerStates.get(i).playerUUID);
                    if (player.getEntity() == null) continue;

                    boolean isOwner = playerStates.get(i).playerUUID == serverState.getTeamByPlayer(player.getUUID()).get().owner;

                    if (isOwner) {
                        e.sendSuccess((i + 1) + ". §a" + player.getName() + " §c(Owner) §r: §a" + playerStates.get(i).getTeamState(world).get().storedEMC + "EMC");
                    } else {
                        e.sendSuccess((i + 1) + ". §a" + player.getName() + " §c(Member) §r: §a" + playerStates.get(i).getTeamState(world).get().storedEMC + "EMC");
                    }
                }

            }
        });
    }

    @Override
    public void execute(ServerCommandEvent event) {
        event.sendSuccess("[ItemAlchemy]"
                + "\n- /itemalchemy reloademc...Reload emc_config.json"
                + "\n- /itemalchemy opentable...Reload emc_config.json"
                + "\n- /itemalchemy resetemc...Set all emc to default emc"
                + "\n- /itemalchemy setemc [Item] [EMC]...Set emc of the item"
                + "\n- /itemalchemy team [create | join | kick | leave | list | members] ([Team Name/Player Name])"
                + "\n- /itemalchemy ranking...Show EMC ranking"
        );
    }
}
