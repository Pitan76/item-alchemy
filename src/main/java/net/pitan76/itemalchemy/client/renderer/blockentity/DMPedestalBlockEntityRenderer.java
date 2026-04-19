package net.pitan76.itemalchemy.client.renderer.blockentity;

import net.pitan76.itemalchemy.item.Items;
import net.pitan76.mcpitanlib.api.util.client.render.CompatItemRenderUtil;
import net.pitan76.mcpitanlib.api.util.item.ItemUtil;
import net.pitan76.itemalchemy.tile.DMPedestalTile;
import net.pitan76.mcpitanlib.api.client.registry.CompatRegistryClient;
import net.pitan76.mcpitanlib.api.client.render.block.entity.event.BlockEntityRenderEvent;
import net.pitan76.mcpitanlib.api.client.render.block.entity.v2.CompatBlockEntityRenderer;
import net.pitan76.mcpitanlib.api.util.MathUtil;
import net.pitan76.mcpitanlib.api.util.client.ClientUtil;
import net.pitan76.mcpitanlib.midohra.item.ItemStack;

public class DMPedestalBlockEntityRenderer extends CompatBlockEntityRenderer<DMPedestalTile> {
    public DMPedestalBlockEntityRenderer(CompatRegistryClient.BlockEntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public boolean rendersOutsideBoundingBoxOverride(DMPedestalTile blockEntity) {
        return true;
    }

    @Override
    public void render(BlockEntityRenderEvent<DMPedestalTile> e) {
        DMPedestalTile entity = e.getBlockEntity();
        if (entity == null) return;
        ItemStack stack = entity.getStack();
        if (stack.isEmpty()) return;

        float tickDelta = e.getTickDelta();

        e.push();
        e.translate(0.5, 0.9, 0.5);

        // Bobbing up and down
        float time = ClientUtil.getTime().orElse(0L) + tickDelta;
        float bobbing = (float) Math.sin(time / 10.0F) * 0.1F + 0.1F;
        e.translate(0, bobbing, 0);

        // Rotating
        boolean fastSpin = entity.getActive() && ItemUtil.isOf(stack.toMinecraft(), Items.WATCH_OF_FLOWING_TIME.getOrNull());
        float rotation = time * (fastSpin ? 6.0F : 2.0F);
        e.multiply(MathUtil.RotationAxisType.POSITIVE_Y, rotation);

        e.scale(0.5f, 0.5f, 0.5f);

        CompatItemRenderUtil.renderItemFixed(stack, e, entity.getMidohraWorld());

        e.pop();
    }
}
