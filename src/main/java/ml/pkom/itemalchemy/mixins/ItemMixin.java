package ml.pkom.itemalchemy.mixins;

import ml.pkom.itemalchemy.api.ItemCharge;
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
    @Inject(method = "onCraft", at = @At("TAIL"))
    public void onCraft(ItemStack stack, World world, PlayerEntity player, CallbackInfo ci) {
        if(stack.getItem() instanceof ItemCharge) {
            if(stack.getSubNbt("itemalchemy") == null) {
                ((ItemCharge) stack.getItem()).setCharge(stack, 0);
            }
        }
    }
}
