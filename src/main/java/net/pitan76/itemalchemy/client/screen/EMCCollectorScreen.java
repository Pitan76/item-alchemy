package net.pitan76.itemalchemy.client.screen;

import net.pitan76.itemalchemy.ItemAlchemy;
import net.pitan76.itemalchemy.gui.screen.EMCCollectorScreenHandler;
import net.pitan76.mcpitanlib.api.client.SimpleHandledScreen;
import net.pitan76.mcpitanlib.api.client.render.handledscreen.DrawBackgroundArgs;
import net.pitan76.mcpitanlib.api.client.render.handledscreen.DrawForegroundArgs;
import net.pitan76.mcpitanlib.api.client.render.handledscreen.DrawMouseoverTooltipArgs;
import net.pitan76.mcpitanlib.api.client.render.handledscreen.RenderArgs;
import net.pitan76.mcpitanlib.api.util.client.RenderUtil;
import net.pitan76.mcpitanlib.api.util.client.ScreenUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.pitan76.mcpitanlib.api.util.TextUtil;

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
    public void drawForegroundOverride(DrawForegroundArgs args) {
        //textRenderer.draw(matrices, getTitle(), (float) titleX, (float) titleY, 4210752);
        long emc = (screenHandler.storedEMC + screenHandler.tile.storedEMC);
        if (emc > screenHandler.maxEMC) emc = screenHandler.maxEMC;

        ScreenUtil.RendererUtil.drawText(textRenderer, args.drawObjectDM, TextUtil.literal(String.format("%,d", emc)), 92, 32, 4210752);
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
        callRenderBackground(args);
        super.renderOverride(args);
        callDrawMouseoverTooltip(new DrawMouseoverTooltipArgs(args.drawObjectDM, args.mouseX, args.mouseY));
    }
}
