package net.pitan76.itemalchemy.data;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
        if (server != null && server.toMinecraft() != null)
            return getModStateServer(server);

        return getModStateClient();
    }

    static ModState getModStateServer(MCServer server) {
        return ServerState.getServerState(server);
    }

    @Environment(EnvType.CLIENT)
    static ModState getModStateClient() {
        return new ClientState();
    }
}
