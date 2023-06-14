package ml.pkom.itemalchemy.item;

import ml.pkom.mcpitanlibarch.api.item.CompatibleItemSettings;
import ml.pkom.mcpitanlibarch.api.item.ExtendItemProvider;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;

public class AlchemicalPickaxe extends PickaxeItem implements ExtendItemProvider {
    public AlchemicalPickaxe(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, CompatibleItemSettings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings.build());
    }

    @Override
    public boolean isDamageable() {
        return false;
    }
}
