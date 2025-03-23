package net.pitan76.itemalchemy.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.pitan76.itemalchemy.api.TeamUtil;
import net.pitan76.itemalchemy.data.PlayerState;
import net.pitan76.itemalchemy.data.ServerState;
import net.pitan76.itemalchemy.data.TeamState;
import net.pitan76.mcpitanlib.api.command.CommandSettings;
import net.pitan76.mcpitanlib.api.command.LiteralCommand;
import net.pitan76.mcpitanlib.api.command.argument.PlayerCommand;
import net.pitan76.mcpitanlib.api.command.argument.StringCommand;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.event.PlayerCommandEvent;
import net.pitan76.mcpitanlib.api.event.ServerCommandEvent;
import net.pitan76.mcpitanlib.api.event.StringCommandEvent;
import net.pitan76.mcpitanlib.api.offlineplayer.OfflinePlayer;
import net.pitan76.mcpitanlib.api.offlineplayer.OfflinePlayerManager;
import net.pitan76.mcpitanlib.api.util.TextUtil;
import net.pitan76.mcpitanlib.api.util.WorldUtil;
import net.pitan76.mcpitanlib.midohra.server.MCServer;
import net.pitan76.mcpitanlib.midohra.server.PlayerManager;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TeamCommand extends LiteralCommand {

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
}
