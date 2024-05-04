package net.pitan76.itemalchemy.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.pitan76.itemalchemy.util.ItemCharge;
import net.pitan76.itemalchemy.util.VersionUtils;
import net.pitan76.mcpitanlib.api.item.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.item.ExtendItemProvider;
import net.pitan76.mcpitanlib.api.item.tool.CompatibleSwordItem;
import net.pitan76.mcpitanlib.api.item.tool.CompatibleToolMaterial;
import net.pitan76.mcpitanlib.api.util.CustomDataUtil;
import net.pitan76.mcpitanlib.api.util.NbtUtil;

import java.util.Objects;

public class AlchemicalSword extends CompatibleSwordItem implements ExtendItemProvider, ItemCharge {
    public AlchemicalSword(CompatibleToolMaterial toolMaterial, int attackDamage, float attackSpeed, CompatibleItemSettings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
        /*
        AttackEntityEventRegistry.register((player, world, entity, hand, hitResult) -> {
            ItemStack stack = player.getStackInHand(hand);

            return EventResult.pass();
        });
        */
    }
    @Override
    public boolean isDamageable() {
        return false;
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        if(VersionUtils.isSupportedComponent()) {
            return CustomDataUtil.contains(stack, "itemalchemy");
        }

        return stack.getSubNbt("itemalchemy") != null;
    }
}
