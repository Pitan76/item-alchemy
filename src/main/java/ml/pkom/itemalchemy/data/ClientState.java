package ml.pkom.itemalchemy.data;

import ml.pkom.itemalchemy.ItemAlchemyClient;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class ClientState implements ModState {
    @Override
    public Optional<TeamState> getTeamByPlayer(@Nullable UUID playerUUID) {
        if (ItemAlchemyClient.itemAlchemyNbt != null) {
            TeamState team = new TeamState();
            team.readNBT(ItemAlchemyClient.itemAlchemyNbt.getCompound("team"));

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
}
