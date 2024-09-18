package net.pitan76.itemalchemy.client.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.pitan76.itemalchemy.gui.screen.EMCBatteryScreenHandler;
import net.pitan76.mcpitanlib.api.client.CompatInventoryScreen;
import net.pitan76.mcpitanlib.api.client.render.handledscreen.DrawBackgroundArgs;
import net.pitan76.mcpitanlib.api.client.render.handledscreen.DrawForegroundArgs;
import net.pitan76.mcpitanlib.api.util.CompatIdentifier;
import net.pitan76.mcpitanlib.api.util.TextUtil;
import net.pitan76.mcpitanlib.api.util.client.ClientUtil;
import net.pitan76.mcpitanlib.api.util.client.ScreenUtil;

import static net.pitan76.itemalchemy.ItemAlchemy._id;

public class EMCBatteryScreen extends CompatInventoryScreen {
    public PlayerInventory playerInventory;

    EMCBatteryScreenHandler screenHandler;

    public EMCBatteryScreen(ScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.playerInventory = inventory;
        setBackgroundWidth(208);
        setBackgroundHeight(166);
        if (handler instanceof EMCBatteryScreenHandler)
            screenHandler = (EMCBatteryScreenHandler) handler;

    }

    @Override
    public void initOverride() {
        if (this.textRenderer == null)
            this.textRenderer = ClientUtil.getTextRenderer();

        setTitleX(backgroundWidth / 2 - ScreenUtil.getWidth(title) / 2);
    }

    @Override
    public CompatIdentifier getCompatTexture() {
        return _id("textures/gui/emc_battery.png");
    }

    @Override
    public void drawForegroundOverride(DrawForegroundArgs args) {
        ScreenUtil.RendererUtil.drawText(textRenderer, args.drawObjectDM, getTitle(), this.titleX, 6, 4210752);
        ScreenUtil.RendererUtil.drawText(textRenderer, args.drawObjectDM, playerInventoryTitle, playerInventoryTitleX + 16, 73, 4210752);

        Text storedEmcText = TextUtil.literal(String.format("%,d", screenHandler.storedEMC));
        ScreenUtil.RendererUtil.drawText(textRenderer, args.drawObjectDM, storedEmcText, 103 - ScreenUtil.getWidth(storedEmcText) / 2, 50, 4210752);

        Text inputChargeItemEmcText = TextUtil.literal("0");
        ScreenUtil.RendererUtil.drawText(textRenderer, args.drawObjectDM, inputChargeItemEmcText, 16, 66, 4210752);

        Text outputChargeItemEmcText = TextUtil.literal("0");
        ScreenUtil.RendererUtil.drawText(textRenderer, args.drawObjectDM, outputChargeItemEmcText, 161, 66, 4210752);
    }

    @Override
    public void drawBackgroundOverride(DrawBackgroundArgs args) {
        super.drawBackgroundOverride(args);
        if (screenHandler == null) return;

        long emc = (screenHandler.storedEMC);
        if (emc > screenHandler.maxEMC) emc = screenHandler.maxEMC;

        callDrawTexture(args.drawObjectDM, getCompatTexture(), x + 51, y + 35, 0, 168, (int) Math.round((double) emc / screenHandler.maxEMC * 106), 14);

        //callDrawTexture(args.drawObjectDM, getCompatTexture(), x + 15, y + 51, 0, 182, (int) Math.round(0.5 * 35), 14);
        //callDrawTexture(args.drawObjectDM, getCompatTexture(), x + 159, y + 51, 0, 182, (int) Math.round(0.5 * 35), 14);

    }
}
