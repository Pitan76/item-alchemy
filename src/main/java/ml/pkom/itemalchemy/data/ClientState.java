package ml.pkom.itemalchemy.data;

import ml.pkom.itemalchemy.ItemAlchemyClient;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ClientState implements ModState {
    @Override
    public Optional<TeamState> getTeamByPlayer(UUID playerUUID) {
        if (ItemAlchemyClient.itemAlchemyNbt != null) {
            TeamState team = new TeamState();
            team.readNbt(ItemAlchemyClient.itemAlchemyNbt.getCompound("team"));

            return Optional.of(team);
        }

        return Optional.empty();
    }

    @Override
    public Optional<PlayerState> getPlayer(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public Optional<TeamState> getTeam(UUID teamID) {
        return Optional.empty();
    }

    @Override
    public Optional<TeamState> getTeamByName(String teamName) {
        return Optional.empty();
    }

    @Override
    public List<TeamState> getTeamsByOwner(UUID playerUUID) {
        return Collections.emptyList();
    }
}
