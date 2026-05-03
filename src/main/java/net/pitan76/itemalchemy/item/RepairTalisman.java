package net.pitan76.itemalchemy.item;

import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.event.item.InventoryTickEvent;
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.util.ItemStackUtil;
import net.pitan76.mcpitanlib.api.util.WorldUtil;
import net.pitan76.mcpitanlib.midohra.item.ItemStack;

public class RepairTalisman extends AlchemicalItem {

    private static final int REPAIR_INTERVAL = 20; // Every second (20 ticks)

    public RepairTalisman(CompatibleItemSettings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(InventoryTickEvent e) {
        super.inventoryTick(e);
        if (e.isClient()) return;
        if (!e.isPlayer()) return;

        Player player = e.getPlayer();

        // Only repair every REPAIR_INTERVAL ticks (use entity age via MCPitanLib)
        if (WorldUtil.getTime(e.world) % REPAIR_INTERVAL != 0) return;

        // Scan player inventory for damaged items
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventoryAsM().callGetStackAsMidohra(i);
            if (stack.isEmpty()) continue;
            if (!ItemStackUtil.isDamageable(stack.toMinecraft())) continue;
            if (ItemStackUtil.getDamage(stack.toMinecraft()) <= 0) continue;

            // Don't repair the talisman itself
            if (ItemStackUtil.getItem(stack.toMinecraft()) instanceof RepairTalisman) continue;

            // Repair 1 durability
            ItemStackUtil.setDamage(stack.toMinecraft(), ItemStackUtil.getDamage(stack.toMinecraft()) - 1);
        }
    }
}
