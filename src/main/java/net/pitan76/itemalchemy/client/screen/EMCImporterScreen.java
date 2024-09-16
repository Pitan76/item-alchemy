package net.pitan76.itemalchemy.client.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.pitan76.itemalchemy.gui.screen.EMCImporterScreenHandler;
import net.pitan76.mcpitanlib.api.client.CompatInventoryScreen;
import net.pitan76.mcpitanlib.api.client.render.handledscreen.DrawBackgroundArgs;
import net.pitan76.mcpitanlib.api.client.render.handledscreen.DrawForegroundArgs;
import net.pitan76.mcpitanlib.api.util.CompatIdentifier;

import static net.pitan76.itemalchemy.ItemAlchemy._id;

public class EMCImporterScreen extends CompatInventoryScreen {
    public PlayerInventory playerInventory;

    EMCImporterScreenHandler screenHandler;

    public EMCImporterScreen(ScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.playerInventory = inventory;
        setBackgroundWidth(256);
        setBackgroundHeight(234);
        if (handler instanceof EMCImporterScreenHandler)
            screenHandler = (EMCImporterScreenHandler) handler;

    }

    @Override
    public void initOverride() {
        super.initOverride();
    }

    public CompatIdentifier getCompatTexture() {
        return _id("textures/gui/emc_importer.png");
    }

    @Override
    public void drawForegroundOverride(DrawForegroundArgs args) {

        //ScreenUtil.RendererUtil.drawText(textRenderer, args.drawObjectDM, TextUtil.literal(String.format("%,d", emc)), 140, 10, 4210752);
    }

    @Override
    public void drawBackgroundOverride(DrawBackgroundArgs args) {
        super.drawBackgroundOverride(args);
        if (screenHandler == null) return;


        //callDrawTexture(args.drawObjectDM, getCompatTexture(), x + 31, y + 7, 0, 240, (int) Math.round((double) emc / screenHandler.maxEMC * 106), 14);
    }
}
