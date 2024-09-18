package net.pitan76.itemalchemy.emc.itemalchemy;

import net.pitan76.itemalchemy.emc.EMCDef;
import net.pitan76.itemalchemy.item.Items;

public class ItemAlchemyEMCDef extends EMCDef {
    public void addAll() {
        add(Items.ALCHEMICAL_FUEL.getOrNull(), 512);
        add(Items.MOBIUS_FUEL.getOrNull(), 2048);
        add(Items.AETERNALIS_FUEL.getOrNull(), 8192);
        add(Items.EMC_COLLECTOR_MK3.getOrNull(), 114697);
        add(Items.ALCHEMY_PAD.getOrNull(), 10248);
    }
}
