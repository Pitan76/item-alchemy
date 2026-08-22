package net.pitan76.itemalchemy.item;

import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.itemalchemy.config.ItemAlchemyConfig;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TomeOfKnowledge extends AlchemicalItem implements ILearnableItem {
    public TomeOfKnowledge(CompatibleItemSettings settings) {
		super(settings);
    }

    @Override
    public List<String> onLearn(Player user) {
        if (!ItemAlchemyConfig.isTomeOfKnowledgeEnabled())
            return Collections.emptyList();

        if (ItemAlchemyConfig.getTomeOfKnowledgeBlacklist().isEmpty())
            return new ArrayList<>(EMCManager.getMap().keySet());

        List<String> items = new ArrayList<>();
        for (String itemId : EMCManager.getMap().keySet()) {
            if (ItemAlchemyConfig.isTomeOfKnowledgeBlacklisted(itemId)) continue;

            items.add(itemId);
        }

        return items;
    }
}
