package net.pitan76.itemalchemy.client.renderer;

import net.pitan76.itemalchemy.item.AlchemicalPickaxe;
import net.pitan76.itemalchemy.item.PhilosopherStone;
import net.pitan76.itemalchemy.util.ItemUtils;
import net.pitan76.itemalchemy.util.WorldUtils;
import net.pitan76.mcpitanlib.api.client.event.listener.BeforeBlockOutlineEvent;
import net.pitan76.mcpitanlib.api.client.event.listener.BeforeBlockOutlineListener;
import net.pitan76.mcpitanlib.api.client.event.listener.WorldRenderContext;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.util.client.ClientUtil;
import net.pitan76.mcpitanlib.midohra.block.BlockState;
import net.pitan76.mcpitanlib.midohra.client.render.CameraWrapper;
import net.pitan76.mcpitanlib.midohra.item.ItemStack;
import net.pitan76.mcpitanlib.midohra.util.hit.HitResult;
import net.pitan76.mcpitanlib.midohra.util.math.BlockPos;
import net.pitan76.mcpitanlib.midohra.util.math.Direction;
import net.pitan76.mcpitanlib.midohra.util.math.Vector3d;
import net.pitan76.mcpitanlib.midohra.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BlockRenderer implements BeforeBlockOutlineListener {

    @Override
    public boolean beforeBlockOutline(BeforeBlockOutlineEvent e) {
        if (ClientUtil.getClientPlayer() == null) return true;
        Player player = ClientUtil.getPlayer();

        HitResult hitResult = e.getHitResultM();

        if (hitResult.getRaw() == null) return true;
        if (!e.isBlockType()) return true;

        Optional<ItemStack> optionalStack = player.getCurrentHandItemM();
        if (!optionalStack.isPresent()) return true;
        ItemStack stack = optionalStack.get();

        if (stack.getItem().instanceOf(PhilosopherStone.class)) return philosopherStone(e, stack);
        if (stack.getItem().instanceOf(AlchemicalPickaxe.class)) return alchemicalPickaxe(e, stack);


        return true;
    }

    protected boolean philosopherStone(BeforeBlockOutlineEvent e, ItemStack stack) {
        WorldRenderContext context = e.getContext();

        CameraWrapper camera = context.getCameraWrapper();
        World world = World.of(e.getWorld());

        Optional<BlockPos> optionalBlockPos = e.getBlockPos().map(BlockPos::of);
        if (!optionalBlockPos.isPresent()) return true;

        BlockPos blockPos = optionalBlockPos.get();

        Optional<BlockState> optionalBlockState = e.getBlockState().map(BlockState::of);
        if (!optionalBlockState.isPresent()) return true;

        BlockState blockState = optionalBlockState.get();

        if (blockState.isAir()) return true;
        if (!PhilosopherStone.isExchange(blockState.getBlock())) return true;

        List<BlockPos> blocks = WorldUtils.getTargetBlocks(world, blockPos, ItemUtils.getCharge(stack), true, true);

        for (BlockPos pos : blocks) {
            Vector3d cameraPos = camera.getCameraPos();
            double x = pos.getX() - cameraPos.x;
            double y = pos.getY() - cameraPos.y;
            double z = pos.getZ() - cameraPos.z;

            e.push();
            e.translate(x, y, z);
            e.drawBox(1f, 0.6f, 1f, 1f);
            e.pop();
        }

        return false;
    }

    protected boolean alchemicalPickaxe(BeforeBlockOutlineEvent e, ItemStack stack) {
        WorldRenderContext context = e.getContext();

        CameraWrapper camera = context.getCameraWrapper();
        World world = World.of(e.getWorld());

        Optional<BlockPos> optionalBlockPos = e.getBlockPos().map(BlockPos::of);
        if (!optionalBlockPos.isPresent()) return true;

        BlockPos blockPos = optionalBlockPos.get();

        Optional<BlockState> optionalBlockState = e.getBlockState().map(BlockState::of);
        if (!optionalBlockState.isPresent()) return true;

        BlockState blockState = optionalBlockState.get();

        if (blockState.isAir()) return true;

        AlchemicalPickaxe pickaxe = stack.getItem().getICompatItem(AlchemicalPickaxe.class);
        int mode = pickaxe.getMode(stack);

        if (mode < 1) return true;

        Direction direction = Direction.of(ClientUtil.getPlayer().getHorizontalFacing());

        List<BlockPos> blocks = new ArrayList<>();

        blocks.add(blockPos);
        pickaxe.getTargetBlocksFromMode(blocks, world, blockPos, stack, direction, mode);

        for (BlockPos pos : blocks) {
            Vector3d cameraPos = camera.getCameraPos();
            double x = pos.getX() - cameraPos.x;
            double y = pos.getY() - cameraPos.y;
            double z = pos.getZ() - cameraPos.z;

            e.push();
            e.translate(x, y, z);
            e.drawBox(1f, 0.6f, 1f, 1f);
            e.pop();
        }

        return false;
    }
}
