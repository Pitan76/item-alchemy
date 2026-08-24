package net.pitan76.itemalchemy.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.pitan76.itemalchemy.ItemAlchemy;
import net.pitan76.mcpitanlib.api.util.CustomDataUtil;
import net.pitan76.mcpitanlib.api.util.ItemStackUtil;
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

    // Key for the charge level the player wants the item to be kept at.
    public static final String TARGET_CHARGE_COMPONENT_KEY = "target_charge";

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

        return stack.getItem().instanceOf(ItemCharge.class);
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

    /**
     * Returns the charge level the item should be automatically recharged up to.
     * Defaults to the item's maximum charge when the player has never set it manually.
     *
     * @param stack of the item to get the target charge value.
     * @return {@code int} of the target charge value.
     */
    public static int getTargetCharge(ItemStack stack) {
        int max = getMaxCharge(stack);
        if (!isItemChargeable(stack)) return max;

        NbtCompound nbt = CustomDataUtil.get(stack, ItemAlchemy.MOD_ID);
        if (!NbtUtil.has(nbt, TARGET_CHARGE_COMPONENT_KEY)) return max;

        return constrainToRange(NbtUtil.getInt(nbt, TARGET_CHARGE_COMPONENT_KEY), MIN_CHARGE_VALUE, max);
    }

    public static int getTargetCharge(net.pitan76.mcpitanlib.midohra.item.ItemStack stack) {
        return getTargetCharge(stack.toMinecraft());
    }

    /**
     * Sets the charge level automatic recharging is allowed to reach.
     *
     * @param stack of the item to set the target charge value.
     * @param targetCharge value to set the {@code stack} to.
     */
    public static void setTargetCharge(ItemStack stack, int targetCharge) {
        if (!isItemChargeable(stack)) return;

        targetCharge = constrainToRange(targetCharge, MIN_CHARGE_VALUE, getMaxCharge(stack));

        NbtCompound nbt = CustomDataUtil.get(stack, ItemAlchemy.MOD_ID);
        NbtUtil.set(nbt, TARGET_CHARGE_COMPONENT_KEY, targetCharge);
        CustomDataUtil.set(stack, ItemAlchemy.MOD_ID, nbt);
    }

    public static void setTargetCharge(net.pitan76.mcpitanlib.midohra.item.ItemStack stack, int targetCharge) {
        setTargetCharge(stack.toMinecraft(), targetCharge);
    }

    // Color of the charge bar shown under chargeable items.
    public static final int CHARGE_BAR_COLOR = 0xE01919;

    // Number of segments in a vanilla item bar.
    private static final int ITEM_BAR_SEGMENTS = 13;

    /**
     * Returns the item bar step (0-13) representing the charge level of the given {@link ItemStack}.
     * Chargeable items are not damageable, so the vanilla damage based bar would always render full.
     */
    public static int getChargeBarStep(ItemStack stack) {
        int max = getMaxCharge(stack);
        if (max <= 0) return 0;

        return ITEM_BAR_SEGMENTS * getCharge(stack) / max;
    }

    public static int getChargeBarStep(net.pitan76.mcpitanlib.midohra.item.ItemStack stack) {
        return getChargeBarStep(stack.toMinecraft());
    }

    /**
     * Returns the maximum charge level of the given {@link ItemStack}, honoring
     * {@link IRechargeableFromKlein#getMaxCharge()} when the item defines its own limit.
     */
    public static int getMaxCharge(@Nullable ItemStack stack) {
        if (stack == null) return MAX_CHARGE_VALUE;

        if (ItemStackUtil.getItem(stack) instanceof IRechargeableFromKlein)
            return ((IRechargeableFromKlein) ItemStackUtil.getItem(stack)).getMaxCharge();

        return MAX_CHARGE_VALUE;
    }
}
