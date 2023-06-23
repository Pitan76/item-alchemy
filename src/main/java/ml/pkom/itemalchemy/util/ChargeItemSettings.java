package ml.pkom.itemalchemy.util;

import ml.pkom.mcpitanlibarch.api.item.CompatibleItemSettings;

public class ChargeItemSettings extends CompatibleItemSettings {
    public ChargeItemSettings() {
        maxDamage(15);
    }

    public static ChargeItemSettings of() {
        return new ChargeItemSettings();
    }
}
