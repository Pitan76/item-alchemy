package net.pitan76.itemalchemy.item;

import java.util.ArrayList;
import java.util.List;
import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.item.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.item.ExtendItem;

public class TomeOfKnowledge extends ExtendItem implements ILearnableItem {
    public TomeOfKnowledge(CompatibleItemSettings settings) {
		super(settings);
    }

    @Override
    public List<String> onLearn(Player user) {
        return new ArrayList<>(EMCManager.getMap().keySet());
    }
}
