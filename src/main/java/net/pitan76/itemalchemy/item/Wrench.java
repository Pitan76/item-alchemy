package net.pitan76.itemalchemy.item;

import net.pitan76.itemalchemy.block.IUseableWrench;
import net.pitan76.mcpitanlib.api.event.item.ItemUseOnBlockEvent;
import net.pitan76.mcpitanlib.api.item.v2.CompatItem;
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.sound.CompatSoundCategory;
import net.pitan76.mcpitanlib.api.sound.CompatSoundEvents;
import net.pitan76.mcpitanlib.api.tile.CompatBlockEntity;
import net.pitan76.mcpitanlib.api.util.BlockEntityDataUtil;
import net.pitan76.mcpitanlib.api.util.CompatActionResult;
import net.pitan76.mcpitanlib.api.util.entity.ItemEntityUtil;
import net.pitan76.mcpitanlib.midohra.block.BlockWrapper;
import net.pitan76.mcpitanlib.midohra.item.ItemStack;
import net.pitan76.mcpitanlib.midohra.util.math.BlockPos;
import net.pitan76.mcpitanlib.midohra.world.World;

import java.util.Optional;

public class Wrench extends CompatItem  {

    public Wrench(CompatibleItemSettings settings) {
        super(settings);
    }

    @Override
    public CompatActionResult onRightClickOnBlock(ItemUseOnBlockEvent e) {
        if (e.isClient()) return e.success();

        BlockWrapper block = e.getBlockWrapper();
        BlockPos pos = e.getMidohraPos();
        World world = e.getMidohraWorld();

        if (!(block.get() instanceof IUseableWrench)) return e.pass();

        world.playSound(pos, CompatSoundEvents.BLOCK_ANVIL_PLACE, CompatSoundCategory.BLOCKS, 0.75f, 1.5f);

        Optional<CompatBlockEntity> optionalCompatBlockEntity = e.getBlockEntityWrapper().toCompatBlockEntity();
        if (optionalCompatBlockEntity.isPresent()) {
            ItemStack dropStack = block.asItem().createStack();
            BlockEntityDataUtil.writeCompatBlockEntityNbtToStack(dropStack.toMinecraft(), optionalCompatBlockEntity.get());

            world.removeBlockEntity(pos);
            world.removeBlock(pos, false);

            ItemEntityUtil.createWithSpawn(world, dropStack, pos.toCenterVector3d());

            return e.success();
        }

        world.breakBlock(pos, true, e.getPlayer());

        return e.success();
    }
}
