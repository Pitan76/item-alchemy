package ml.pkom.itemalchemy.mixins;

import ml.pkom.itemalchemy.util.ItemCharge;
import ml.pkom.itemalchemy.util.ItemUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "inventoryTick", at = @At("TAIL"))
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if(world.isClient) {
            return;
        }

        if(stack.getItem() instanceof ItemCharge) {
            if(stack.getSubNbt("itemalchemy") == null) {
                ItemUtils.setCharge(stack, 0);
            }

            int charge = ItemUtils.getCharge(stack);
            stack.setDamage(stack.getMaxDamage() - charge * 4);
        }
    }
}
