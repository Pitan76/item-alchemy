package net.pitan76.itemalchemy.client.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.pitan76.itemalchemy.gui.screen.EMCCollectorScreenHandler;
import net.pitan76.mcpitanlib.api.client.CompatInventoryScreen;
import net.pitan76.mcpitanlib.api.client.render.handledscreen.DrawBackgroundArgs;
import net.pitan76.mcpitanlib.api.client.render.handledscreen.DrawForegroundArgs;
import net.pitan76.mcpitanlib.api.util.CompatIdentifier;
import net.pitan76.mcpitanlib.api.util.TextUtil;
import net.pitan76.mcpitanlib.api.util.client.ScreenUtil;

import static net.pitan76.itemalchemy.ItemAlchemy._id;

public class EMCCollectorScreen extends CompatInventoryScreen {
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

    @Override
    public CompatIdentifier getCompatTexture() {
        return _id("textures/gui/emc_collector.png");
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
        super.drawBackgroundOverride(args);
        if (screenHandler == null) return;

        long emc = (screenHandler.storedEMC + screenHandler.tile.storedEMC);
        if (emc > screenHandler.maxEMC) emc = screenHandler.maxEMC;

        callDrawTexture(args.drawObjectDM, getCompatTexture(), x + 93, y + 13, 0, 168, (int) Math.round((double) emc / screenHandler.maxEMC * 46), 14);
    }
}
