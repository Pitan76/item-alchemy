package net.pitan76.itemalchemy.util;

import net.pitan76.mcpitanlib.api.item.CompatibleItemSettings;

public class ChargeItemSettings extends CompatibleItemSettings {
    public ChargeItemSettings() {
        maxDamage(16);
    }

    public static ChargeItemSettings of() {
        return new ChargeItemSettings();
    }
}
