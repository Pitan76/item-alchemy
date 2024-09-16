package net.pitan76.itemalchemy.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.pitan76.itemalchemy.ItemAlchemy;
import net.pitan76.mcpitanlib.api.util.NbtUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TeamState {
    public String name;
    public long createdAt;
    public UUID teamID;
    public UUID owner;
    public long storedEMC = 0;
    public boolean isDefault = true;
    public List<String> registeredItems = new ArrayList<>();

    public void readNbt(NbtCompound nbt) {
        name = NbtUtil.getString(nbt, "name");
        createdAt = NbtUtil.getLong(nbt, "created_at");
        teamID = NbtUtil.getUuid(nbt, "id");
        owner = NbtUtil.getUuid(nbt, "owner");
        storedEMC = NbtUtil.getLong(nbt, "emc");
        isDefault = NbtUtil.getBoolean(nbt, "is_default");

        ItemAlchemy.logger.infoIfDev("TeamState.readNbt(): nbt: " + nbt);

        List<String> registeredItems = (NbtUtil.getList(nbt, "registered_items")).stream()
                .filter(nbtElement -> nbtElement instanceof NbtString)
                .map(NbtElement::asString)
                .collect(Collectors.toList());

        this.registeredItems.addAll(registeredItems);
    }

    public void writeNbt(NbtCompound nbt) {
        NbtUtil.putString(nbt, "name", name);
        NbtUtil.putLong(nbt, "created_at", createdAt);
        NbtUtil.putUuid(nbt, "id", teamID);
        NbtUtil.putUuid(nbt, "owner", owner);
        NbtUtil.putLong(nbt, "emc", storedEMC);
        NbtUtil.putBoolean(nbt, "is_default", isDefault);

        ItemAlchemy.logger.infoIfDev("TeamState.writeNbt(): nbt: " + nbt);

        NbtList registeredItems = NbtUtil.createNbtList();

        for (String registeredItem : this.registeredItems) {
            registeredItems.add(NbtString.of(registeredItem));
        }

        NbtUtil.put(nbt, "registered_items", registeredItems);
    }
}
