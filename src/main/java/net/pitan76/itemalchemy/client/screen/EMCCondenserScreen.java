package net.pitan76.itemalchemy.client.screen;

import net.pitan76.itemalchemy.ItemAlchemy;
import net.pitan76.itemalchemy.gui.screen.EMCCondenserScreenHandler;
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

public class EMCCondenserScreen extends SimpleHandledScreen {
    public PlayerInventory playerInventory;

    EMCCondenserScreenHandler screenHandler;

    public EMCCondenserScreen(ScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.playerInventory = inventory;
        setBackgroundWidth(256);
        setBackgroundHeight(234);
        if (handler instanceof EMCCondenserScreenHandler) {
            screenHandler = (EMCCondenserScreenHandler) handler;
        }
    }

    @Override
    public void initOverride() {
        super.initOverride();

    }

    public Identifier getTexture() {
        return ItemAlchemy.id("textures/gui/emc_condenser.png");
    }

    @Override
    public void drawForegroundOverride(DrawForegroundArgs args) {
        long emc = (screenHandler.storedEMC + screenHandler.tile.storedEMC);
        if (emc > screenHandler.maxEMC) emc = screenHandler.maxEMC;

        ScreenUtil.RendererUtil.drawText(textRenderer, args.drawObjectDM, TextUtil.literal("" + String.format("%,d", emc) ), 140, 10, 4210752);
    }

    @Override
    public void drawBackgroundOverride(DrawBackgroundArgs args) {
        long emc = (screenHandler.storedEMC + screenHandler.tile.storedEMC);
        if (emc > screenHandler.maxEMC) emc = screenHandler.maxEMC;

        RenderUtil.setShaderToPositionTexProgram();
        RenderUtil.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        callDrawTexture(args.drawObjectDM, getTexture(), x, y, 0, 0, backgroundWidth, backgroundHeight);
        if (screenHandler != null)
            callDrawTexture(args.drawObjectDM, getTexture(), x + 31, y + 7, 0, 240, (int) Math.round((double) emc / screenHandler.maxEMC * 106), 14);
    }

    @Override
    public void renderOverride(RenderArgs args) {
        callRenderBackground(args);
        super.renderOverride(args);
        callDrawMouseoverTooltip(new DrawMouseoverTooltipArgs(args.drawObjectDM, args.mouseX, args.mouseY));
    }
}
