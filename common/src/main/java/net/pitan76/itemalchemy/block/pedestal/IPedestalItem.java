package net.pitan76.itemalchemy.block.pedestal;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pitan76.mcpitanlib.api.registry.CompatRegistryLookup;

public interface IPedestalItem {
    boolean updateInPedestal(net.pitan76.mcpitanlib.midohra.item.ItemStack stack, net.pitan76.mcpitanlib.midohra.world.World world, net.pitan76.mcpitanlib.midohra.util.math.BlockPos pos, CompatRegistryLookup registryLookup);

    default boolean updateInPedestal(ItemStack stack, World world, BlockPos pos, CompatRegistryLookup registryLookup) {
        return updateInPedestal(net.pitan76.mcpitanlib.midohra.item.ItemStack.of(stack),
                net.pitan76.mcpitanlib.midohra.world.World.of(world),
                net.pitan76.mcpitanlib.midohra.util.math.BlockPos.of(pos), registryLookup);
    }

    default boolean updateInPedestal(net.pitan76.mcpitanlib.midohra.item.ItemStack stack, net.pitan76.mcpitanlib.midohra.world.World world, net.pitan76.mcpitanlib.midohra.util.math.BlockPos pos) {
        return updateInPedestal(stack, world, pos, null);
    }
    default boolean updateInPedestal(ItemStack stack, World world, BlockPos pos) {
        return updateInPedestal(net.pitan76.mcpitanlib.midohra.item.ItemStack.of(stack),
                net.pitan76.mcpitanlib.midohra.world.World.of(world),
                net.pitan76.mcpitanlib.midohra.util.math.BlockPos.of(pos), null);
    }
}
