package net.pitan76.itemalchemy.block.pedestal;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IPedestalItem {
    boolean updateInPedestal(ItemStack stack, net.pitan76.mcpitanlib.midohra.world.World world, net.pitan76.mcpitanlib.midohra.util.math.BlockPos pos);

    default boolean updateInPedestal(ItemStack stack, World world, BlockPos pos) {
        return updateInPedestal(stack, net.pitan76.mcpitanlib.midohra.world.World.of(world), net.pitan76.mcpitanlib.midohra.util.math.BlockPos.of(pos));
    }
}
