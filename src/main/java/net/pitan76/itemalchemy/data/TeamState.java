package net.pitan76.itemalchemy.data;

import net.pitan76.itemalchemy.ItemAlchemy;
import net.pitan76.mcpitanlib.api.entity.Player;
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
    // オフライン時にもオーナー名を表示できるように保持しておく
    public String ownerName = "";
    public long storedEMC = 0;
    public boolean isDefault = true;
    public List<String> registeredItems = new ArrayList<>();

    public void readNbt(NbtCompound nbt) {
        name = nbt.has("name") ? nbt.getString("name") : "";
        createdAt = nbt.has("created_at") ? nbt.getLong("created_at") : 0;
        teamID = nbt.has("id") ? nbt.getUuid("id") : null;
        owner = nbt.has("owner") ? nbt.getUuid("owner") : null;
        ownerName = nbt.has("owner_name") ? nbt.getString("owner_name") : "";
        storedEMC = nbt.has("emc") ? nbt.getLong("emc") : 0;
        isDefault = !nbt.has("is_default") || nbt.getBoolean("is_default");

        ItemAlchemy.logger.infoIfDev("TeamState.readNbt(): nbt: " + nbt);

        if (!nbt.has("registered_items") || !nbt.get("registered_items").isNbtList()) return;

        List<String> registeredItems = (nbt.get("registered_items").asNbtList()).stream()
                .filter(NbtElement::isNbtString)
                .map(NbtElement::asNbtString)
                .map(NbtString::getValue)
                .collect(Collectors.toList());

        this.registeredItems.addAll(registeredItems);
    }

    public void writeNbt(NbtCompound nbt) {
        // nullを書き込もうとすると保存自体が失敗し、ワールド全体のEMCが消えるため必ずガードする
        nbt.putString("name", name == null ? "" : name);
        nbt.putLong("created_at", createdAt);
        if (teamID != null) nbt.putUuid("id", teamID);
        if (owner != null) nbt.putUuid("owner", owner);
        nbt.putString("owner_name", ownerName == null ? "" : ownerName);
        nbt.putLong("emc", storedEMC);
        nbt.putBoolean("is_default", isDefault);

        ItemAlchemy.logger.infoIfDev("TeamState.writeNbt(): nbt: " + nbt);

        NbtList registeredItems = NbtList.of();

        for (String registeredItem : this.registeredItems) {
            registeredItems.add(NbtString.of(registeredItem));
        }

        nbt.put("registered_items", registeredItems);
    }

    /**
     * オーナー名。未記録の場合はUUIDを返す。
     */
    public String getOwnerName() {
        if (ownerName == null || ownerName.isEmpty()) return String.valueOf(owner);

        return ownerName;
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
