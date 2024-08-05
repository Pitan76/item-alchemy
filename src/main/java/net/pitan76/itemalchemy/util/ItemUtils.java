package net.pitan76.itemalchemy.util;

import static com.google.common.primitives.Ints.constrainToRange;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.pitan76.itemalchemy.ItemAlchemy;
import net.pitan76.itemalchemy.mixins.ItemMixin;
import net.pitan76.mcpitanlib.api.util.CustomDataUtil;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Provides utility functions relating to {@link ItemStack}s and their {@link ItemAlchemy} charge
 * level.
 */
public class ItemUtils {

  // Key for getting back charge value of an item.
  public static final String CHARGE_COMPONENT_KEY = "charge";

  // Minimum charge value allowed.
  public static final int MIN_CHARGE_VALUE = 0;
  // Maximum charge value allowed.
  public static final int MAX_CHARGE_VALUE = 4;

  /**
   * Handles {@link ItemMixin#inventoryTick(ItemStack, World, Entity, int, boolean, CallbackInfo)}
   * if the selected inventory item is of {@link ItemCharge}.
   *
   * @param itemStack that is selected and of {@link ItemCharge}.
   */
  public static void handleItemChargeInventoryTick(ItemStack itemStack) {
    int charge = getCharge(itemStack);
    int damage = itemStack.getMaxDamage() - (charge * 4);
    itemStack.setDamage(damage);
  }

  /**
   * Returns the current {@link ItemStack} in the {@link PlayerEntity}'s hand, or offhand if the
   * main hand is empty.
   *
   * @param player to check current hand item.
   * @return {@code ItemStack} that the {@link PlayerEntity} is holding. Can be {@link null}.
   */
  @Nullable
  public static ItemStack getCurrentHandItem(PlayerEntity player) {
    boolean playerIsHoldingInMainHand = !player.getMainHandStack().isEmpty();
    if (playerIsHoldingInMainHand) {
      return player.getMainHandStack();
    }

    boolean playerIsHoldingInOffHand = !player.getOffHandStack().isEmpty();
    if (playerIsHoldingInOffHand) {
      return player.getOffHandStack();
    }

    return null;
  }

  /**
   * Checks if the given {@link ItemStack} is chargeable via {@link ItemCharge}.
   *
   * @param stack to check.
   * @return false if {@code stack} is null or not chargeable. True if stack is non-null and
   *     chargeable.
   */
  public static boolean isItemChargeable(@Nullable ItemStack stack) {
    if (stack == null) {
      return false;
    }

    return stack.getItem() instanceof ItemCharge;
  }

  /**
   * Returns the charge value of an {@link ItemStack}. If the {@link ItemStack} does not have a
   * charge value set, it sets it to 0 and returns 0.
   *
   * @param stack of the item to get the charge value.
   * @return {@code int} of the charge value between [0-4].
   */
  public static int getCharge(ItemStack stack) {
    if (!isItemChargeable(stack)) {
      return MIN_CHARGE_VALUE;
    }

    NbtCompound component = CustomDataUtil.get(stack, ItemAlchemy.MOD_ID);

    if (!component.contains(CHARGE_COMPONENT_KEY)) {
      setCharge(stack, MIN_CHARGE_VALUE);
      return MIN_CHARGE_VALUE;
    }

    return component.getInt(CHARGE_COMPONENT_KEY);
  }

  /**
   * Sets the charge value of an {@link ItemStack}.
   *
   * @param stack of the item to set the charge value.
   * @param charge value to set the {@code stack} to.
   */
  public static void setCharge(ItemStack stack, int charge) {
    if (!isItemChargeable(stack)) {
      return;
    }

    // Needed as method is under Guava beta right now.
    //noinspection UnstableApiUsage
    charge = constrainToRange(charge, MIN_CHARGE_VALUE, MAX_CHARGE_VALUE);

    NbtCompound component = CustomDataUtil.get(stack, ItemAlchemy.MOD_ID);

    component.putInt(CHARGE_COMPONENT_KEY, charge);
    CustomDataUtil.set(stack, ItemAlchemy.MOD_ID, component);
  }
}
