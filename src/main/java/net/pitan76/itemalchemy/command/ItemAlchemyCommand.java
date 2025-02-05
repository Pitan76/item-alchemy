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
import net.pitan76.itemalchemy.config.ItemAlchemyConfig;
import net.pitan76.itemalchemy.data.PlayerState;
import net.pitan76.itemalchemy.data.ServerState;
import net.pitan76.itemalchemy.data.TeamState;
import net.pitan76.itemalchemy.gui.AlchemyTableScreenHandlerFactory;
import net.pitan76.mcpitanlib.api.command.CommandSettings;
import net.pitan76.mcpitanlib.api.command.ConfigCommand;
import net.pitan76.mcpitanlib.api.command.LiteralCommand;
import net.pitan76.mcpitanlib.api.command.argument.IntegerCommand;
import net.pitan76.mcpitanlib.api.command.argument.ItemCommand;
import net.pitan76.mcpitanlib.api.command.argument.PlayerCommand;
import net.pitan76.mcpitanlib.api.command.argument.StringCommand;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.event.*;
import net.pitan76.mcpitanlib.api.offlineplayer.OfflinePlayer;
import net.pitan76.mcpitanlib.api.offlineplayer.OfflinePlayerManager;
import net.pitan76.mcpitanlib.api.util.*;
import net.pitan76.mcpitanlib.api.util.item.ItemUtil;
import net.pitan76.mcpitanlib.midohra.server.MCServer;
import net.pitan76.mcpitanlib.midohra.server.PlayerManager;
import net.pitan76.mcpitanlib.midohra.world.ServerWorld;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
            public void execute(ServerCommandEvent e) {
                if (!e.isClient()) {
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

                    if (e.getWorld() instanceof net.minecraft.server.world.ServerWorld) {
                        ServerWorld serverWorld = ServerWorld.of((net.minecraft.server.world.ServerWorld) e.getWorld());
                        EMCManager.setEmcFromRecipes(serverWorld);
                    }

                    e.sendSuccess("[ItemAlchemy] Reloaded emc_config.json");
                }
            }
        });

        addArgumentCommand("opentable", new LiteralCommand() {
            @Override
            public void init(CommandSettings settings) {
                settings.permissionLevel(2);
            }

            @Override
            public void execute(ServerCommandEvent e) {
                if (!e.isClient()) {
                    try {
                        e.getPlayer().openGuiScreen(new AlchemyTableScreenHandlerFactory());
                    } catch (CommandSyntaxException ex) {
                        e.sendFailure(TextUtil.literal("[ItemAlchemy] " + ex.getMessage()));
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
            public void execute(ServerCommandEvent e) {
                if (!e.isClient()) {

                    File dir = new File(PlatformUtil.getConfigFolderAsFile(), ItemAlchemy.MOD_ID);
                    if (!dir.exists()) dir.mkdirs();
                    
                    File file = new File(dir, "emc_config.json");
                    if (file.exists()) {
                        String fileName = "emc_config_backup_" + DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss").format(LocalDateTime.now()) + ".json";
                        FileControl.fileRename(file, new File(dir, fileName));
                        e.sendSuccess("[ItemAlchemy] Backup emc_config.json as " + fileName);
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
;
                    if (e.getWorld() instanceof net.minecraft.server.world.ServerWorld) {
                        ServerWorld serverWorld = ServerWorld.of((net.minecraft.server.world.ServerWorld) e.getWorld());
                        EMCManager.setEmcFromRecipes(serverWorld);
                    }

                    e.sendSuccess("[ItemAlchemy] Set all emc to default emc");
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
                            public void execute(IntegerCommandEvent e) {
                                Item item = CommandUtil.getItemArgument("item", e);
                                EMCManager.set(item, e.getValue());
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
                    public void execute(ItemCommandEvent e) {
                        e.sendSuccess("[ItemAlchemy] " + ItemUtil.toId(e.getValue()) + ": " + EMCManager.get(e.getValue()) + "EMC");
                    }

                    @Override
                    public String getArgumentName() {
                        return "item";
                    }
                });
            }

            @Override
            public void execute(ServerCommandEvent e) {
                if (!e.isClient()) {
                    e.sendSuccess("[ItemAlchemy] Example: /itemalchemy setemc [Item] [EMC]");
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
                            public void execute(StringCommandEvent e) {
                                try {
                                    if (OfflinePlayerManager.INSTANCE != null)
                                        OfflinePlayerManager.INSTANCE.addPlayer(e.getPlayer().getUUID().toString(), e.getPlayer().getName());

                                    if (!e.isClient()) {
                                        if (TeamUtil.createTeam(e.getPlayer(), e.getValue(), true)) {
                                            e.sendSuccess("[ItemAlchemy] Created Team");
                                            return;
                                        }

                                        e.sendFailure(TextUtil.literal("[ItemAlchemy]§c Failed Create Team"));
                                    }
                                } catch (CommandSyntaxException ex) {
                                    e.sendFailure(TextUtil.literal("[ItemAlchemy] " + ex.getMessage()));
                                }
                            }

                            @Override
                            public String getArgumentName() {
                                return "name";
                            }

                        });
                    }

                    @Override
                    public void execute(ServerCommandEvent e) {
                        e.sendSuccess("[ItemAlchemy] Example: /itemalchemy team create [Team Name]");
                    }
                });

                addArgumentCommand("join", new LiteralCommand() {

                    @Override
                    public void init(CommandSettings settings) {
                        addArgumentCommand("team", new StringCommand() {
                            @Override
                            public void execute(StringCommandEvent e) {
                                try {
                                    if (OfflinePlayerManager.INSTANCE != null)
                                        OfflinePlayerManager.INSTANCE.addPlayer(e.getPlayer().getUUID().toString(), e.getPlayer().getName());

                                    if (!e.isClient()) {
                                        if (TeamUtil.joinTeam(e.getPlayer(), e.getValue())) {
                                            e.sendFailure(TextUtil.literal("[ItemAlchemy] Joined Team"));
                                            return;
                                        }

                                        e.sendSuccess("[ItemAlchemy]§c Failed join");
                                    }
                                } catch (CommandSyntaxException ex) {
                                    e.sendFailure(TextUtil.literal("[ItemAlchemy] " + ex.getMessage()));
                                }
                            }

                            @Override
                            public String getArgumentName() {
                                return "team";
                            }
                        });
                    }

                    @Override
                    public void execute(ServerCommandEvent e) {
                        e.sendSuccess("[ItemAlchemy] Example: /itemalchemy team join [Team Name]");
                    }
                });

                addArgumentCommand("leave", new LiteralCommand() {
                    @Override
                    public void execute(ServerCommandEvent e) {
                        try {
                            if (!e.isClient()) {
                                if (TeamUtil.leaveTeam(e.getPlayer())) {
                                    e.sendSuccess("[ItemAlchemy] Leaved team");
                                    return;
                                }

                                e.sendFailure(TextUtil.literal("[ItemAlchemy]§c Failed leave"));
                            }
                        } catch (CommandSyntaxException ex) {
                            e.sendFailure(TextUtil.literal("[ItemAlchemy] " + ex.getMessage()));
                        }
                    }
                });

                addArgumentCommand("kick", new LiteralCommand() {
                    @Override
                    public void init(CommandSettings settings) {
                        addArgumentCommand("player", new PlayerCommand() {
                            @Override
                            public void execute(PlayerCommandEvent e) {
                                if (e.isClient()) return;

                                try {
                                    Player player = e.getPlayer();
                                    Player targetPlayer = new Player((PlayerEntity) e.getValue());

                                    ServerState serverState = ServerState.getServerState(player.getWorld().getServer());

                                    Optional<TeamState> senderTeam = serverState.getTeamByPlayer(player.getUUID());
                                    Optional<TeamState> targetTeam = serverState.getTeamByPlayer(targetPlayer.getUUID());

                                    if (!senderTeam.isPresent() || !targetTeam.isPresent()) {
                                        e.sendFailure(TextUtil.literal("[ItemAlchemy] Not Found Team"));
                                        return;
                                    }

                                    if (senderTeam.get().owner != player.getUUID()) {
                                        e.sendFailure(TextUtil.literal("[ItemAlchemy] You don't have permission"));
                                        return;
                                    }

                                    if (senderTeam.get().teamID != targetTeam.get().teamID) {
                                        e.sendFailure(TextUtil.literal("[ItemAlchemy] " + targetPlayer.getName() + " is not in your team"));
                                        return;
                                    }

                                    if (TeamUtil.kickTeam(player.getWorld().getServer(), targetTeam.get().teamID, targetPlayer.getUUID())) {
                                        e.sendSuccess("[ItemAlchemy] Kicked " + targetPlayer.getName());
                                        return;
                                    }

                                    e.sendFailure(TextUtil.literal("[ItemAlchemy]§c Failed leave"));

                                } catch (CommandSyntaxException ex) {
                                    e.sendFailure(TextUtil.literal("[ItemAlchemy] " + ex.getMessage()));
                                }
                            }

                            @Override
                            public String getArgumentName() {
                                return "player";
                            }
                        });

                        addArgumentCommand("offlineplayer", new StringCommand() {
                            @Override
                            public void execute(StringCommandEvent e) {
                                if (e.isClient()) return;

                                try {
                                    Player player = e.getPlayer();
                                    OfflinePlayer targetPlayer = OfflinePlayerManager.INSTANCE.getPlayerByName(e.getValue());

                                    if (targetPlayer == null) {
                                        e.sendFailure(TextUtil.literal("[ItemAlchemy] Not registered player in offlineplayer.json"));
                                        return;
                                    }

                                    ServerState serverState = ServerState.getServerState(player.getWorld().getServer());

                                    Optional<TeamState> senderTeam = serverState.getTeamByPlayer(player.getUUID());
                                    Optional<TeamState> targetTeam = serverState.getTeamByPlayer(targetPlayer.getUUID());

                                    if (!senderTeam.isPresent() || !targetTeam.isPresent()) {
                                        e.sendFailure(TextUtil.literal("[ItemAlchemy] Not Found Team"));
                                        return;
                                    }

                                    if (senderTeam.get().owner != player.getUUID()) {
                                        e.sendFailure(TextUtil.literal("[ItemAlchemy] You don't have permission"));
                                        return;
                                    }

                                    if (senderTeam.get().teamID != targetTeam.get().teamID) {
                                        e.sendFailure(TextUtil.literal("[ItemAlchemy] " + targetPlayer.getName() + " is not in your team"));
                                        return;
                                    }

                                    if (TeamUtil.kickTeam(player.getWorld().getServer(), targetTeam.get().teamID, targetPlayer.getUUID())) {
                                        e.sendSuccess("[ItemAlchemy] Kicked " + targetPlayer.getName());
                                        return;
                                    }

                                    e.sendFailure(TextUtil.literal("[ItemAlchemy]§c Failed leave"));

                                } catch (CommandSyntaxException ex) {
                                    e.sendFailure(TextUtil.literal("[ItemAlchemy] " + ex.getMessage()));
                                }
                            }

                            @Override
                            public String getArgumentName() {
                                return "player";
                            }
                        });
                    }

                    @Override
                    public void execute(ServerCommandEvent e) {
                        e.sendSuccess("[ItemAlchemy] Example: /itemalchemy team kick [Player Name]");
                    }
                });

                addArgumentCommand("list", new LiteralCommand() {

                    @Override
                    public void execute(ServerCommandEvent e) {
                        World world = e.getWorld();
                        if (WorldUtil.isClient(world)) return;
                        ServerState serverState = ServerState.getServerState(world.getServer());
                        e.sendSuccess("[ItemAlchemy] Team List");
                        for (TeamState state : serverState.teams) {
                            Optional<MinecraftServer> optionalServer = WorldUtil.getServer(world);
                            if (!optionalServer.isPresent()) continue;

                            MCServer server = MCServer.of(optionalServer.get());
                            PlayerManager playerManager = server.getPlayerManager();

                            if (playerManager.hasPlayerByUUID(state.owner)) {
                                Player player = playerManager.getPlayerByUUID(state.owner);
                                e.sendSuccess("- §a§l" + state.name + "§7 - §rOwner: §c" + player.getName() + "§r");
                            } else {
                                // TODO: This is a bug. The owner is not online, so the owner's name is not displayed.
                                e.sendSuccess("- §a§l" + state.name + "§7 - §rOwner: §c" + state.owner + "§r");
                            }
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
                            public void execute(StringCommandEvent e) {
                                World world = e.getWorld();
                                if (e.isClient()) return;

                                ServerState serverState = ServerState.getServerState(world.getServer());
                                Optional<TeamState> teamState = serverState.getTeamByName(e.getValue());
                                if (!teamState.isPresent()) {
                                    e.sendSuccess("[ItemAlchemy]§c Not found the team named \"" + e.getValue() + "\"");
                                    return;
                                }

                                e.sendSuccess("[ItemAlchemy] §a§l" + e.getValue() + "§r's Members List");

                                List<PlayerState> playerStates = serverState.players.stream().filter(state -> state.teamID.equals(teamState.get().teamID)).collect(Collectors.toList());

                                MCServer server = MCServer.of(world.getServer());
                                PlayerManager playerManager = server.getPlayerManager();
                                for (PlayerState state : playerStates) {
                                    if (!playerManager.hasPlayerByUUID(state.playerUUID))
                                        continue;

                                    Player player = playerManager.getPlayerByUUID(state.playerUUID);
                                    e.sendSuccess("- §c" + player.getName());
                                }
                            }
                        });
                    }

                    @Override
                    public void execute(ServerCommandEvent e) {
                        e.sendSuccess("[ItemAlchemy] Example: /itemalchemy team members [Team Name]");
                    }
                });
            }

            @Override
            public void execute(ServerCommandEvent e) {
                e.sendSuccess("[ItemAlchemy] Example:");
                e.sendSuccess("/itemalchemy team create [Team Name]");
                e.sendSuccess("/itemalchemy team join [Team Name]");
                e.sendSuccess("/itemalchemy team kick [Player Name]");
                e.sendSuccess("/itemalchemy team leave");
                e.sendSuccess("/itemalchemy team list");
                e.sendSuccess("/itemalchemy team members [Team Name]");
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

                        MCServer server = MCServer.of(world.getServer());
                        PlayerManager playerManager = server.getPlayerManager();

                        for (int i = 0; i < 10; i++) {
                            if (playerStates.size() <= i) break;
                            UUID uuid = playerStates.get(i).playerUUID;
                            if (!playerManager.hasPlayerByUUID(uuid)) continue;
                            Player player = playerManager.getPlayerByUUID(uuid);
                            Optional<TeamState> team = serverState.getTeamByPlayer(uuid);

                            if (!team.isPresent())
                                continue;

                            List<Player> players = playerManager.getPlayers();
                            boolean isOwner = (uuid == team.get().owner);
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

                MCServer server = MCServer.of(world.getServer());
                PlayerManager playerManager = server.getPlayerManager();

                for (int i = 0; i < 10; i++) {
                    if (playerStates.size() <= i) break;
                    UUID uuid = playerStates.get(i).playerUUID;
                    if (!playerManager.hasPlayerByUUID(uuid)) continue;
                    Player player = playerManager.getPlayerByUUID(uuid);
                    Optional<TeamState> team = serverState.getTeamByPlayer(uuid);

                    if (!team.isPresent())
                        continue;

                    boolean isOwner = (uuid == team.get().owner);
                    if (isOwner) {
                        e.sendSuccess((i + 1) + ". §a" + player.getName() + " §c(Owner) §r: §a" + playerStates.get(i).getTeamState(world).get().storedEMC + "EMC");
                    } else {
                        e.sendSuccess((i + 1) + ". §a" + player.getName() + " §c(Member) §r: §a" + playerStates.get(i).getTeamState(world).get().storedEMC + "EMC");
                    }
                }

            }
        });

        addArgumentCommand("config", new ConfigCommand(ItemAlchemyConfig.getConfig(), ItemAlchemyConfig.getFile(), "[Item Alchemy]", ItemAlchemyConfig::reset));
    }

    @Override
    public void execute(ServerCommandEvent e) {
        e.sendSuccess("[ItemAlchemy]"
                + "\n- /itemalchemy reloademc...Reload emc_config.json"
                + "\n- /itemalchemy opentable...Reload emc_config.json"
                + "\n- /itemalchemy resetemc...Set all emc to default emc"
                + "\n- /itemalchemy setemc [Item] [EMC]...Set emc of the item"
                + "\n- /itemalchemy team [create | join | kick | leave | list | members] ([Team Name/Player Name])"
                + "\n- /itemalchemy ranking...Show EMC ranking"
                + "\n- /itemalchemy config [set | get | reset | list] ([key])...Config command"
        );
    }
}
