package net.pitan76.itemalchemy.emc.itemalchemy;

import net.pitan76.itemalchemy.emc.EMCDef;
import net.pitan76.itemalchemy.item.Items;

public class ItemAlchemyEMCDef extends EMCDef {
    public void addAll() {
        add(Items.ALCHEMICAL_FUEL.getOrNull(), 512);
        add(Items.MOBIUS_FUEL.getOrNull(), 2048);
        add(Items.AETERNALIS_FUEL.getOrNull(), 8192);
        add(Items.EMC_COLLECTOR_MK1.getOrNull(), 82953);
        add(Items.EMC_COLLECTOR_MK2.getOrNull(), 95753);
        add(Items.EMC_COLLECTOR_MK3.getOrNull(), 114697);
        add(Items.EMC_COLLECTOR_MK4.getOrNull(), 1384520);
        add(Items.EMC_COLLECTOR_MK5.getOrNull(), 11543104);
        add(Items.ALCHEMY_PAD.getOrNull(), 10248);
    }
}
