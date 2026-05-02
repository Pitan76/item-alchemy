package net.pitan76.itemalchemy.item;

import net.pitan76.itemalchemy.util.IRechargeableFromKlein;
import net.pitan76.itemalchemy.util.ItemUtils;
import net.pitan76.itemalchemy.util.TooltipUtil;
import net.pitan76.mcpitanlib.api.event.item.ItemAppendTooltipEvent;
import net.pitan76.mcpitanlib.api.event.item.ItemBarVisibleArgs;
import net.pitan76.mcpitanlib.api.item.args.tool.MiningSpeedMultiplierArgs;
import net.pitan76.mcpitanlib.api.item.args.tool.SuitableForArgs;
import net.pitan76.mcpitanlib.api.item.tool.CompatibleToolMaterial;
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.item.v3.tool.CompatSwordItem;
import net.pitan76.mcpitanlib.api.util.CustomDataUtil;
import net.pitan76.mcpitanlib.midohra.item.ItemStack;

public class AlchemicalSword extends CompatSwordItem implements IRechargeableFromKlein {
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
        ItemStack stack = e.getStackM();
        e.addTooltip(TooltipUtil.generateTooltipLines(stack.getItem()));
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
    public float getMiningSpeedMultiplier(MiningSpeedMultiplierArgs args) {
        if (!isSuitableFor(new SuitableForArgs(args.getState())))
            return super.getMiningSpeedMultiplier(args);

        return super.getMiningSpeedMultiplier(args) * (ItemUtils.getCharge(args.getStack()) + 1);
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
        if (stack.getItem().instanceOf(AlchemicalSword.class)) {
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
        if (stack.getItem().instanceOf(AlchemicalSword.class)) {
            int charge = ItemUtils.getCharge(stack);
            return charge * 1.0f;
        }
        return 0.0f;
    }
}
