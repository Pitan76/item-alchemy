package ml.pkom.itemalchemy.item;

import ml.pkom.mcpitanlibarch.api.item.CompatibleItemSettings;
import ml.pkom.mcpitanlibarch.api.item.ExtendItemProvider;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ToolMaterial;

public class AlchemicalAxe extends AxeItem implements ExtendItemProvider {
    public AlchemicalAxe(ToolMaterial toolMaterial, float attackDamage, float attackSpeed, CompatibleItemSettings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings.build());
    }

    @Override
    public boolean isDamageable() {
        return false;
    }
}
