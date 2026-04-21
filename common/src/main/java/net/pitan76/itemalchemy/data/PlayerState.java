package net.pitan76.itemalchemy.data;

import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.midohra.nbt.NbtCompound;
import net.pitan76.mcpitanlib.midohra.server.MCServer;
import net.pitan76.mcpitanlib.midohra.world.World;

import java.util.Optional;
import java.util.UUID;

public class PlayerState {
    public UUID playerUUID;
    public UUID teamID;

    public void readNbt(NbtCompound nbt) {
        playerUUID = nbt.getUuid("uuid");
        teamID = nbt.getUuid("team");
    }

    public void writeNBT(NbtCompound nbt) {
        nbt.putUuid("uuid", playerUUID);
        nbt.putUuid("team", teamID);
    }

    public Player getPlayer(World world) {
        return world.getPlayerByUUID(playerUUID);
    }

    public Optional<TeamState> getTeamState(World world) {
        return getTeamState(world.getMCServer());
    }

    public Optional<TeamState> getTeamState(MCServer server) {
        ServerState serverState = ServerState.of(server);
        if (serverState == null)
            return Optional.empty();

        return serverState.getTeam(teamID);
    }
}
