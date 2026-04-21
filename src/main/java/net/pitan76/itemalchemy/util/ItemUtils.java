package net.pitan76.itemalchemy.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.pitan76.itemalchemy.ItemAlchemy;
import net.pitan76.mcpitanlib.api.util.CustomDataUtil;
import net.pitan76.mcpitanlib.api.util.NbtUtil;
import org.jetbrains.annotations.Nullable;

import static com.google.common.primitives.Ints.constrainToRange;

/**
 * Provides utility functions relating to {@link ItemStack}s and their {@link ItemAlchemy} charge
 * level.
 */
public class ItemUtils {

    // DEBUG flag - set to true to enable diagnostic logging
    private static final boolean DEBUG_CHARGE = false;

    // Key for getting back charge value of an item.
    public static final String CHARGE_COMPONENT_KEY = "charge";

    // Minimum charge value allowed.
    public static final int MIN_CHARGE_VALUE = 0;
    // Maximum charge value allowed.
    public static final int MAX_CHARGE_VALUE = 4;

    /**
     * Checks if the given {@link ItemStack} is chargeable via {@link ItemCharge}.
     *
     * @param stack to check.
     * @return false if {@code stack} is null or not chargeable. True if stack is non-null and
     *     chargeable.
     */
    public static boolean isItemChargeable(@Nullable ItemStack stack) {
        if (stack == null)
            return false;

        return stack.getItem() instanceof ItemCharge;
    }

    public static boolean isItemChargeable(@Nullable net.pitan76.mcpitanlib.midohra.item.ItemStack stack) {
        if (stack == null)
            return false;

        return stack.toMinecraft().getItem() instanceof ItemCharge;
    }

    /**
     * Returns the charge value of an {@link ItemStack}. If the {@link ItemStack} does not have a
     * charge value set, it sets it to 0 and returns 0.
     *
     * @param stack of the item to get the charge value.
     * @return {@code int} of the charge value between [0-4].
     */
    public static int getCharge(ItemStack stack) {
        if (!isItemChargeable(stack))
            return MIN_CHARGE_VALUE;

        NbtCompound nbt = CustomDataUtil.get(stack, ItemAlchemy.MOD_ID);

        if (!NbtUtil.has(nbt, CHARGE_COMPONENT_KEY)) {
            setCharge(stack, MIN_CHARGE_VALUE);
            if (DEBUG_CHARGE) {
                System.out.println("[DEBUG-CHARGE] getCharge: initialized charge to 0 for " + stack.getItem().getClass().getSimpleName());
            }
            return MIN_CHARGE_VALUE;
        }

        int charge = NbtUtil.getInt(nbt, CHARGE_COMPONENT_KEY);
        if (DEBUG_CHARGE && charge > 0) {
            System.out.println("[DEBUG-CHARGE] getCharge: read charge=" + charge + " for " + stack.getItem().getClass().getSimpleName());
        }
        return charge;
    }

    public static int getCharge(net.pitan76.mcpitanlib.midohra.item.ItemStack stack) {
        return getCharge(stack.toMinecraft());
    }

    /**
     * Sets the charge value of an {@link ItemStack}.
     *
     * @param stack of the item to set the charge value.
     * @param charge value to set the {@code stack} to.
     */
    public static void setCharge(ItemStack stack, int charge) {
        if (!isItemChargeable(stack)) return;

        // Needed as method is under Guava beta right now.
        charge = constrainToRange(charge, MIN_CHARGE_VALUE, MAX_CHARGE_VALUE);

        NbtCompound nbt = CustomDataUtil.get(stack, ItemAlchemy.MOD_ID);

        int oldCharge = NbtUtil.has(nbt, CHARGE_COMPONENT_KEY) ? NbtUtil.getInt(nbt, CHARGE_COMPONENT_KEY) : -1;
        NbtUtil.set(nbt, CHARGE_COMPONENT_KEY, charge);
        CustomDataUtil.set(stack, ItemAlchemy.MOD_ID, nbt);

        if (DEBUG_CHARGE && oldCharge != charge) {
            System.out.println("[DEBUG-CHARGE] setCharge: changed from " + oldCharge + " to " + charge + " for " + stack.getItem().getClass().getSimpleName());
        }
    }

    public static void setCharge(net.pitan76.mcpitanlib.midohra.item.ItemStack stack, int charge) {
        setCharge(stack.toMinecraft(), charge);
    }
}
