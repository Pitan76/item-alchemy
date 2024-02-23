package net.pitan76.itemalchemy.client.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.pitan76.itemalchemy.ItemAlchemy;
import net.pitan76.mcpitanlib.api.client.SimpleHandledScreen;
import net.pitan76.mcpitanlib.api.client.render.handledscreen.DrawBackgroundArgs;
import net.pitan76.mcpitanlib.api.client.render.handledscreen.DrawForegroundArgs;
import net.pitan76.mcpitanlib.api.client.render.handledscreen.DrawMouseoverTooltipArgs;
import net.pitan76.mcpitanlib.api.client.render.handledscreen.RenderArgs;
import net.pitan76.mcpitanlib.api.util.TextUtil;
import net.pitan76.mcpitanlib.api.util.client.RenderUtil;

public class AlchemyChestScreen extends SimpleHandledScreen {
    public PlayerInventory playerInventory;

    public AlchemyChestScreen(ScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, TextUtil.literal(""));

        this.playerInventory = inventory;
        setBackgroundWidth(256);
        setBackgroundHeight(234);
    }

    @Override
    public void initOverride() {
        super.initOverride();

    }

    public Identifier getTexture() {
        return ItemAlchemy.id("textures/gui/alchemy_chest.png");
    }

    @Override
    public void drawBackgroundOverride(DrawBackgroundArgs args) {
        RenderUtil.setShaderToPositionTexProgram();
        RenderUtil.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        callDrawTexture(args.drawObjectDM, getTexture(), x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    protected void drawForegroundOverride(DrawForegroundArgs args) {
    }

    @Override
    public void renderOverride(RenderArgs args) {
        callRenderBackground(args);
        super.renderOverride(args);
        callDrawMouseoverTooltip(new DrawMouseoverTooltipArgs(args.drawObjectDM, args.mouseX, args.mouseY));
    }
}
