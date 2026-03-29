package net.pitan76.itemalchemy.item;

import net.minecraft.block.BlockState;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.pitan76.itemalchemy.util.IRechargeableFromKlein;
import net.pitan76.itemalchemy.util.ItemUtils;
import net.pitan76.itemalchemy.util.TooltipUtil;
import net.pitan76.mcpitanlib.api.event.item.ItemAppendTooltipEvent;
import net.pitan76.mcpitanlib.api.event.item.ItemBarVisibleArgs;
import net.pitan76.mcpitanlib.api.item.ExtendItemProvider;
import net.pitan76.mcpitanlib.api.item.tool.CompatibleSwordItem;
import net.pitan76.mcpitanlib.api.item.tool.CompatibleToolMaterial;
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.util.CustomDataUtil;
import net.pitan76.mcpitanlib.api.util.TextUtil;
import net.pitan76.mcpitanlib.api.util.item.ItemUtil;
import net.pitan76.mcpitanlib.api.item.ExtendItemProvider.Options;

import java.util.ArrayList;
import java.util.List;

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
        options.cancel = true;
        ItemStack stack = e.getStack();
        String translationKey = ItemUtil.getTranslationKey(stack.getItem());
        
        // Show detailed description with multi-line support when shift is held
        if (TooltipUtil.hasShiftDown()) {
            String shiftKey = translationKey + ".desc_shift";
            if (I18n.hasTranslation(shiftKey)) {
                String shiftText = I18n.translate(shiftKey);
                List<String> lines = splitTooltipText(shiftText);
                for (String line : lines) {
                    e.addTooltip(TextUtil.literal(line));
                }
            }
        } else {
            // Show basic description with multi-line support (only when shift is NOT held)
            String descKey = translationKey + ".desc";
            if (I18n.hasTranslation(descKey)) {
                String descText = I18n.translate(descKey);
                List<String> descLines = splitTooltipText(descText);
                for (String line : descLines) {
                    e.addTooltip(TextUtil.literal(line));
                }
            }
            e.addTooltip(TextUtil.withColor(TextUtil.translatable("text.itemalchemy.shift_info"), 0x555555));
        }
    }
    
    /**
     * Split tooltip text into lines using custom delimiter.
     * Uses ||| as delimiter since JSON doesn't reliably parse \n as newlines.
     */
    private List<String> splitTooltipText(String text) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return lines;
        }
        
        // Split on custom delimiter ||| using manual parsing
        int start = 0;
        int delimiterIndex;
        boolean foundDelimiter = false;
        
        while ((delimiterIndex = text.indexOf("|||", start)) != -1) {
            foundDelimiter = true;
            String line = text.substring(start, delimiterIndex);
            if (!line.trim().isEmpty()) {
                lines.add(line);
            }
            start = delimiterIndex + 3; // Skip past |||
        }
        
        // Add remaining text after last delimiter
        if (start < text.length()) {
            String remaining = text.substring(start);
            if (!remaining.trim().isEmpty()) {
                lines.add(remaining);
            }
        }
        
        return lines;
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
