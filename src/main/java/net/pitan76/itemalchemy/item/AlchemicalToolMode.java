package net.pitan76.itemalchemy.item;

import net.pitan76.mcpitanlib.api.util.CustomDataUtil;
import net.pitan76.mcpitanlib.api.util.NbtUtil;
import net.pitan76.mcpitanlib.midohra.item.ItemStack;
import net.pitan76.mcpitanlib.midohra.nbt.NbtCompound;

public interface AlchemicalToolMode {

    default int getMaxModeValue() {
        return 4;
    }

    default String getModeKey() {
        return "modechange";
    }

    default int getMode(net.minecraft.item.ItemStack stack) {
        return getMode(ItemStack.of(stack));
    }

    /**
     * Get the current mode of the pickaxe
     */
    default int getMode(ItemStack stack) {
        NbtCompound nbt = stack.getCustomNbt("itemalchemy");
        String modeKey = getModeKey();
        if (!nbt.has(modeKey)) {
            setMode(stack, 0);
            return 0;
        }
        return nbt.getInt(modeKey);
    }

    default void setMode(net.minecraft.item.ItemStack stack, int mode) {
        setMode(ItemStack.of(stack), mode);
    }

    /**
     * Set the mode of the pickaxe
     */
    default void setMode(ItemStack stack, int mode) {
        NbtCompound nbt = stack.getCustomNbt("itemalchemy");
        nbt.putInt(getModeKey(), mode);
        stack.putCustomNbt("itemalchemy", nbt);
    }

    default void incrementMode(net.minecraft.item.ItemStack stack) {
        incrementMode(ItemStack.of(stack));
    }

    /**
     * Increment the mode value, wrapping around to 0 after reaching max mode. (default key "G")
     * @param stack The ItemStack to modify.
     */
    default void incrementMode(ItemStack stack) {
        int currentMode = getMode(stack);
        int maxMode = getMaxModeValue();
        int newMode = (currentMode + 1) % maxMode;
        setMode(stack, newMode);
    }
}
