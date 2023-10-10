package ml.pkom.itemalchemy.item;

import ml.pkom.itemalchemy.util.ItemCharge;
import ml.pkom.mcpitanlibarch.api.item.CompatibleItemSettings;
import ml.pkom.mcpitanlibarch.api.item.ExtendItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
public class AlchemicalSword extends SwordItem implements ExtendItemProvider, ItemCharge {
    public AlchemicalSword(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, CompatibleItemSettings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings.build());
        /*
        AttackEntityEventRegistry.register((player, world, entity, hand, hitResult) -> {
            ItemStack stack = player.getStackInHand(hand);

            return EventResult.pass();
        });
        */
    }
    @Override
    public boolean isDamageable() {
        return false;
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return stack.getSubNbt("itemalchemy") != null;
    }
}
