package net.pitan76.itemalchemy.item;

import java.util.List;
import net.pitan76.mcpitanlib.api.entity.Player;

/**
 * Item that doesn't have EMC, but can be learned in the Alchemy Table.
 */
public interface ILearnableItem {

    /**
     * Called when the item is put into the input slot of an Alchemy Table.
     *
     * @param user The player
     * @return the item ids to be learned.
     */
    List<String> onLearn(Player user);
}
