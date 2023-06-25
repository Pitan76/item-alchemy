package ml.pkom.itemalchemy.data;

import com.google.common.collect.Lists;
import ml.pkom.itemalchemy.ItemAlchemy;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class ServerState extends PersistentState {
    public List<TeamState> teams = new ArrayList<>();

    public static ServerState create(NbtCompound nbt) {
        ServerState serverState = new ServerState();

        NbtCompound modNBT = nbt.getCompound("itemalchemy");

        for (NbtElement item : modNBT.getList("teams", NbtElement.COMPOUND_TYPE)) {
            if(!(item instanceof NbtCompound)) {
                continue;
            }

            TeamState teamState = new TeamState();
            NbtCompound teamNBT = (NbtCompound)item;

            teamState.name = teamNBT.getString("name");
            teamState.createdAt = teamNBT.getLong("created_at");
            teamState.teamID = teamNBT.getUuid("id");
            teamState.owner = teamNBT.getUuid("owner");
            teamState.storedEMC = teamNBT.getLong("emc");

            List<UUID> players = teamNBT.getList("players", NbtElement.LIST_TYPE).stream()
                    .filter(nbtElement -> nbtElement instanceof NbtIntArray)
                    .map(nbtElement -> (NbtIntArray)nbtElement)
                    .map(NbtHelper::toUuid)
                    .collect(Collectors.toList());

            List<String> registeredItems = teamNBT.getList("registered_items", NbtElement.LIST_TYPE).stream()
                    .filter(nbtElement -> nbtElement instanceof NbtString)
                    .map(NbtElement::asString)
                    .collect(Collectors.toList());

            teamState.players.addAll(players);
            teamState.registeredItems.addAll(registeredItems);

            serverState.teams.add(teamState);
        }

        return serverState;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound modNBT = new NbtCompound();
        NbtList teamNBTList = new NbtList();

        for (TeamState teamState : teams) {
            NbtCompound teamNBT = new NbtCompound();

            teamNBT.putString("name", teamState.name);
            teamNBT.putLong("created_at", teamState.createdAt);
            teamNBT.putUuid("id", teamState.teamID);
            teamNBT.putUuid("owner", teamState.owner);
            teamNBT.putLong("emc", teamState.storedEMC);

            NbtList players = new NbtList();
            NbtList registeredItems = new NbtList();

            for (UUID player : teamState.players) {
                players.add(NbtHelper.fromUuid(player));
            }

            for (String registeredItem : teamState.registeredItems) {
                registeredItems.add(NbtString.of(registeredItem));
            }

            teamNBT.put("players", players);
            teamNBT.put("registered_items", registeredItems);

            teamNBTList.add(teamNBT);
        }

        modNBT.put("teams", teamNBTList);

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

    public TeamState createTeam(UUID owner, String name) {
        TeamState team = new TeamState();

        team.owner = owner;
        team.name = name;
        team.createdAt = System.currentTimeMillis();
        team.teamID = UUID.randomUUID();

        teams.add(team);

        return team;
    }

    @Nullable
    public TeamState getTeam(UUID teamID) {
        Optional<TeamState> team = teams.stream().filter(teamState -> teamState.teamID == teamID).findFirst();

        return team.orElse(null);
    }
}
