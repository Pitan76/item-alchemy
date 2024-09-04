package net.pitan76.itemalchemy.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.pitan76.mcpitanlib.api.util.NbtUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TeamState {
    public UUID teamID;
    public String name;
    public long createdAt;
    public UUID owner;
    public long storedEMC = 0;
    public boolean isDefault = true;
    public List<String> registeredItems = new ArrayList<>();

    public void readNbt(NbtCompound nbt) {
        name = NbtUtil.get(nbt, "name", String.class);
        createdAt = NbtUtil.get(nbt, "created_at", Long.class);
        teamID = NbtUtil.get(nbt, "id", UUID.class);
        owner = NbtUtil.get(nbt, "owner", UUID.class);
        storedEMC = NbtUtil.get(nbt, "emc", Long.class);
        isDefault = NbtUtil.get(nbt, "is_default", Boolean.class);

        List<String> registeredItems = ((NbtList)nbt.get("registered_items")).stream()
                .filter(nbtElement -> nbtElement instanceof NbtString)
                .map(NbtElement::asString)
                .collect(Collectors.toList());

        this.registeredItems.addAll(registeredItems);
    }

    public void writeNbt(NbtCompound nbt) {
        NbtUtil.set(nbt, "name", name);
        NbtUtil.set(nbt, "created_at", createdAt);
        nbt.putUuid("id", teamID);
        nbt.putUuid("owner", owner);
        NbtUtil.set(nbt, "emc", storedEMC);
        NbtUtil.set(nbt, "is_default", isDefault);

        NbtList registeredItems = new NbtList();

        for (String registeredItem : this.registeredItems) {
            registeredItems.add(NbtString.of(registeredItem));
        }

        nbt.put("registered_items", registeredItems);
    }
}
