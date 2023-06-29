package ml.pkom.itemalchemy.data;

import ml.pkom.itemalchemy.ItemAlchemy;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ServerState extends PersistentState implements ModState {
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
            teamState.readNBT((NbtCompound)item);

            serverState.teams.add(teamState);
        }

        for (NbtElement item : (NbtList)modNBT.get("players")) {
            if(!(item instanceof NbtCompound)) {
                continue;
            }

            PlayerState playerState = new PlayerState();

            playerState.readNBT((NbtCompound)item);

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

            teamState.writeNBT(teamNBT);

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
        PersistentStateManager manager = server.getWorld(World.OVERWORLD).getPersistentStateManager();

        return manager.getOrCreate(
                ServerState::create,
                ServerState::new,
                "itemalchemy"
        );
    }

    public TeamState createTeam(Player owner, @Nullable String name) {
        TeamState team = new TeamState();

        if(name == null) {
            name = owner.getName();
        }

        team.owner = owner.getUUID();
        team.name = name;
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
}
