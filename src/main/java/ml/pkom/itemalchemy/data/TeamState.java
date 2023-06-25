package ml.pkom.itemalchemy.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeamState {
    public UUID teamID;
    public String name;
    public long createdAt;
    public UUID owner;
    public long storedEMC = 0;
    public List<String> registeredItems = new ArrayList<>();
    public List<UUID> players = new ArrayList<>();
}
