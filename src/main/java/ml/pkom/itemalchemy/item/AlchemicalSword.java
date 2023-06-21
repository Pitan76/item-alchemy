package ml.pkom.itemalchemy.item;

import dev.architectury.event.EventResult;
import ml.pkom.itemalchemy.api.ItemCharge;
import ml.pkom.mcpitanlibarch.api.event.v0.AttackEntityEventRegistry;
import ml.pkom.mcpitanlibarch.api.item.CompatibleItemSettings;
import ml.pkom.mcpitanlibarch.api.item.ExtendItemProvider;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AlchemicalSword extends SwordItem implements ExtendItemProvider, ItemCharge {
    public AlchemicalSword(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, CompatibleItemSettings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings.build());
        AttackEntityEventRegistry.register((player, world, entity, hand, hitResult) -> {
            ItemStack stack = player.getStackInHand(hand);

            return EventResult.pass();
        });
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        int chargeLevel = getCharge(stack);

        target.damage(DamageSource.mob(attacker), chargeLevel * 4);

        return true;
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        return true;
    }
}
