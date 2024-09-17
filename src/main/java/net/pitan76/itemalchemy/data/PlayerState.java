package net.pitan76.itemalchemy.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.util.NbtUtil;
import net.pitan76.mcpitanlib.api.util.PlayerManagerUtil;
import net.pitan76.mcpitanlib.api.util.ServerUtil;

import java.util.Optional;
import java.util.UUID;

public class PlayerState {
    public UUID playerUUID;
    public UUID teamID;

    public void readNbt(NbtCompound nbt) {
        playerUUID = NbtUtil.getUuid(nbt, "uuid");
        teamID = NbtUtil.getUuid(nbt, "team");
    }

    public void writeNBT(NbtCompound nbt) {
        NbtUtil.putUuid(nbt, "uuid", playerUUID);
        NbtUtil.putUuid(nbt, "team", teamID);
    }

    public Player getPlayer(World world) {
        return PlayerManagerUtil.getPlayerByUUID(world, playerUUID);
    }

    public Optional<TeamState> getTeamState(World world) {
        return getTeamState(ServerUtil.getServer(world));
    }

    public Optional<TeamState> getTeamState(MinecraftServer server) {
        ServerState serverState = ServerState.of(server);
        if (serverState == null)
            return Optional.empty();

        return serverState.getTeam(teamID);
    }
}
