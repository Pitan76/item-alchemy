package net.pitan76.itemalchemy.item;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.pitan76.itemalchemy.util.TooltipUtil;
import net.pitan76.mcpitanlib.api.event.item.ItemAppendTooltipEvent;
import net.pitan76.mcpitanlib.api.item.v2.CompatItem;
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.util.TextUtil;
import net.pitan76.mcpitanlib.api.util.item.ItemUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for alchemical items with built-in shift-tooltip support.
 * Reads descriptions from language files using the pattern:
 * - item.{mod_id}.{item_name}.desc - Basic description (always shown)
 * - item.{mod_id}.{item_name}.desc_shift - Detailed description (shown when Shift is held)
 */
public class AlchemicalItem extends CompatItem {

    public AlchemicalItem(CompatibleItemSettings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemAppendTooltipEvent e) {
        ItemStack stack = e.getStack();
        String translationKey = ItemUtil.getTranslationKey(stack.getItem());
        
        // Check if shift is being held
        if (TooltipUtil.hasShiftDown()) {
            // Show detailed description with multi-line support
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
            // Show shift hint
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
}
