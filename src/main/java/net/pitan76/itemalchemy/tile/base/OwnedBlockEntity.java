package net.pitan76.itemalchemy.tile.base;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.pitan76.itemalchemy.data.PlayerState;
import net.pitan76.itemalchemy.data.ServerState;
import net.pitan76.itemalchemy.data.TeamState;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent;
import net.pitan76.mcpitanlib.api.util.BlockEntityUtil;

import java.util.Optional;
import java.util.UUID;

public abstract class OwnedBlockEntity extends EMCStorageBlockEntity {

    public UUID teamUUID = null;

    public OwnedBlockEntity(BlockEntityType<?> type, TileCreateEvent event) {
        super(type, event);
    }

    public OwnedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public Optional<TeamState> getTeamState() {
        if (callGetWorld() == null) return Optional.empty();

        ServerState serverState = ServerState.of(callGetWorld());
        if (serverState == null || teamUUID == null)
            return Optional.empty();

        return serverState.getTeam(teamUUID);
    }

    public boolean isTeamOwner(Player player) {
        return getTeamState().map(state -> state.isOwner(player)).orElse(false);
    }

    public boolean isTeamMember(Player player) {
        return getTeamState().map(state -> state.isMember(player)).orElse(false);
    }

    public boolean hasTeam() {
        if (teamUUID == null) return false;
        return getTeamState().isPresent();
    }

    public UUID getTeam() {
        return teamUUID;
    }

    public void setTeam(UUID teamUUID) {
        this.teamUUID = teamUUID;
        BlockEntityUtil.markDirty(this);
    }

    /**
     * Set team by player
     * @param player Player
     * @return boolean success or fail
     */
    public boolean setTeam(Player player) {
        ServerState serverState = ServerState.of(callGetWorld());
        if (serverState == null) return false;

        Optional<PlayerState> optionalPlayerState = serverState.getPlayer(player.getUUID());
        if (!optionalPlayerState.isPresent()) return false;

        PlayerState playerState = optionalPlayerState.get();

        if (playerState.teamID == null) return false;
        setTeam(playerState.teamID);

        return true;
    }
}
