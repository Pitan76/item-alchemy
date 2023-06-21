package ml.pkom.itemalchemy.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class ItemUtils {
    public static ItemStack getCurrentHandItem(PlayerEntity player) {
        if(!player.getMainHandStack().isEmpty()) {
            return player.getMainHandStack();
        }

        if(!player.getOffHandStack().isEmpty()) {
            return player.getOffHandStack();
        }

        return null;
    }
}
