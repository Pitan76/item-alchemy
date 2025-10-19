package net.pitan76.itemalchemy.client.renderer;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.Camera;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pitan76.itemalchemy.item.PhilosopherStone;
import net.pitan76.itemalchemy.util.ItemUtils;
import net.pitan76.itemalchemy.util.WorldUtils;
import net.pitan76.mcpitanlib.api.client.event.listener.BeforeBlockOutlineEvent;
import net.pitan76.mcpitanlib.api.client.event.listener.BeforeBlockOutlineListener;
import net.pitan76.mcpitanlib.api.client.event.listener.WorldRenderContext;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.util.BlockStateUtil;
import net.pitan76.mcpitanlib.api.util.client.ClientUtil;

import java.util.List;
import java.util.Optional;

public class BlockRenderer implements BeforeBlockOutlineListener {

    @Override
    public boolean beforeBlockOutline(BeforeBlockOutlineEvent e) {
        if (ClientUtil.getClientPlayer() == null) return true;
        Player player = ClientUtil.getPlayer();

        HitResult hitResult = e.getHitResult();

        if (hitResult == null) return true;
        if (!e.isBlockType()) return true;

        Optional<ItemStack> optionalStack = player.getCurrentHandItem();
        if (!optionalStack.isPresent()) return true;
        ItemStack stack = optionalStack.get();

        if (!(stack.getItem() instanceof PhilosopherStone)) return true;

        WorldRenderContext context = e.getContext();

        Camera camera = context.getCamera();
        World world = e.getWorld();

        Optional<BlockPos> optionalBlockPos = e.getBlockPos();
        if (!optionalBlockPos.isPresent()) return true;

        BlockPos blockPos = optionalBlockPos.get();

        Optional<BlockState> optionalBlockState = e.getBlockState();
        if (!optionalBlockState.isPresent()) return true;

        BlockState blockState = optionalBlockState.get();

        if (BlockStateUtil.isAir(blockState)) return true;
        if (!PhilosopherStone.isExchange(BlockStateUtil.getBlock(blockState))) return true;

        List<BlockPos> blocks = WorldUtils.getTargetBlocks(world, blockPos, ItemUtils.getCharge(stack), true, true);

        for (BlockPos block : blocks) {
            double x = block.getX() - camera.getPos().x;
            double y = block.getY() - camera.getPos().y;
            double z = block.getZ() - camera.getPos().z;

            e.push();
            e.translate(x, y, z);
            e.drawBox(1f, 0.6f, 1f, 1f);
            e.pop();
        }

        return false;
    }
}
