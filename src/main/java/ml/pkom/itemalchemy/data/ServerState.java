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

public class ServerState extends PersistentState {
    public List<TeamState> teams = new ArrayList<>();
    public List<PlayerState> players = new ArrayList<>();

    public static ServerState create(NbtCompound nbt) {
        ServerState serverState = new ServerState();

        NbtCompound modNBT = nbt.getCompound("itemalchemy");

        for (NbtElement item : modNBT.getList("teams", NbtElement.LIST_TYPE)) {
            if(!(item instanceof NbtCompound)) {
                continue;
            }

            TeamState teamState = new TeamState();
            teamState.readNBT((NbtCompound)item);

            serverState.teams.add(teamState);
        }

        for (NbtElement item : modNBT.getList("players", NbtElement.LIST_TYPE)) {
            if(!(item instanceof NbtCompound)) {
                continue;
            }

            NbtCompound playerNBT = (NbtCompound)item;

            PlayerState playerState = new PlayerState();

            playerState.readNBT(playerNBT);

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
                ItemAlchemy.id("stats_nbt").toString()
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

        return team;
    }

    public PlayerState createPlayer(Player player) {
        PlayerState state = new PlayerState();

        state.playerUUID = player.getUUID();

        TeamState team = createTeam(player, null);
        state.teamID = team.teamID;

        players.add(state);

        return state;
    }

    public Optional<TeamState> getTeam(UUID teamID) {
        return teams.stream().filter(teamState -> teamState.teamID == teamID).findFirst();
    }

    public Optional<PlayerState> getPlayer(UUID uuid) {
        return players.stream().filter(playerState -> playerState.playerUUID == uuid).findFirst();
    }
}
