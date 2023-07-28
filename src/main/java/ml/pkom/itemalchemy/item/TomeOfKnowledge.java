package ml.pkom.itemalchemy.item;

import ml.pkom.itemalchemy.api.PlayerRegisteredItemUtil;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import ml.pkom.mcpitanlibarch.api.item.CompatibleItemSettings;
import ml.pkom.mcpitanlibarch.api.item.ExtendItem;

public class TomeOfKnowledge extends ExtendItem implements ILearnableItem {
    public TomeOfKnowledge(CompatibleItemSettings settings) {
		super(settings);
    }

    @Override
    public void onLearn(Player user) {
        PlayerRegisteredItemUtil.addAll(user);
    }
}
