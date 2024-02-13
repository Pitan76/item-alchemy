package ml.pkom.itemalchemy.emcs.itemalchemy;

import ml.pkom.itemalchemy.emcs.EMCDef;

public class ItemAlchemyEMCDef extends EMCDef {
    public void addAll() {
        add(ml.pkom.itemalchemy.item.Items.ALCHEMICAL_FUEL.getOrNull(), 512);
        add(ml.pkom.itemalchemy.item.Items.MOBIUS_FUEL.getOrNull(), 2048);
        add(ml.pkom.itemalchemy.item.Items.AETERNALIS_FUEL.getOrNull(), 8192);
        add(ml.pkom.itemalchemy.item.Items.EMC_COLLECTOR_MK3.getOrNull(), 114697);
        add(ml.pkom.itemalchemy.item.Items.ALCHEMY_PAD.getOrNull(), 10248);
    }
}
