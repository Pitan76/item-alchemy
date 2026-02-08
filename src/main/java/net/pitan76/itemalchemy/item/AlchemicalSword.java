package net.pitan76.itemalchemy.item;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.pitan76.itemalchemy.util.ItemCharge;
import net.pitan76.itemalchemy.util.ItemUtils;
import net.pitan76.mcpitanlib.api.event.item.BonusAttackDamageArgs;
import net.pitan76.mcpitanlib.api.event.item.ItemBarVisibleArgs;
import net.pitan76.mcpitanlib.api.item.tool.CompatibleSwordItem;
import net.pitan76.mcpitanlib.api.item.tool.CompatibleToolMaterial;
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.util.CustomDataUtil;

public class AlchemicalSword extends CompatibleSwordItem implements ItemCharge {
    public AlchemicalSword(CompatibleToolMaterial toolMaterial, int attackDamage, float attackSpeed, CompatibleItemSettings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);

        /*
            AttackEntityEventRegistry.register(
                (player, world, entity, hand, hitResult) -> {
               ItemStack stack = player.getStackInHand(hand);
               return EventResult.pass();
           });
         */
    }

    @Override
    public boolean isDamageableOnDefault() {
        return false;
    }

    @Override
    public boolean isItemBarVisible(ItemBarVisibleArgs args, Options options) {
        return CustomDataUtil.contains(args.getStack(), "itemalchemy");
    }

    @Override
    public float overrideGetMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        if (!overrideIsSuitableFor(state))
            return super.overrideGetMiningSpeedMultiplier(stack, state);

        return super.overrideGetMiningSpeedMultiplier(stack, state) * (ItemUtils.getCharge(stack) + 1);
    }

    // TODO: Attack damage bonus based on charge level
}
