package net.pitan76.itemalchemy.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.pitan76.itemalchemy.util.ItemUtils;
import net.pitan76.mcpitanlib.api.util.CustomDataUtil;
import net.pitan76.mcpitanlib.api.util.NbtUtil;

public interface AlchemicalToolMode {
    String getModeKey();

    /**
     * Get the current mode of the pickaxe (0 = normal, 1 = efficiency mode, etc.)
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
        // Mode 0 = normal, Mode 1 = efficiency (when fully charged)
        mode = Math.max(0, Math.min(1, mode));
        NbtCompound nbt = CustomDataUtil.get(stack, "itemalchemy");
        NbtUtil.set(nbt, getModeKey(), mode);
    }

    /**
     * Toggle the mode when the key is pressed
     */
    default void toggleMode(ItemStack stack) {
        if (!ItemUtils.isItemChargeable(stack)) return;
        int chargeLevel = ItemUtils.getCharge(stack);
        // Only allow mode toggle when fully charged
        if (chargeLevel < ItemUtils.MAX_CHARGE_VALUE) return;

        int currentMode = getMode(stack);
        setMode(stack, (currentMode + 1) % 2);
    }
}
