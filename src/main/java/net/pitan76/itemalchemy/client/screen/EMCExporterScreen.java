package net.pitan76.itemalchemy.client.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.pitan76.itemalchemy.gui.screen.EMCExporterScreenHandler;
import net.pitan76.mcpitanlib.api.client.CompatInventoryScreen;
import net.pitan76.mcpitanlib.api.client.render.handledscreen.DrawBackgroundArgs;
import net.pitan76.mcpitanlib.api.client.render.handledscreen.DrawForegroundArgs;
import net.pitan76.mcpitanlib.api.util.CompatIdentifier;
import net.pitan76.mcpitanlib.api.util.TextUtil;
import net.pitan76.mcpitanlib.api.util.client.ClientUtil;
import net.pitan76.mcpitanlib.api.util.client.ScreenUtil;

import static net.pitan76.itemalchemy.ItemAlchemy._id;

public class EMCExporterScreen extends CompatInventoryScreen {
    public PlayerInventory playerInventory;

    EMCExporterScreenHandler screenHandler;

    public EMCExporterScreen(ScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.playerInventory = inventory;
        setBackgroundWidth(176);
        setBackgroundHeight(184);
        if (handler instanceof EMCExporterScreenHandler)
            screenHandler = (EMCExporterScreenHandler) handler;

    }

    @Override
    public void initOverride() {
        if (this.textRenderer == null)
            this.textRenderer = ClientUtil.getTextRenderer();

        setTitleX(backgroundWidth / 2 - textRenderer.getWidth(title) / 2);
    }

    @Override
    public CompatIdentifier getCompatTexture() {
        return _id("textures/gui/3x3.png");
    }

    @Override
    public void drawForegroundOverride(DrawForegroundArgs args) {
        ScreenUtil.RendererUtil.drawText(textRenderer, args.drawObjectDM, getTitle(), this.titleX, 10, 4210752);
        ScreenUtil.RendererUtil.drawText(textRenderer, args.drawObjectDM, playerInventoryTitle, playerInventoryTitleX, 90, 4210752);

        if (screenHandler.ownerName != null && !screenHandler.ownerName.isEmpty()) {
            Text owner = TextUtil.translatable("text.itemalchemy.owner", screenHandler.ownerName);
            ScreenUtil.RendererUtil.drawText(textRenderer, args.drawObjectDM, owner, backgroundWidth - 10 - ScreenUtil.getWidth(owner), 90, 4210752);
        }
        //ScreenUtil.RendererUtil.drawText(textRenderer, args.drawObjectDM, TextUtil.literal(String.format("%,d", emc)), 140, 10, 4210752);
    }

    @Override
    public void drawBackgroundOverride(DrawBackgroundArgs args) {
        super.drawBackgroundOverride(args);
        if (screenHandler == null) return;

        //callDrawTexture(args.drawObjectDM, getCompatTexture(), x + 31, y + 7, 0, 240, (int) Math.round((double) emc / screenHandler.maxEMC * 106), 14);
    }
}
