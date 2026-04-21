package net.pitan76.itemalchemy.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.event.item.InventoryTickEvent;
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.util.ItemStackUtil;
import net.pitan76.mcpitanlib.api.util.WorldUtil;

public class RepairTalisman extends AlchemicalItem {

    private static final int REPAIR_INTERVAL = 20; // Every second (20 ticks)

    public RepairTalisman(CompatibleItemSettings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(InventoryTickEvent e) {
        super.inventoryTick(e);
        if (e.isClient()) return;
        if (!(e.entity instanceof PlayerEntity)) return;

        Player player = new Player((PlayerEntity) e.entity);

        // Only repair every REPAIR_INTERVAL ticks (use entity age via MCPitanLib)
        if (WorldUtil.getTime(e.world) % REPAIR_INTERVAL != 0) return;

        // Scan player inventory for damaged items
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (ItemStackUtil.isEmpty(stack)) continue;
            if (!ItemStackUtil.isDamageable(stack)) continue;
            if (ItemStackUtil.getDamage(stack) <= 0) continue;

            // Don't repair the talisman itself
            if (ItemStackUtil.getItem(stack) instanceof RepairTalisman) continue;

            // Repair 1 durability
            ItemStackUtil.setDamage(stack, ItemStackUtil.getDamage(stack) - 1);
        }
    }
}
