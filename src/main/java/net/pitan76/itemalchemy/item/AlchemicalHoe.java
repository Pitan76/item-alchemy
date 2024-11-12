package net.pitan76.itemalchemy.item;

import net.minecraft.item.ItemStack;
import net.pitan76.itemalchemy.util.ItemCharge;
import net.pitan76.mcpitanlib.api.item.ExtendItemProvider;
import net.pitan76.mcpitanlib.api.item.tool.CompatibleHoeItem;
import net.pitan76.mcpitanlib.api.item.tool.CompatibleToolMaterial;
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.util.CustomDataUtil;

public class AlchemicalHoe extends CompatibleHoeItem implements ExtendItemProvider, ItemCharge {
    public AlchemicalHoe(CompatibleToolMaterial toolMaterial, int attackDamage, float attackSpeed, CompatibleItemSettings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    @Override
    public boolean isDamageableOnDefault() {
        return false;
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return CustomDataUtil.contains(stack, "itemalchemy");
    }
}
