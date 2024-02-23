package net.pitan76.itemalchemy.item;

import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.item.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.item.ExtendItem;

import java.util.ArrayList;
import java.util.List;

public class TomeOfKnowledge extends ExtendItem implements ILearnableItem {
    public TomeOfKnowledge(CompatibleItemSettings settings) {
		super(settings);
    }

    @Override
    public List<String> onLearn(Player user) {
        return new ArrayList<>(EMCManager.getMap().keySet());
    }
}
