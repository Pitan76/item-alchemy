package ml.pkom.itemalchemy.client.screens;

import ml.pkom.itemalchemy.ItemAlchemy;
import ml.pkom.itemalchemy.gui.screens.EMCCollectorScreenHandler;
import ml.pkom.mcpitanlibarch.api.client.SimpleHandledScreen;
import ml.pkom.mcpitanlibarch.api.util.TextUtil;
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
        return ItemAlchemy.id("textures/guis/emc_collector.png");
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
    public void drawBackgroundOverride(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        long emc = (screenHandler.storedEMC + screenHandler.tile.storedEMC);
        if (emc > screenHandler.maxEMC) emc = screenHandler.maxEMC;

        ScreenUtil.setBackground(getTexture());
        callDrawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
        if (screenHandler != null)
            callDrawTexture(matrices, x + 93, y + 13, 0, 168, (int) Math.round((double) emc / screenHandler.maxEMC * 46), 14);
    }

    @Override
    public void renderOverride(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        callRenderBackground(matrices);
        super.renderOverride(matrices, mouseX, mouseY, delta);
        callDrawMouseoverTooltip(matrices, mouseX, mouseY);
    }
}
