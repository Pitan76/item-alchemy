package net.pitan76.itemalchemy.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.pitan76.itemalchemy.gui.screen.AlchemyTableScreenHandler;
import net.pitan76.mcpitanlib.api.util.TextUtil;

public class AlchemyTableScreenHandlerFactory implements NamedScreenHandlerFactory {
    private static final Text TITLE = TextUtil.translatable("container.itemalchemy.alchemy_table");

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new AlchemyTableScreenHandler(syncId, inv);
    }

    @Override
    public Text getDisplayName() {
        return TITLE;
    }
}
