package net.pitan76.itemalchemy.util;

import static com.google.common.primitives.Ints.constrainToRange;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.pitan76.itemalchemy.ItemAlchemy;
import net.pitan76.mcpitanlib.api.util.CustomDataUtil;
import org.jetbrains.annotations.Nullable;

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
   * Returns the charge value of an {@link ItemStack}.
   *
   * @param stack of the item to get the charge value.
   * @return {@code int} of the charge value between [0-4].
   */
  public static int getCharge(ItemStack stack) {
    boolean isChargeableItem = stack.getItem() instanceof ItemCharge;
    if (!isChargeableItem) {
      return MIN_CHARGE_VALUE;
    }

    NbtCompound component = CustomDataUtil.get(stack, ItemAlchemy.MOD_ID);

    if (!component.contains(CHARGE_COMPONENT_KEY)) {
      setCharge(stack, MIN_CHARGE_VALUE);
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
    boolean isChargeableItem = stack.getItem() instanceof ItemCharge;
    if (!isChargeableItem) {
      return;
    }

    //noinspection UnstableApiUsage
    charge = constrainToRange(charge, MIN_CHARGE_VALUE, MAX_CHARGE_VALUE);

    NbtCompound component = CustomDataUtil.get(stack, ItemAlchemy.MOD_ID);

    component.putInt(CHARGE_COMPONENT_KEY, charge);
    CustomDataUtil.set(stack, ItemAlchemy.MOD_ID, component);
  }
}
