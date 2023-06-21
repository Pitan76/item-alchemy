package ml.pkom.itemalchemy.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class ItemUtils {
    public static ItemStack getCurrentHandItem(PlayerEntity player) {
        if(player.getMainHandStack() != null) {
            return player.getMainHandStack();
        }

        if(player.getOffHandStack() != null) {
            return player.getOffHandStack();
        }

        return null;
    }
}
