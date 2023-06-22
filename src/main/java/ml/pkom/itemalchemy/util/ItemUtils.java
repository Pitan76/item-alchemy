package ml.pkom.itemalchemy.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

public class ItemUtils {
    @Nullable
    public static ItemStack getCurrentHandItem(PlayerEntity player) {
        if(!player.getMainHandStack().isEmpty()) {
            return player.getMainHandStack();
        }

        if(!player.getOffHandStack().isEmpty()) {
            return player.getOffHandStack();
        }

        return null;
    }

    public static int getCharge(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateSubNbt("itemalchemy");

        if(!nbt.contains("charge")) {
            setCharge(stack, 0);
        }

        return stack.getOrCreateSubNbt("itemalchemy").getInt("charge");
    }

    public static void setCharge(ItemStack stack, int charge) {
        NbtCompound nbt = stack.getOrCreateSubNbt("itemalchemy");

        if(charge < 0 || charge > 4) {
            return;
        }

        nbt.putInt("charge", charge);
    }
}
