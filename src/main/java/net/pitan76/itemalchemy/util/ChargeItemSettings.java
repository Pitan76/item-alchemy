package net.pitan76.itemalchemy.util;

import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.util.CompatIdentifier;

public class ChargeItemSettings extends CompatibleItemSettings {
    public ChargeItemSettings(CompatIdentifier id) {
        super(id);
        maxDamage(16);
    }

    public static ChargeItemSettings of(CompatIdentifier id) {
        return new ChargeItemSettings(id);
    }
}
