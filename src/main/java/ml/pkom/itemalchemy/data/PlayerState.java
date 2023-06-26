package ml.pkom.itemalchemy.data;

import net.minecraft.nbt.NbtCompound;

import java.util.UUID;

public class PlayerState {
    public UUID playerUUID;
    public UUID teamID;

    public void readNBT(NbtCompound nbt) {
        playerUUID = nbt.getUuid("uuid");
        teamID = nbt.getUuid("team");
    }

    public void writeNBT(NbtCompound nbt) {
        nbt.putUuid("uuid", playerUUID);
        nbt.putUuid("team", teamID);
    }
}
