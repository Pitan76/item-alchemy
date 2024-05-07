package net.pitan76.itemalchemy.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.pitan76.mcpitanlib.api.util.CustomDataUtil;
import org.jetbrains.annotations.Nullable;

public class ItemUtils {
    @Nullable
    public static ItemStack getCurrentHandItem(PlayerEntity player) {
        if (!player.getMainHandStack().isEmpty()) {
            return player.getMainHandStack();
        }

        if (!player.getOffHandStack().isEmpty()) {
            return player.getOffHandStack();
        }

        return null;
    }

    public static int getCharge(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemCharge)) {
            return 0;
        }

        if(VersionUtils.isSupportedComponent()) {
            NbtCompound component = CustomDataUtil.get(stack, "itemalchemy");

            if (!component.contains("charge")) {
                setCharge(stack, 0);
            }

            return component.getInt("charge");
        }
        else {
            NbtCompound nbt = stack.getOrCreateSubNbt("itemalchemy");

            if (!nbt.contains("charge")) {
                setCharge(stack, 0);
            }

            return nbt.getInt("charge");
        }
    }

    public static void setCharge(ItemStack stack, int charge) {
        if (!(stack.getItem() instanceof ItemCharge)) {
            return;
        }

        if (charge < 0 || charge > 4) {
            return;
        }

        if(VersionUtils.isSupportedComponent()) {
            NbtCompound component = CustomDataUtil.get(stack, "itemalchemy");

            component.putInt("charge", charge);
            CustomDataUtil.set(stack, "itemalchemy", component);
        }
        else {
            NbtCompound nbt = stack.getOrCreateSubNbt("itemalchemy");

            nbt.putInt("charge", charge);
        }
    }
}
