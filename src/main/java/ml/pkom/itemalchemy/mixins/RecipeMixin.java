package ml.pkom.itemalchemy.mixins;

import ml.pkom.itemalchemy.items.PhilosopherStone;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Task: Move to MCPitanLibarch
@Mixin(Recipe.class)
public interface RecipeMixin<C extends Inventory> {
    @Inject(method = "getRemainder", at = @At("RETURN"))
    default void injectGetRemainder(C inventory, CallbackInfoReturnable<DefaultedList<ItemStack>> cir) {
        DefaultedList<ItemStack> defaultedList = cir.getReturnValue();

        int i = 0;
        for(ItemStack stack : defaultedList) {
           if (stack.getItem() instanceof PhilosopherStone) {
               defaultedList.set(i, stack.copy().getRecipeRemainder());
           }
           i++;
        }
    }
}
