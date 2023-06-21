package ml.pkom.itemalchemy.client.renderer;

import ml.pkom.itemalchemy.api.ItemCharge;
import ml.pkom.itemalchemy.item.PhilosopherStone;
import ml.pkom.itemalchemy.util.ItemUtils;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockRenderer implements WorldRenderEvents.BeforeBlockOutline{

    @Override
    public boolean beforeBlockOutline(WorldRenderContext context, @Nullable HitResult hitResult) {
        PlayerEntity player = MinecraftClient.getInstance().player;

        if(player == null) {
            return true;
        }

        if(hitResult == null) {
            return true;
        }

        if(hitResult.getType() != HitResult.Type.BLOCK) {
            return true;
        }

        ItemStack itemStack = ItemUtils.getCurrentHandItem(player);

        if(itemStack == null) {
            return true;
        }

        if(!(itemStack.getItem() instanceof PhilosopherStone)) {
            return true;
        }

        MatrixStack matrixStack = context.matrixStack();
        Camera camera = context.camera();
        World world = context.world();

        BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
        BlockState blockState = context.world().getBlockState(blockPos);

        if(blockState.isAir()) {
            return true;
        }

        VoxelShape sharp = blockState.getOutlineShape(world, blockPos);
        VertexConsumer consumer = context.consumers().getBuffer(RenderLayer.getLines());

        List<BlockPos> blocks = PhilosopherStone.getTargetBlocks(context.world(), blockPos, ((ItemCharge)itemStack.getItem()).getCharge(itemStack));

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
