package net.pitan76.itemalchemy.item;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.pitan76.itemalchemy.util.IRechargeableFromKlein;
import net.pitan76.itemalchemy.util.ItemUtils;
import net.pitan76.itemalchemy.util.TooltipUtil;
import net.pitan76.mcpitanlib.api.event.item.ItemAppendTooltipEvent;
import net.pitan76.mcpitanlib.api.event.item.ItemBarVisibleArgs;
import net.pitan76.mcpitanlib.api.item.tool.CompatibleSwordItem;
import net.pitan76.mcpitanlib.api.item.tool.CompatibleToolMaterial;
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.util.CustomDataUtil;
import net.pitan76.mcpitanlib.api.util.ItemStackUtil;

public class AlchemicalSword extends CompatibleSwordItem implements IRechargeableFromKlein {
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
    public void appendTooltip(ItemAppendTooltipEvent e, Options options) {
        ItemStack stack = e.getStack();
        e.addTooltip(TooltipUtil.generateTooltipLines(ItemStackUtil.getItem(stack)));
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

    @Override
    public int getEmcCostPerCharge() {
        return 1500;
    }
    
    /**
     * Consume charge from the sword when hitting entities.
     * Called from ItemAlchemy via AttackEntityEventRegistry.
     */
    public static void onAttack(ItemStack stack) {
        if (stack.getItem() instanceof AlchemicalSword) {
            int charge = ItemUtils.getCharge(stack);
            if (charge > 0) {
                ItemUtils.setCharge(stack, charge - 1);
            }
        }
    }
    
    /**
     * Get bonus damage based on charge level.
     * @param stack the sword ItemStack
     * @return bonus damage (+1 per charge level)
     */
    public static float getBonusDamage(ItemStack stack) {
        if (stack.getItem() instanceof AlchemicalSword) {
            int charge = ItemUtils.getCharge(stack);
            return charge * 1.0f;
        }
        return 0.0f;
    }
}
