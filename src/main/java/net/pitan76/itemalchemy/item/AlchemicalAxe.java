package net.pitan76.itemalchemy.item;

import net.minecraft.item.ItemStack;
import net.pitan76.itemalchemy.util.ItemCharge;
import net.pitan76.mcpitanlib.api.item.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.item.ExtendItemProvider;
import net.pitan76.mcpitanlib.api.item.tool.CompatibleAxeItem;
import net.pitan76.mcpitanlib.api.item.tool.CompatibleToolMaterial;
import net.pitan76.mcpitanlib.api.util.CustomDataUtil;

public class AlchemicalAxe extends CompatibleAxeItem implements ExtendItemProvider, ItemCharge {
    public AlchemicalAxe(CompatibleToolMaterial toolMaterial, float attackDamage, float attackSpeed, CompatibleItemSettings settings) {
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
