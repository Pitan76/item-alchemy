package net.pitan76.itemalchemy.data;

import net.pitan76.mcpitanlib.midohra.server.MCServer;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ModState {
    Optional<TeamState> getTeamByPlayer(UUID playerUUID);
    Optional<PlayerState> getPlayer(UUID uuid);
    Optional<TeamState> getTeam(UUID teamID);

    Optional<TeamState> getTeamByName(String teamName);

    List<TeamState> getTeamsByOwner(UUID playerUUID);

    static ModState getModState(@Nullable MCServer server) {
        if (server != null)
            return ServerState.getServerState(server);

        return new ClientState();
    }
}
