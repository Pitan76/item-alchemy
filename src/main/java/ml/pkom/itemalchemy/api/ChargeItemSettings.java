package ml.pkom.itemalchemy.api;

import ml.pkom.mcpitanlibarch.api.item.CompatibleItemSettings;

public class ChargeItemSettings extends CompatibleItemSettings {
    public ChargeItemSettings() {
        maxDamage(8);
    }

    public static ChargeItemSettings of() {
        return new ChargeItemSettings();
    }
}
