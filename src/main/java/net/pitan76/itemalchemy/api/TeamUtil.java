package net.pitan76.itemalchemy.api;

import net.pitan76.itemalchemy.data.PlayerState;
import net.pitan76.itemalchemy.data.ServerState;
import net.pitan76.itemalchemy.data.TeamState;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class TeamUtil {
    public static boolean createTeam(Player player, @Nullable String teamName, Boolean isAutoMove) {
        if(player.isClient()) {
            return false;
        }

        ServerState serverState = ServerState.getServerState(player.getWorld().getServer());

        Optional<PlayerState> playerState = serverState.getPlayer(player.getUUID());

        if(!playerState.isPresent()) {
            return false;
        }

        if(serverState.getTeamsByOwner(player.getUUID()).size() >= 2) {
            return false;
        }

        if(serverState.getTeamByName(teamName).isPresent()) {
            return false;
        }
        
        TeamState teamState = serverState.createTeam(player, teamName);

        if(isAutoMove) {
            joinTeam(player, teamState.teamID);
        }

        return true;
    }

    public static boolean joinTeam(Player player, String teamName) {
        if(player.isClient()) {
            return false;
        }

        ServerState serverState = ServerState.getServerState(player.getWorld().getServer());

        Optional<TeamState> teamState = serverState.getTeamByName(teamName);

        if(!teamState.isPresent()) {
            return false;
        }

        return joinTeam(player, teamState.get().teamID);
    }

    public static boolean joinTeam(Player player, UUID teamUUID) {
        if(player.isClient()) {
            return false;
        }

        ServerState serverState = ServerState.getServerState(player.getWorld().getServer());

        Optional<PlayerState> playerState = serverState.getPlayer(player.getUUID());

        if(!playerState.isPresent()) {
            return false;
        }

        Optional<TeamState> teamState = serverState.getTeam(teamUUID);
        TeamState currentTeamState = serverState.getTeamByPlayer(player.getUUID()).get();

        if(!teamState.isPresent()) {
            return false;
        }

        if(!currentTeamState.isDefault) {
            return false;
        }

        playerState.get().teamID = teamState.get().teamID;

        serverState.markDirty();

        return true;
    }

    public static boolean removeTeam(MinecraftServer server, UUID teamUUID) {
        ServerState serverState = ServerState.getServerState(server);
        Optional<TeamState> teamState = serverState.getTeam(teamUUID);

        if(!teamState.isPresent()) {
            return false;
        }

        List<PlayerState> playerStates = serverState.players.stream().filter(playerState -> playerState.teamID == teamUUID).collect(Collectors.toList());

        for (PlayerState playerState : playerStates) {
            if(!leaveTeam(server, playerState.playerUUID)) {
                return false;
            }
        }

        serverState.teams.remove(teamState.get());

        serverState.markDirty();

        return true;
    }

    public static boolean leaveTeam(MinecraftServer server, UUID playerUUID) {
        ServerState serverState = ServerState.getServerState(server);

        Optional<PlayerState> playerState = serverState.getPlayer(playerUUID);

        if(!playerState.isPresent()) {
            return false;
        }

        TeamState currentTeamState = serverState.getTeamByPlayer(playerUUID).get();
        Optional<TeamState> defaultTeamState = serverState.getTeamsByOwner(playerUUID)
                .stream()
                .filter(state -> state.owner == playerUUID && state.isDefault)
                .findFirst();

        if(currentTeamState.isDefault && currentTeamState.owner == playerUUID) {
            return false;
        }

        if(!defaultTeamState.isPresent()) {
            return false;
        }

        playerState.get().teamID = defaultTeamState.get().teamID;

        if(currentTeamState.owner == playerUUID) {
            removeTeam(server, currentTeamState.teamID);
        }

        serverState.markDirty();

        return true;
    }

    public static boolean leaveTeam(Player player) {
        if(player.isClient()) {
            return false;
        }

        return leaveTeam(player.getWorld().getServer(), player.getUUID());
    }
}
