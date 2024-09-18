package net.pitan76.itemalchemy.client.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.pitan76.mcpitanlib.api.util.CompatIdentifier;

import static net.pitan76.itemalchemy.ItemAlchemy._id;

public class EMCCondenserMK2Screen extends EMCCondenserScreen {

    public EMCCondenserMK2Screen(ScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    public CompatIdentifier getCompatTexture() {
        return _id("textures/gui/emc_condenser_mk2.png");
    }
}
