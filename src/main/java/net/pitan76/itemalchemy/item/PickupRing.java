package net.pitan76.itemalchemy.item;

import net.pitan76.mcpitanlib.api.event.item.ItemAppendTooltipEvent;
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.text.TextComponent;

public class PickupRing extends Ring {
    public PickupRing(CompatibleItemSettings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemAppendTooltipEvent e) {
        super.appendTooltip(e);
        e.addTooltip(TextComponent.literal("not implemented yet"));
    }
}
