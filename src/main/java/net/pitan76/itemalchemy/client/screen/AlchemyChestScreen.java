package net.pitan76.itemalchemy.client.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.pitan76.mcpitanlib.api.client.CompatInventoryScreen;
import net.pitan76.mcpitanlib.api.client.render.handledscreen.DrawBackgroundArgs;
import net.pitan76.mcpitanlib.api.client.render.handledscreen.DrawForegroundArgs;
import net.pitan76.mcpitanlib.api.util.CompatIdentifier;
import net.pitan76.mcpitanlib.api.util.TextUtil;

import static net.pitan76.itemalchemy.ItemAlchemy._id;

public class AlchemyChestScreen extends CompatInventoryScreen {
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

    @Override
    public CompatIdentifier getCompatTexture() {
        return _id("textures/gui/alchemy_chest.png");
    }

    @Override
    public void drawBackgroundOverride(DrawBackgroundArgs args) {
        super.drawBackgroundOverride(args);
    }

    @Override
    public void drawForegroundOverride(DrawForegroundArgs args) {

    }
}
