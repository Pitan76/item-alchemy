package ml.pkom.itemalchemy.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public interface ItemCharge {
    default int getCharge(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateSubNbt("itemalchemy");

        if(!nbt.contains("charge")) {
            setCharge(stack, 0);
        }

        return stack.getOrCreateSubNbt("itemalchemy").getInt("charge");
    }

    default void setCharge(ItemStack stack, int charge) {
        NbtCompound nbt = stack.getOrCreateSubNbt("itemalchemy");

        if(charge < 0 || charge > 4) {
            return;
        }

        stack.setDamage(Math.max(stack.getMaxDamage() - charge * 2, 1));

        nbt.putInt("charge", charge);
    }
}
