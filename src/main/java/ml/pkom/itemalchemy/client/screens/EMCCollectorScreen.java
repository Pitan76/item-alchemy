package ml.pkom.itemalchemy.client.screens;

import ml.pkom.itemalchemy.ItemAlchemy;
import ml.pkom.mcpitanlibarch.api.client.SimpleHandledScreen;
import ml.pkom.mcpitanlibarch.api.util.client.ScreenUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

;

public class EMCCollectorScreen extends SimpleHandledScreen {
    public PlayerInventory playerInventory;

    public EMCCollectorScreen(ScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.playerInventory = inventory;
        setBackgroundWidth(208);
        setBackgroundHeight(166);
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
        textRenderer.draw(matrices, getTitle(), (float) titleX, (float) titleY, 4210752);
    }

    @Override
    public void drawBackgroundOverride(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        ScreenUtil.setBackground(getTexture());
        callDrawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void renderOverride(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        callRenderBackground(matrices);
        super.renderOverride(matrices, mouseX, mouseY, delta);
        callDrawMouseoverTooltip(matrices, mouseX, mouseY);
    }
}
