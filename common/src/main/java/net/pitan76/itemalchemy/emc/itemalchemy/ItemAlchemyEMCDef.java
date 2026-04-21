package net.pitan76.itemalchemy.emc.itemalchemy;

import net.pitan76.itemalchemy.emc.EMCDef;
import net.pitan76.itemalchemy.item.Items;

public class ItemAlchemyEMCDef extends EMCDef {
    public void addAll() {
        add(Items.ALCHEMICAL_FUEL, 512);
        add(Items.MOBIUS_FUEL, 2048);
        add(Items.AETERNALIS_FUEL, 8192);
        add(Items.EMC_COLLECTOR_MK1, 82953);
        add(Items.EMC_COLLECTOR_MK2, 95753);
        add(Items.EMC_COLLECTOR_MK3, 114697);
        add(Items.EMC_COLLECTOR_MK4, 1384520);
        add(Items.EMC_COLLECTOR_MK5, 11543104);
        add(Items.ALCHEMY_PAD, 10248);
        add(Items.EMC_REPEATER, 10395);
        add(Items.EMC_CABLE, 2598);
    }
}
