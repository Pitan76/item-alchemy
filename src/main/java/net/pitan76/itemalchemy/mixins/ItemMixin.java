package net.pitan76.itemalchemy.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.pitan76.itemalchemy.ItemAlchemy;
import net.pitan76.itemalchemy.util.ItemCharge;
import net.pitan76.itemalchemy.util.ItemUtils;
import net.pitan76.mcpitanlib.api.util.CustomDataUtil;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public class ItemMixin {
  @Inject(method = "inventoryTick", at = @At("TAIL"))
  public void inventoryTick(
      ItemStack stack, World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
    if (world.isClient) {
      return;
    }

    if (selected && ItemUtils.isItemChargeable(stack)) {
      ItemUtils.handleItemChargeInventoryTick(stack);
    }
  }
}
