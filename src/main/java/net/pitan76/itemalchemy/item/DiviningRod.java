package net.pitan76.itemalchemy.item;

import net.pitan76.mcpitanlib.api.event.item.ItemAppendTooltipEvent;
import net.pitan76.mcpitanlib.api.event.item.ItemUseEvent;
import net.pitan76.mcpitanlib.api.event.item.ItemUseOnBlockEvent;
import net.pitan76.mcpitanlib.api.item.v2.CompatItem;
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.util.CompatActionResult;
import net.pitan76.mcpitanlib.api.util.StackActionResult;
import net.pitan76.mcpitanlib.api.util.TextUtil;

public class DiviningRod extends CompatItem  {
    public DiviningRod(int i, CompatibleItemSettings settings) {
        super(settings);
    }

    public DiviningRod(CompatibleItemSettings settings) {
        this(1, settings);
    }

    @Override
    public CompatActionResult onRightClickOnBlock(ItemUseOnBlockEvent e) {
        return super.onRightClickOnBlock(e);
    }

    @Override
    public StackActionResult onRightClick(ItemUseEvent e) {
        return super.onRightClick(e);
    }

    @Override
    public void appendTooltip(ItemAppendTooltipEvent e) {
        super.appendTooltip(e);
        e.addTooltip(TextUtil.literal("not implemented yet"));
    }
}
