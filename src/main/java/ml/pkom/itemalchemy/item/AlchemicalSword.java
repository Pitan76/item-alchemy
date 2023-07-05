package ml.pkom.itemalchemy.item;

import ml.pkom.mcpitanlibarch.api.item.CompatibleItemSettings;
import ml.pkom.mcpitanlibarch.api.item.ExtendItemProvider;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
public class AlchemicalSword extends SwordItem implements ExtendItemProvider {
    public AlchemicalSword(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, CompatibleItemSettings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings.build());
        /*
        AttackEntityEventRegistry.register((player, world, entity, hand, hitResult) -> {
            ItemStack stack = player.getStackInHand(hand);

            return EventResult.pass();
        });
        */
    }
}
