package net.pitan76.itemalchemy.data;

import net.pitan76.itemalchemy.ItemAlchemy;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.util.NbtUtil;
import net.pitan76.mcpitanlib.midohra.nbt.NbtCompound;
import net.pitan76.mcpitanlib.midohra.nbt.NbtElement;
import net.pitan76.mcpitanlib.midohra.nbt.NbtList;
import net.pitan76.mcpitanlib.midohra.nbt.NbtString;

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
        name = nbt.getString("name");
        createdAt = nbt.getLong("created_at");
        teamID = nbt.getUuid("id");
        owner = nbt.getUuid("owner");
        storedEMC = nbt.getLong("emc");
        isDefault = nbt.getBoolean("is_default");

        ItemAlchemy.logger.infoIfDev("TeamState.readNbt(): nbt: " + nbt);

        List<String> registeredItems = (nbt.get("registered_items").asNbtList()).stream()
                .filter(NbtElement::isNbtString)
                .map(NbtElement::asNbtString)
                .map(NbtString::getValue)
                .collect(Collectors.toList());

        this.registeredItems.addAll(registeredItems);
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putString("name", name);
        nbt.putLong("created_at", createdAt);
        nbt.putUuid("id", teamID);
        nbt.putUuid("owner", owner);
        nbt.putLong("emc", storedEMC);
        nbt.putBoolean("is_default", isDefault);

        ItemAlchemy.logger.infoIfDev("TeamState.writeNbt(): nbt: " + nbt);

        NbtList registeredItems = NbtList.of(NbtUtil.createNbtList());

        for (String registeredItem : this.registeredItems) {
            registeredItems.add(NbtString.of(registeredItem).toElement()); // TODO: NbtListにそのままElementConvertibleを追加できるようにする
        }

        nbt.put("registered_items", registeredItems);
    }

    public boolean isOwner(UUID player) {
        return owner.equals(player);
    }

    public boolean isOwner(Player player) {
        return isOwner(player.getUUID());
    }

    public boolean isMember(Player player) {
        return ServerState.of(player).getTeamByPlayer(player.getUUID()).isPresent();
    }
}
