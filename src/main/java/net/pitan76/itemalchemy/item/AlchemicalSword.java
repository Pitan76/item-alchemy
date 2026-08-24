package net.pitan76.itemalchemy.item;

import net.pitan76.itemalchemy.util.IRechargeableFromKlein;
import net.pitan76.itemalchemy.util.ItemUtils;
import net.pitan76.itemalchemy.util.TooltipUtil;
import net.pitan76.mcpitanlib.api.event.item.ItemAppendTooltipEvent;
import net.pitan76.mcpitanlib.api.event.item.BonusAttackDamageArgs;
import net.pitan76.mcpitanlib.api.event.item.EnchantableArgs;
import net.pitan76.mcpitanlib.api.event.item.ItemBarColorArgs;
import net.pitan76.mcpitanlib.api.event.item.ItemBarStepArgs;
import net.pitan76.mcpitanlib.api.event.item.ItemBarVisibleArgs;
import net.pitan76.mcpitanlib.api.item.args.tool.MiningSpeedMultiplierArgs;
import net.pitan76.mcpitanlib.api.item.args.tool.SuitableForArgs;
import net.pitan76.mcpitanlib.api.item.tool.CompatibleToolMaterial;
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.item.v3.tool.CompatSwordItem;
import net.pitan76.mcpitanlib.api.text.TextComponent;
import net.pitan76.mcpitanlib.api.util.CustomDataUtil;
import net.pitan76.mcpitanlib.midohra.item.ItemStack;

import java.util.stream.Collectors;

public class AlchemicalSword extends CompatSwordItem implements IRechargeableFromKlein {
    public static final float DAMAGE_PER_CHARGE = 2.0F;

    public AlchemicalSword(CompatibleToolMaterial toolMaterial, int attackDamage, float attackSpeed, CompatibleItemSettings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    @Override
    public void appendTooltip(ItemAppendTooltipEvent e, Options options) {
        ItemStack stack = e.getStackM();
        e.addTooltip(TooltipUtil.generateTooltipLines(stack.getItem())
                .stream().map(TextComponent::getText).collect(Collectors.toList()));
    }

    @Override
    public boolean isDamageableOnDefault() {
        return false;
    }

    @Override
    public boolean isEnchantable(EnchantableArgs args, Options options) {
        return true;
    }

    @Override
    public boolean isItemBarVisible(ItemBarVisibleArgs args, Options options) {
        return CustomDataUtil.contains(args.getStack(), "itemalchemy");
    }

    @Override
    public int getItemBarStep(ItemBarStepArgs args, Options options) {
        return ItemUtils.getChargeBarStep(args.getStack());
    }

    @Override
    public int getItemBarColor(ItemBarColorArgs args, Options options) {
        return ItemUtils.CHARGE_BAR_COLOR;
    }

    @Override
    public float getMiningSpeedMultiplier(MiningSpeedMultiplierArgs args) {
        if (!isSuitableFor(new SuitableForArgs(args.getState())))
            return super.getMiningSpeedMultiplier(args);

        return super.getMiningSpeedMultiplier(args) * (ItemUtils.getCharge(args.getStack()) + 1);
    }

    /**
     * Add attack power according to the amount of charge.
     */
    @Override
    public float getBonusAttackDamage(BonusAttackDamageArgs args, Options options) {
        options.cancel = true;

        return getBonusDamage(ItemUtils.getCharge(args.getStack()));
    }

    @Override
    public int getEmcCostPerCharge() {
        return 1500;
    }

    /**
     * Get bonus damage based on charge level.
     *
     * @param charge the charge level of the sword
     * @return bonus damage
     */
    public static float getBonusDamage(int charge) {
        return charge * DAMAGE_PER_CHARGE;
    }
}
