package net.pitan76.itemalchemy.data;


import net.pitan76.itemalchemy.ItemAlchemy;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.event.nbt.ReadNbtArgs;
import net.pitan76.mcpitanlib.api.event.nbt.WriteNbtArgs;
import net.pitan76.mcpitanlib.api.util.NbtUtil;
import net.pitan76.mcpitanlib.midohra.nbt.NbtCompound;
import net.pitan76.mcpitanlib.midohra.nbt.NbtElement;
import net.pitan76.mcpitanlib.midohra.nbt.NbtList;
import net.pitan76.mcpitanlib.midohra.server.MCServer;
import net.pitan76.mcpitanlib.midohra.world.CompatPersistentState;
import net.pitan76.mcpitanlib.midohra.world.PersistentStateManager;
import net.pitan76.mcpitanlib.midohra.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class ServerState extends CompatPersistentState implements ModState {
    public List<TeamState> teams = new ArrayList<>();
    public List<PlayerState> players = new ArrayList<>();

    public ServerState() {
//        TODO: super("itemalchemy");
        super();
    }

    public static ServerState create(NbtCompound nbt) {
        ServerState serverState = new ServerState();
        serverState.readNbt(new ReadNbtArgs(nbt));

        return serverState;
    }

    /*
    itemalchemy (NbtCompound: modNBT):
      teams (NbtList: teamNBTList):
        (NbtCompound: teamNBT)
        - name: "(Player Name)"
          created_at: (Created At)
          id: (Team UUID)
          owner: (Owner UUID)
          emc: (Stored EMC)
          is_default: (isDefault)
          registered_items: (NbtList: registeredItems)

      players (NbtList: playerNBTList):
        (NbtCompound: playerNBT)
        - uuid: (Player UUID)
          team: (Team UUID)

     */

    @Override
    public NbtCompound writeNbtM(WriteNbtArgs args) {
        NbtCompound nbt = args.getNbtM();

        NbtCompound modNBT = NbtCompound.of();
        NbtList teamNBTList = NbtList.of(NbtUtil.createNbtList()); // TODO: impl NbtList.of();
        NbtList playerNBTList = NbtList.of(NbtUtil.createNbtList());

        for (TeamState teamState : teams) {
            NbtCompound teamNBT = NbtCompound.of();
            teamState.writeNbt(teamNBT);
            teamNBTList.add(teamNBT.toElement());
        }

        for (PlayerState playerState : players) {
            NbtCompound playerNBT = NbtCompound.of();
            playerState.writeNBT(playerNBT);

            playerNBTList.add(playerNBT.toElement()); // TODO: impl NbtList.add(ElementConvertible);
        }

        modNBT.put("teams", teamNBTList);
        modNBT.put("players", playerNBTList);
        nbt.put("itemalchemy", modNBT);

        ItemAlchemy.logger.infoIfDev("ServerState.writeNbt: " + args.getNbt().toString());

        return nbt;
    }

    @Override
    public void readNbt(ReadNbtArgs args) {
        ItemAlchemy.logger.infoIfDev("ServerState.readNbt: " + args.getNbt().toString());

        NbtCompound nbt = args.getNbtM();
        NbtCompound modNBT = nbt.getCompound("itemalchemy");

        NbtList teamNBTList = NbtList.of(NbtUtil.getNbtCompoundList(modNBT.toMinecraft(), "teams")); // TODO: impl nbt.getList;
        for (NbtElement teamNbt : teamNBTList) {
            TeamState teamState = new TeamState();
            teamState.readNbt(teamNbt.asNbtCompound());

            teams.add(teamState);
        }

        NbtList playerNBTList = NbtList.of(NbtUtil.getNbtCompoundList(modNBT.toMinecraft(), "players")); // TODO: impl nbt.getList;
        for (NbtElement playerNbt : playerNBTList) {
            PlayerState playerState = new PlayerState();
            playerState.readNbt(playerNbt.asNbtCompound());

            players.add(playerState);
        }
    }

    public static ServerState getServerState(MCServer server) {
        PersistentStateManager manager = server.getPersistentStateManager();

        return manager.getOrCreateCompatiblePersistentState("itemalchemy", ServerState::new, ServerState::create);
    }

    public static ServerState of(MCServer server) {
        if (server == null) return null;

        return getServerState(server);
    }

    public static ServerState of(World world) {
        return of(world.getMCServer());
    }

    public static ServerState of(Player player) {
        return of(player.getMidohraWorld());
    }

    public TeamState createTeam(Player owner, @Nullable String name) {
        TeamState team = new TeamState();

        if (name == null) {
            team.name = owner.getName();
        } else {
            team.name = name;
            team.isDefault = false;
        }

        team.owner = owner.getUUID();
        team.createdAt = System.currentTimeMillis();
        team.teamID = UUID.randomUUID();

        teams.add(team);

        callMarkDirty();

        return team;
    }

    public PlayerState createPlayer(Player player) {
        if (getPlayer(player.getUUID()).isPresent())
            return getPlayer(player.getUUID()).get();

        PlayerState state = new PlayerState();

        state.playerUUID = player.getUUID();

        TeamState team = createTeam(player, null);
        state.teamID = team.teamID;

        players.add(state);

        callMarkDirty();

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

        if (!playerState.isPresent())
            return Optional.empty();

        return getTeam(playerState.get().teamID);
    }

    @Override
    public Optional<PlayerState> getPlayer(UUID uuid) {
        return players.stream().filter(playerState -> playerState.playerUUID.equals(uuid)).findFirst();
    }
}
