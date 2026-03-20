package net.pitan76.itemalchemy.client.renderer.blockentity;

import net.minecraft.item.ItemStack;
import net.pitan76.mcpitanlib.api.client.render.CompatItemRenderUtil;
import net.pitan76.itemalchemy.tile.DMPedestalTile;
import net.pitan76.mcpitanlib.api.client.registry.CompatRegistryClient;
import net.pitan76.mcpitanlib.api.client.render.block.entity.event.BlockEntityRenderEvent;
import net.pitan76.mcpitanlib.api.client.render.block.entity.v2.CompatBlockEntityRenderer;
import net.pitan76.mcpitanlib.api.util.MathUtil;
import net.pitan76.mcpitanlib.api.util.client.ClientUtil;

public class DMPedestalBlockEntityRenderer extends CompatBlockEntityRenderer<DMPedestalTile> {
    public DMPedestalBlockEntityRenderer(CompatRegistryClient.BlockEntityRendererFactory.Context ctx) {
        super(ctx);
        CompatItemRenderUtil.initFromContext(ctx);
    }

    @Override
    public boolean rendersOutsideBoundingBoxOverride(DMPedestalTile blockEntity) {
        return true;
    }

    @Override
    public void render(BlockEntityRenderEvent<DMPedestalTile> event) {
        DMPedestalTile entity = event.getBlockEntity();
        if (entity == null) return;
        ItemStack stack = entity.getStack();
        if (stack.isEmpty()) return;

        float tickDelta = event.getTickDelta();

        event.push();

        event.translate(0.5, 0.9, 0.5);

        // Bobbing up and down
        float time = ClientUtil.getTime().orElse(0L) + tickDelta;
        float bobbing = (float) Math.sin(time / 10.0F) * 0.1F + 0.1F;
        event.translate(0, bobbing, 0);

        // Rotating
        float rotation = time * 2.0F;
        event.multiply(MathUtil.RotationAxisType.POSITIVE_Y, rotation);

        event.scale(0.5f, 0.5f, 0.5f);

        CompatItemRenderUtil.renderItemFixed(stack, event, entity.callGetWorld());

        event.pop();
    }
}
