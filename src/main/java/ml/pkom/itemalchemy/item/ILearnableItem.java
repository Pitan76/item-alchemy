package ml.pkom.itemalchemy.item;

import ml.pkom.mcpitanlibarch.api.entity.Player;

/**
 * Item that doesn't have EMC, but can be learned in the Alchemy Table.
 */
public interface ILearnableItem {

    /**
     * Called when the item is put into the input slot of an Alchemy Table.
     * @param user The player
     */
    void onLearn(Player user);
}
