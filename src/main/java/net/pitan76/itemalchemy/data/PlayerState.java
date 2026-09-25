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
        playerUUID = nbt.has("uuid") ? nbt.getUuid("uuid") : null;
        teamID = nbt.has("team") ? nbt.getUuid("team") : null;
    }

    public void writeNBT(NbtCompound nbt) {
        // nullを書き込むと保存処理全体が失敗するためガードする
        if (playerUUID != null) nbt.putUuid("uuid", playerUUID);
        if (teamID != null) nbt.putUuid("team", teamID);
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
