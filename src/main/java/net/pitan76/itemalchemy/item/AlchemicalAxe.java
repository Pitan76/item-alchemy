package net.pitan76.itemalchemy.item;

import net.pitan76.itemalchemy.util.IRechargeableFromKlein;
import net.pitan76.itemalchemy.util.ItemUtils;
import net.pitan76.itemalchemy.util.TooltipUtil;
import net.pitan76.mcpitanlib.api.event.item.ItemAppendTooltipEvent;
import net.pitan76.mcpitanlib.api.event.item.ItemBarVisibleArgs;
import net.pitan76.mcpitanlib.api.event.item.PostMineEvent;
import net.pitan76.mcpitanlib.api.item.args.tool.MiningSpeedMultiplierArgs;
import net.pitan76.mcpitanlib.api.item.args.tool.SuitableForArgs;
import net.pitan76.mcpitanlib.api.item.tool.CompatibleToolMaterial;
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.item.v3.tool.CompatAxeItem;
import net.pitan76.mcpitanlib.api.text.TextComponent;
import net.pitan76.mcpitanlib.api.util.CustomDataUtil;
import net.pitan76.mcpitanlib.midohra.item.ItemStack;

import java.util.stream.Collectors;

public class AlchemicalAxe extends CompatAxeItem implements IRechargeableFromKlein {
    public AlchemicalAxe(CompatibleToolMaterial toolMaterial, float attackDamage, float attackSpeed, CompatibleItemSettings settings) {
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
    public boolean postMine(PostMineEvent e) {
        ItemStack stack = e.getStackM();
        if (!isSuitableFor(new SuitableForArgs(e.getStateM()))) return super.postMine(e);

        int charge = ItemUtils.getCharge(stack);
        
        // Consume 1 charge per block mined if charge > 0
        if (charge > 0) {
            ItemUtils.setCharge(stack, charge - 1);
        }

        return super.postMine(e);
    }

    @Override
    public int getEmcCostPerCharge() {
        return 1000;
    }
}
