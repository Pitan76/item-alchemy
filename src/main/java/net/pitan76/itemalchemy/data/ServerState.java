package net.pitan76.itemalchemy.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.util.PersistentStateUtil;
import net.pitan76.mcpitanlib.api.world.CompatiblePersistentState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class ServerState extends CompatiblePersistentState implements ModState {
    public List<TeamState> teams = new ArrayList<>();
    public List<PlayerState> players = new ArrayList<>();

    public static ServerState create(NbtCompound nbt) {
        ServerState serverState = new ServerState();

        NbtCompound modNBT = nbt.getCompound("itemalchemy");

        for (NbtElement item : (NbtList)modNBT.get("teams")) {
            if(!(item instanceof NbtCompound)) {
                continue;
            }

            TeamState teamState = new TeamState();
            teamState.readNbt((NbtCompound)item);

            serverState.teams.add(teamState);
        }

        for (NbtElement item : (NbtList)modNBT.get("players")) {
            if(!(item instanceof NbtCompound)) {
                continue;
            }

            PlayerState playerState = new PlayerState();

            playerState.readNbt((NbtCompound)item);

            serverState.players.add(playerState);
        }

        return serverState;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound modNBT = new NbtCompound();
        NbtList teamNBTList = new NbtList();
        NbtList playerNBTList = new NbtList();

        for (TeamState teamState : teams) {
            NbtCompound teamNBT = new NbtCompound();

            teamState.writeNbt(teamNBT);

            teamNBTList.add(teamNBT);
        }

        for (PlayerState playerState : players) {
            NbtCompound playerNBT = new NbtCompound();

            playerState.writeNBT(playerNBT);

            playerNBTList.add(playerNBT);
        }

        modNBT.put("teams", teamNBTList);
        modNBT.put("players", playerNBTList);
        nbt.put("itemalchemy", modNBT);

        return nbt;
    }

    public static ServerState getServerState(MinecraftServer server) {
        PersistentStateManager manager = PersistentStateUtil.getManagerFromServer(server);

        return PersistentStateUtil.getOrCreate(manager, "itemalchemy", ServerState::new, ServerState::create);
    }

    public TeamState createTeam(Player owner, @Nullable String name) {
        TeamState team = new TeamState();

        if(name == null) {
            team.name = owner.getName();
        } else {
            team.name = name;
            team.isDefault = false;
        }

        team.owner = owner.getUUID();
        team.createdAt = System.currentTimeMillis();
        team.teamID = UUID.randomUUID();

        teams.add(team);

        markDirty();

        return team;
    }

    public PlayerState createPlayer(Player player) {
        if(getPlayer(player.getUUID()).isPresent()) {
            return getPlayer(player.getUUID()).get();
        }

        PlayerState state = new PlayerState();

        state.playerUUID = player.getUUID();

        TeamState team = createTeam(player, null);
        state.teamID = team.teamID;

        players.add(state);

        markDirty();

        return state;
    }

    @Override
    public Optional<TeamState> getTeam(UUID teamID) {
        return teams.stream().filter(teamState -> teamState.teamID.equals(teamID)).findFirst();
    }

    @Override
    public Optional<TeamState> getTeamByName(String teamName) {
        return teams.stream().filter(state -> state.name.equalsIgnoreCase(teamName)).findFirst();
    }

    @Override
    public List<TeamState> getTeamsByOwner(UUID playerUUID) {
        return teams.stream().filter(teamState -> teamState.owner == playerUUID).collect(Collectors.toList());
    }

    @Override
    public Optional<TeamState> getTeamByPlayer(UUID playerUUID) {
        Optional<PlayerState> playerState = getPlayer(playerUUID);

        if(!playerState.isPresent()) {
            return Optional.empty();
        }

        return getTeam(playerState.get().teamID);
    }

    @Override
    public Optional<PlayerState> getPlayer(UUID uuid) {
        return players.stream().filter(playerState -> playerState.playerUUID.equals(uuid)).findFirst();
    }

    @Override
    public void readNbt(NbtCompound tag) {

    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        return null;
    }
}
