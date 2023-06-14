package ml.pkom.itemalchemy.client.screen;

import ml.pkom.itemalchemy.ItemAlchemy;
import ml.pkom.itemalchemy.gui.screen.EMCCollectorScreenHandler;
import ml.pkom.mcpitanlibarch.api.client.SimpleHandledScreen;
import ml.pkom.mcpitanlibarch.api.client.render.handledscreen.DrawBackgroundArgs;
import ml.pkom.mcpitanlibarch.api.client.render.handledscreen.DrawMouseoverTooltipArgs;
import ml.pkom.mcpitanlibarch.api.client.render.handledscreen.RenderArgs;
import ml.pkom.mcpitanlibarch.api.util.TextUtil;
import ml.pkom.mcpitanlibarch.api.util.client.RenderUtil;
import ml.pkom.mcpitanlibarch.api.util.client.ScreenUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class EMCCollectorScreen extends SimpleHandledScreen {
    public PlayerInventory playerInventory;

    EMCCollectorScreenHandler screenHandler;

    public EMCCollectorScreen(ScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.playerInventory = inventory;
        setBackgroundWidth(208);
        setBackgroundHeight(166);
        if (handler instanceof EMCCollectorScreenHandler) {
            screenHandler = (EMCCollectorScreenHandler) handler;
        }
    }

    @Override
    public void initOverride() {
        super.initOverride();

    }

    public Identifier getTexture() {
        return ItemAlchemy.id("textures/gui/emc_collector.png");
    }

    @Override
    public void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        //textRenderer.draw(matrices, getTitle(), (float) titleX, (float) titleY, 4210752);
        long emc = (screenHandler.storedEMC + screenHandler.tile.storedEMC);
        if (emc > screenHandler.maxEMC) emc = screenHandler.maxEMC;

        if (screenHandler != null)
            textRenderer.draw(matrices, TextUtil.literal("" + String.format("%,d", emc) ), 92, 32, 4210752);
    }

    @Override
    public void drawBackgroundOverride(DrawBackgroundArgs args) {
        long emc = (screenHandler.storedEMC + screenHandler.tile.storedEMC);
        if (emc > screenHandler.maxEMC) emc = screenHandler.maxEMC;

        RenderUtil.setShaderToPositionTexProgram();
        RenderUtil.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        callDrawTexture(args.drawObjectDM, getTexture(), x, y, 0, 0, backgroundWidth, backgroundHeight);
        if (screenHandler != null)
            callDrawTexture(args.drawObjectDM, getTexture(), x + 93, y + 13, 0, 168, (int) Math.round((double) emc / screenHandler.maxEMC * 46), 14);
    }

    @Override
    public void renderOverride(RenderArgs args) {
        callRenderBackground(args.drawObjectDM);
        super.renderOverride(args);
        callDrawMouseoverTooltip(new DrawMouseoverTooltipArgs(args.drawObjectDM, args.mouseX, args.mouseY));
    }
}
