package net.pitan76.itemalchemy.item;

import net.minecraft.block.Block;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.pitan76.itemalchemy.block.IUseableWrench;
import net.pitan76.mcpitanlib.api.event.item.ItemUseOnBlockEvent;
import net.pitan76.mcpitanlib.api.item.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.item.ExtendItem;
import net.pitan76.mcpitanlib.api.sound.CompatSoundCategory;
import net.pitan76.mcpitanlib.api.sound.CompatSoundEvents;
import net.pitan76.mcpitanlib.api.util.WorldUtil;

public class Wrench extends ExtendItem {

    public Wrench(CompatibleItemSettings settings) {
        super(settings);
    }

    @Override
    public ActionResult onRightClickOnBlock(ItemUseOnBlockEvent e) {
        if (e.isClient()) return ActionResult.SUCCESS;

        Block block = e.getBlockState().getBlock();
        if (block == null) return ActionResult.PASS;

        if (block instanceof IUseableWrench) {
            WorldUtil.breakBlock(e.getWorld(), e.getBlockPos(), true, e.getPlayer());
            WorldUtil.playSound(e.world, null, e.getBlockPos(), CompatSoundEvents.BLOCK_ANVIL_PLACE, CompatSoundCategory.BLOCKS, 0.75f, 1.5f);
        }

        return super.onRightClickOnBlock(e);
    }
}
