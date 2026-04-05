package net.pitan76.itemalchemy.util;

/**
 * Marker interface for items that can be recharged from Klein Star EMC storage.
 * Extends ItemCharge to indicate the item uses the charge system.
 */
public interface IRechargeableFromKlein extends ItemCharge {
    /**
     * Returns the EMC cost to increase charge by one level.
     * Default is 1000 EMC per charge level.
     * 
     * @return EMC cost per charge level
     */
    default int getEmcCostPerCharge() {
        return 1000;
    }
    
    /**
     * Returns the maximum charge level for this item.
     * Default is 4 (same as MAX_CHARGE_VALUE in ItemUtils).
     * 
     * @return maximum charge level
     */
    default int getMaxCharge() {
        return ItemUtils.MAX_CHARGE_VALUE;
    }
}
