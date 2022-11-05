package ml.pkom.itemalchemy.mixins.client;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static ml.pkom.itemalchemy.ItemAlchemyClient.getEmcText;

@Mixin(Screen.class)
public class ScreenMixin {

    @Inject(method = "getTooltipFromItem", at = @At("TAIL"))
    public void getTooltipFromItem(ItemStack stack, CallbackInfoReturnable<List<Text>> cir) {
        cir.getReturnValue().addAll(getEmcText(stack));
    }


}
