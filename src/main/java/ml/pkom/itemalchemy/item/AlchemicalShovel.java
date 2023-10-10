package ml.pkom.itemalchemy.item;

import ml.pkom.itemalchemy.util.ItemCharge;
import ml.pkom.mcpitanlibarch.api.item.CompatibleItemSettings;
import ml.pkom.mcpitanlibarch.api.item.ExtendItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.ToolMaterial;

public class AlchemicalShovel extends ShovelItem implements ExtendItemProvider, ItemCharge {
    public AlchemicalShovel(ToolMaterial toolMaterial, float attackDamage, float attackSpeed, CompatibleItemSettings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings.build());
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return stack.getSubNbt("itemalchemy") != null;
    }
}
