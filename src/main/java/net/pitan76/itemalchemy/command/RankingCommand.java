package net.pitan76.itemalchemy.command;

import net.minecraft.world.World;
import net.pitan76.itemalchemy.data.PlayerState;
import net.pitan76.itemalchemy.data.ServerState;
import net.pitan76.itemalchemy.data.TeamState;
import net.pitan76.mcpitanlib.api.command.CommandSettings;
import net.pitan76.mcpitanlib.api.command.LiteralCommand;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.event.ServerCommandEvent;
import net.pitan76.mcpitanlib.api.util.TextUtil;
import net.pitan76.mcpitanlib.midohra.server.MCServer;
import net.pitan76.mcpitanlib.midohra.server.PlayerManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class RankingCommand extends LiteralCommand {

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
}
