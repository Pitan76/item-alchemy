package net.pitan76.itemalchemy.client.renderer;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.pitan76.itemalchemy.item.PhilosopherStone;
import net.pitan76.itemalchemy.util.ItemUtils;
import net.pitan76.itemalchemy.util.WorldUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockRenderer implements WorldRenderEvents.BeforeBlockOutline{

    @Override
    public boolean beforeBlockOutline(WorldRenderContext context, @Nullable HitResult hitResult) {
        PlayerEntity player = MinecraftClient.getInstance().player;

        if (player == null) return true;
        if (hitResult == null) return true;
        if (hitResult.getType() != HitResult.Type.BLOCK) return true;

        ItemStack stack = ItemUtils.getCurrentHandItem(player);

        if (stack == null) return true;
        if (!(stack.getItem() instanceof PhilosopherStone)) return true;

        MatrixStack matrixStack = context.matrixStack();
        Camera camera = context.camera();
        World world = context.world();

        BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
        BlockState blockState = context.world().getBlockState(blockPos);

        if (blockState.isAir()) return true;
        if (!PhilosopherStone.isExchange(blockState.getBlock())) return true;

        VoxelShape sharp = blockState.getOutlineShape(world, blockPos);
        VertexConsumer consumer = context.consumers().getBuffer(RenderLayer.getLines());

        List<BlockPos> blocks = WorldUtils.getTargetBlocks(context.world(), blockPos, ItemUtils.getCharge(stack), true, true);

        for (BlockPos block : blocks) {
            double x = block.getX() - camera.getPos().x;
            double y = block.getY() - camera.getPos().y;
            double z = block.getZ() - camera.getPos().z;

            matrixStack.push();

            matrixStack.translate(x, y, z);

            WorldRenderer.drawBox(matrixStack, consumer, sharp.getBoundingBox(), 1f, 0.6f, 1f, 1f);

            matrixStack.pop();
        }

        return false;
    }
}
