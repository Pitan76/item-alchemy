package net.pitan76.itemalchemy.item;

import net.minecraft.block.BlockState;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.pitan76.itemalchemy.util.IRechargeableFromKlein;
import net.pitan76.itemalchemy.util.ItemUtils;
import net.pitan76.itemalchemy.util.TooltipUtil;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.event.item.ItemAppendTooltipEvent;
import net.pitan76.mcpitanlib.api.event.item.ItemBarVisibleArgs;
import net.pitan76.mcpitanlib.api.event.item.PostMineEvent;
import net.pitan76.mcpitanlib.api.item.ExtendItemProvider;
import net.pitan76.mcpitanlib.api.item.tool.CompatibleHoeItem;
import net.pitan76.mcpitanlib.api.item.tool.CompatibleToolMaterial;
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.util.CustomDataUtil;
import net.pitan76.mcpitanlib.api.util.TextUtil;
import net.pitan76.mcpitanlib.api.util.WorldUtil;
import net.pitan76.mcpitanlib.api.util.item.ItemUtil;
import net.pitan76.mcpitanlib.api.item.ExtendItemProvider.Options;
import net.pitan76.mcpitanlib.midohra.util.math.Direction;
import net.pitan76.mcpitanlib.api.util.math.PosUtil;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class AlchemicalHoe extends CompatibleHoeItem implements IRechargeableFromKlein {
    public AlchemicalHoe(CompatibleToolMaterial toolMaterial, int attackDamage, float attackSpeed, CompatibleItemSettings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
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
    public boolean postMine(PostMineEvent e) {
        ItemStack stack = e.getStack();
        if (!overrideIsSuitableFor(e.getState())) return super.postMine(e);

        int charge = ItemUtils.getCharge(stack);

        // Expand tilling area based on charge level
        if (charge > 0) {
            // Till adjacent blocks in horizontal plane (3x3 area)
            Player player = e.getPlayer();
            Direction facing = Direction.of(player.getHorizontalFacing());
            Direction left = facing.rotateYCounterclockwise();
            Direction right = facing.rotateYClockwise();
            
            tillAdjacentBlocks(e.getPlayer(), e.getWorld(), e.getPos(), left, 1);
            tillAdjacentBlocks(e.getPlayer(), e.getWorld(), e.getPos(), right, 1);
            tillAdjacentBlocks(e.getPlayer(), e.getWorld(), e.getPos(), facing.rotateYCounterclockwise().rotateYCounterclockwise(), 1);
            tillAdjacentBlocks(e.getPlayer(), e.getWorld(), e.getPos(), facing.rotateYClockwise().rotateYClockwise(), 1);
        }

        // Consume 1 charge per block tilled if charge > 0
        if (charge > 0) {
            ItemUtils.setCharge(stack, charge - 1);
        }

        return super.postMine(e);
    }

    protected void tillAdjacentBlocks(Player player, net.minecraft.world.World world, BlockPos originPos, Direction direction, int distance) {
        for (int i = 1; i <= distance; i++) {
            BlockPos targetPos = PosUtil.offset(originPos, direction, i);
            BlockState targetState = WorldUtil.getBlockState(world, targetPos);
            if (overrideIsSuitableFor(targetState)) {
                WorldUtil.breakBlock(world, targetPos, true, player);
            }
        }
    }

    @Override
    public int getEmcCostPerCharge() {
        return 1000;
    }
}
