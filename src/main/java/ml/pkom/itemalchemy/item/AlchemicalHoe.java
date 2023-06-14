package ml.pkom.itemalchemy.item;

import ml.pkom.mcpitanlibarch.api.item.CompatibleItemSettings;
import ml.pkom.mcpitanlibarch.api.item.ExtendItemProvider;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ToolMaterial;

public class AlchemicalHoe extends HoeItem implements ExtendItemProvider {
    public AlchemicalHoe(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, CompatibleItemSettings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings.build());
    }

    @Override
    public boolean isDamageable() {
        return false;
    }
}
