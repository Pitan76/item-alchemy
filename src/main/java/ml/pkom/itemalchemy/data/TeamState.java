package ml.pkom.itemalchemy.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

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
    public List<String> registeredItems = new ArrayList<>();

    public void readNBT(NbtCompound nbt) {
        name = nbt.getString("name");
        createdAt = nbt.getLong("created_at");
        teamID = nbt.getUuid("id");
        owner = nbt.getUuid("owner");
        storedEMC = nbt.getLong("emc");

        List<String> registeredItems = nbt.getList("registered_items", NbtElement.LIST_TYPE).stream()
                .filter(nbtElement -> nbtElement instanceof NbtString)
                .map(NbtElement::asString)
                .collect(Collectors.toList());

        this.registeredItems.addAll(registeredItems);
    }

    public void writeNBT(NbtCompound nbt) {
        nbt.putString("name", name);
        nbt.putLong("created_at", createdAt);
        nbt.putUuid("id", teamID);
        nbt.putUuid("owner", owner);
        nbt.putLong("emc", storedEMC);

        NbtList registeredItems = new NbtList();

        for (String registeredItem : this.registeredItems) {
            registeredItems.add(NbtString.of(registeredItem));
        }

        nbt.put("registered_items", registeredItems);
    }
}
