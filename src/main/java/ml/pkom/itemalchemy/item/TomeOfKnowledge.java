package ml.pkom.itemalchemy.item;

import java.util.ArrayList;
import java.util.List;
import ml.pkom.itemalchemy.EMCManager;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import ml.pkom.mcpitanlibarch.api.item.CompatibleItemSettings;
import ml.pkom.mcpitanlibarch.api.item.ExtendItem;

public class TomeOfKnowledge extends ExtendItem implements ILearnableItem {
    public TomeOfKnowledge(CompatibleItemSettings settings) {
		super(settings);
    }

    @Override
    public List<String> onLearn(Player user) {
        return new ArrayList<>(EMCManager.getMap().keySet());
    }
}
