package net.pitan76.itemalchemy.block.pedestal;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IPedestalItem {
    boolean updateInPedestal(ItemStack stack, World world, BlockPos pos);
}
