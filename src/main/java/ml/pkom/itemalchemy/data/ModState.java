package ml.pkom.itemalchemy.data;

import java.util.Optional;
import java.util.UUID;

public interface ModState {
    Optional<TeamState> getTeamByPlayer(UUID playerUUID);
    Optional<PlayerState> getPlayer(UUID uuid);
    Optional<TeamState> getTeam(UUID teamID);
}
