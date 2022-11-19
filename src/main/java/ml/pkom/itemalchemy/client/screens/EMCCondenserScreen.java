package ml.pkom.itemalchemy.client.screens;

import ml.pkom.itemalchemy.ItemAlchemy;
import ml.pkom.itemalchemy.gui.screens.EMCCondenserScreenHandler;
import ml.pkom.mcpitanlibarch.api.client.SimpleHandledScreen;
import ml.pkom.mcpitanlibarch.api.util.TextUtil;
import ml.pkom.mcpitanlibarch.api.util.client.ScreenUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

;

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
        return ItemAlchemy.id("textures/guis/emc_condenser.png");
    }

    @Override
    public void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        long emc = (screenHandler.storedEMC + screenHandler.tile.storedEMC);
        if (emc > screenHandler.maxEMC) emc = screenHandler.maxEMC;

        if (screenHandler != null)
            textRenderer.draw(matrices, TextUtil.literal("" + String.format("%,d", emc) ), 140, 10, 4210752);
    }

    @Override
    public void drawBackgroundOverride(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        long emc = (screenHandler.storedEMC + screenHandler.tile.storedEMC);
        if (emc > screenHandler.maxEMC) emc = screenHandler.maxEMC;

        ScreenUtil.setBackground(getTexture());
        callDrawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
        if (screenHandler != null)
            callDrawTexture(matrices, x + 31, y + 7, 0, 240, (int) Math.round((double) emc / screenHandler.maxEMC * 106), 14);
    }

    @Override
    public void renderOverride(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        callRenderBackground(matrices);
        super.renderOverride(matrices, mouseX, mouseY, delta);
        callDrawMouseoverTooltip(matrices, mouseX, mouseY);
    }
}
