package net.pitan76.itemalchemy.item;

import net.pitan76.mcpitanlib.midohra.item.ItemStack;
import net.pitan76.mcpitanlib.midohra.nbt.NbtCompound;

/**
 * 防具の特殊能力のインターフェース。
 */
public interface IArmorEffect {

    String EFFECT_KEY = "armor_effect";

    /**
     * 能力が有効かどうか。
     */
    default boolean isEffectEnabled(ItemStack stack) {
        NbtCompound nbt = stack.getCustomNbt("itemalchemy");
        if (!nbt.has(EFFECT_KEY)) return true;

        return nbt.getBoolean(EFFECT_KEY);
    }

    default boolean isEffectEnabled(net.minecraft.item.ItemStack stack) {
        return isEffectEnabled(ItemStack.of(stack));
    }

    default void setEffectEnabled(ItemStack stack, boolean enabled) {
        NbtCompound nbt = stack.getCustomNbt("itemalchemy");
        nbt.putBoolean(EFFECT_KEY, enabled);
        stack.putCustomNbt("itemalchemy", nbt);
    }

    default void setEffectEnabled(net.minecraft.item.ItemStack stack, boolean enabled) {
        setEffectEnabled(ItemStack.of(stack), enabled);
    }

    default boolean toggleEffect(ItemStack stack) {
        boolean enabled = !isEffectEnabled(stack);
        setEffectEnabled(stack, enabled);

        return enabled;
    }

    default boolean toggleEffect(net.minecraft.item.ItemStack stack) {
        return toggleEffect(ItemStack.of(stack));
    }
}
