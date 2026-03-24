package net.pitan76.itemalchemy.item;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.pitan76.itemalchemy.block.IUseableWrench;
import net.pitan76.mcpitanlib.api.event.item.ItemUseOnBlockEvent;
import net.pitan76.mcpitanlib.api.item.v2.CompatItem;
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.sound.CompatSoundCategory;
import net.pitan76.mcpitanlib.api.sound.CompatSoundEvents;
import net.pitan76.mcpitanlib.api.tile.CompatBlockEntity;
import net.pitan76.mcpitanlib.api.util.BlockEntityDataUtil;
import net.pitan76.mcpitanlib.api.util.CompatActionResult;
import net.pitan76.mcpitanlib.api.util.ItemStackUtil;
import net.pitan76.mcpitanlib.api.util.WorldUtil;
import net.pitan76.mcpitanlib.api.util.entity.ItemEntityUtil;
import net.pitan76.mcpitanlib.midohra.util.math.BlockPos;
import net.pitan76.mcpitanlib.midohra.world.World;

public class Wrench extends CompatItem  {

    public Wrench(CompatibleItemSettings settings) {
        super(settings);
    }

    @Override
    public CompatActionResult onRightClickOnBlock(ItemUseOnBlockEvent e) {
        if (e.isClient()) return e.success();

        Block block = e.getBlockState().getBlock();
        BlockPos pos = e.getMidohraPos();
        World world = e.getMidohraWorld();

        if (block == null) return e.pass();

        if (block instanceof IUseableWrench) {
            world.playSound(pos, CompatSoundEvents.BLOCK_ANVIL_PLACE, CompatSoundCategory.BLOCKS, 0.75f, 1.5f);

            if (e.hasBlockEntity()) {
                BlockEntity blockEntity = e.getBlockEntity();

                if (blockEntity instanceof CompatBlockEntity) {
                    ItemStack dropStack = ItemStackUtil.create(block);
                    BlockEntityDataUtil.writeCompatBlockEntityNbtToStack(dropStack, (CompatBlockEntity) blockEntity);

                    world.removeBlockEntity(pos);
                    world.removeBlock(pos, false);

                    ItemEntityUtil.createWithSpawn(e.getWorld(), dropStack, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

                    return e.success();
                }
            }

            WorldUtil.breakBlock(e.getWorld(), e.getBlockPos(), true, e.getPlayer());
            return e.success();

        }

        return super.onRightClickOnBlock(e);
    }
}
