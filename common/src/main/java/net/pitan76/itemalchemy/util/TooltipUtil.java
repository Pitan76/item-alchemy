package net.pitan76.itemalchemy.util;

import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.pitan76.mcpitanlib.api.util.PlatformUtil;
import net.pitan76.mcpitanlib.api.util.TextUtil;
import net.pitan76.mcpitanlib.api.util.client.LanguageUtil;
import net.pitan76.mcpitanlib.api.util.item.ItemUtil;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for tooltip-related functions that provides cross-version compatibility.
 */
public class TooltipUtil {

    /**
     * Checks if the shift key is currently being held down.
     * Uses GLFW directly for cross-version compatibility.
     * 
     * @return true if either left or right shift key is pressed, false otherwise
     */
    public static boolean hasShiftDown() {
        long window = GLFW.glfwGetCurrentContext();
        if (window == 0L) {
            return false;
        }
        return GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS ||
               GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
    }

    /**
     * Generates tooltip lines based on the provided translation key, supporting both basic and detailed descriptions.
     * @param translationKey The base translation key for the item, used to look up both basic and shift descriptions.
     * @return A list of Text objects representing the tooltip lines to be displayed.
     */
    public static List<Text> generateTooltipLines(String translationKey) {
        List<Text> tooltipLines = new ArrayList<>();

        if (!PlatformUtil.isClient()) return tooltipLines;

        // Show detailed description with multi-line support when shift is held
        if (TooltipUtil.hasShiftDown()) {
            String shiftKey = translationKey + ".desc_shift";
            if (LanguageUtil.hasTranslation(shiftKey)) {
                String shiftText = LanguageUtil.translate(shiftKey);
                List<String> lines = splitTooltipText(shiftText);
                for (String line : lines) {
                    tooltipLines.add(TextUtil.literal(line));
                }
            }
        } else {
            // Show basic description with multi-line support (only when shift is NOT held)
            String descKey = translationKey + ".desc";
            if (LanguageUtil.hasTranslation(descKey)) {
                String descText = LanguageUtil.translate(descKey);
                List<String> descLines = splitTooltipText(descText);
                for (String line : descLines) {
                    tooltipLines.add(TextUtil.literal(line));
                }
            }

            tooltipLines.add(TextUtil.withColor(TextUtil.translatable("text.itemalchemy.shift_info"), 0x555555));
        }

        return tooltipLines;
    }

    public static List<Text> generateTooltipLines(Item item) {
        String translationKey = ItemUtil.getTranslationKey(item);
        return generateTooltipLines(translationKey);
    }
                                                  /**
     * Split tooltip text into lines using custom delimiter.
     * Uses ||| as delimiter since JSON doesn't reliably parse \n as newlines.
     */
    public static List<String> splitTooltipText(String text) {
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
