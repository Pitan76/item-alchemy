package net.pitan76.itemalchemy.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.pitan76.mcpitanlib.api.util.CustomDataUtil;
import net.pitan76.mcpitanlib.api.util.NbtUtil;

public interface AlchemicalToolMode {

    default int getMaxModeValue() {
        return 4;
    }

    default String getModeKey() {
        return "modechange";
    }

    /**
     * Get the current mode of the pickaxe
     */
    default int getMode(ItemStack stack) {
        NbtCompound nbt = CustomDataUtil.get(stack, "itemalchemy");
        String modeKey = getModeKey();
        if (!NbtUtil.has(nbt, modeKey)) {
            setMode(stack, 0);
            return 0;
        }
        return NbtUtil.getInt(nbt, modeKey);
    }

    /**
     * Set the mode of the pickaxe
     */
    default void setMode(ItemStack stack, int mode) {
        NbtCompound nbt = CustomDataUtil.get(stack, "itemalchemy");
        NbtUtil.set(nbt, getModeKey(), mode);
        CustomDataUtil.put(stack, "itemalchemy", nbt);
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
