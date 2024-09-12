package net.pitan76.itemalchemy.data;

import net.minecraft.nbt.NbtCompound;
import net.pitan76.mcpitanlib.api.util.NbtUtil;

import java.util.UUID;

public class PlayerState {
    public UUID playerUUID;
    public UUID teamID;

    public void readNbt(NbtCompound nbt) {
        playerUUID = NbtUtil.getUuid(nbt, "uuid");
        teamID = NbtUtil.getUuid(nbt, "team");
    }

    public void writeNBT(NbtCompound nbt) {
        NbtUtil.putUuid(nbt, "uuid", playerUUID);
        NbtUtil.putUuid(nbt, "team", teamID);
    }
}
