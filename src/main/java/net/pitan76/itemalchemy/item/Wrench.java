package net.pitan76.itemalchemy.item;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.pitan76.itemalchemy.block.IUseableWrench;
import net.pitan76.mcpitanlib.api.event.item.ItemUseOnBlockEvent;
import net.pitan76.mcpitanlib.api.item.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.item.ExtendItem;
import net.pitan76.mcpitanlib.api.sound.CompatSoundCategory;
import net.pitan76.mcpitanlib.api.sound.CompatSoundEvents;
import net.pitan76.mcpitanlib.api.tile.CompatBlockEntity;
import net.pitan76.mcpitanlib.api.util.BlockEntityDataUtil;
import net.pitan76.mcpitanlib.api.util.ItemStackUtil;
import net.pitan76.mcpitanlib.api.util.WorldUtil;
import net.pitan76.mcpitanlib.api.util.entity.ItemEntityUtil;

public class Wrench extends ExtendItem {

    public Wrench(CompatibleItemSettings settings) {
        super(settings);
    }

    @Override
    public ActionResult onRightClickOnBlock(ItemUseOnBlockEvent e) {
        if (e.isClient()) return e.success();

        Block block = e.getBlockState().getBlock();
        BlockPos pos = e.getBlockPos();

        if (block == null) return e.pass();

        if (block instanceof IUseableWrench) {
            WorldUtil.playSound(e.world, null, e.getBlockPos(), CompatSoundEvents.BLOCK_ANVIL_PLACE, CompatSoundCategory.BLOCKS, 0.75f, 1.5f);

            if (e.hasBlockEntity()) {
                BlockEntity blockEntity = e.getBlockEntity();

                if (blockEntity instanceof CompatBlockEntity) {
                    ItemStack dropStack = ItemStackUtil.create(block);
                    BlockEntityDataUtil.writeCompatBlockEntityNbtToStack(dropStack, (CompatBlockEntity) blockEntity);

                    WorldUtil.removeBlockEntity(e.getWorld(), e.getBlockPos());
                    WorldUtil.removeBlock(e.getWorld(), e.getBlockPos(), false);

                    ItemEntity itemEntity = ItemEntityUtil.create(e.getWorld(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, dropStack);
                    itemEntity.setToDefaultPickupDelay();
                    WorldUtil.spawnEntity(e.getWorld(), itemEntity);

                    return e.success();
                }
            }

            WorldUtil.breakBlock(e.getWorld(), e.getBlockPos(), true, e.getPlayer());
            return e.success();

        }

        return super.onRightClickOnBlock(e);
    }
}
