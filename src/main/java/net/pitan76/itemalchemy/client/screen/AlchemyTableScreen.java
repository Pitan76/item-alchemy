package net.pitan76.itemalchemy.client.screen;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.itemalchemy.api.PlayerRegisteredItemUtil;
import net.pitan76.itemalchemy.gui.screen.AlchemyTableScreenHandler;
import net.pitan76.mcpitanlib.api.client.CompatInventoryScreen;
import net.pitan76.mcpitanlib.api.client.render.handledscreen.DrawBackgroundArgs;
import net.pitan76.mcpitanlib.api.client.render.handledscreen.DrawForegroundArgs;
import net.pitan76.mcpitanlib.api.client.render.handledscreen.KeyEventArgs;
import net.pitan76.mcpitanlib.api.client.render.handledscreen.RenderArgs;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.network.PacketByteUtil;
import net.pitan76.mcpitanlib.api.network.v2.ClientNetworking;
import net.pitan76.mcpitanlib.api.util.CompatIdentifier;
import net.pitan76.mcpitanlib.api.util.ItemStackUtil;
import net.pitan76.mcpitanlib.api.util.NbtUtil;
import net.pitan76.mcpitanlib.api.util.TextUtil;
import net.pitan76.mcpitanlib.api.util.client.ScreenUtil;

import java.util.List;

import static net.pitan76.itemalchemy.ItemAlchemy._id;

public class AlchemyTableScreen extends CompatInventoryScreen {
    public PlayerInventory playerInventory;
    public TextFieldWidget searchBox;
    public AlchemyTableScreen(ScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        ScreenUtil.setPassEvents(this, false);

        this.playerInventory = inventory;
        setBackgroundWidth(208);
        setBackgroundHeight(222);
    }

    @Override
    public boolean keyPressed(KeyEventArgs args) {
        if (searchBox.isFocused()) {
            if (args.keyCode != 256) {
                return searchBox.keyPressed(args.keyCode, args.scanCode, args.modifiers);
            }
        }
        return super.keyPressed(args);
    }

    @Override
    public boolean keyReleased(KeyEventArgs args) {
        if (searchBox.isFocused()) {
            if (args.keyCode != 256) {
                NbtCompound translations = NbtUtil.create();

                List<Item> items = PlayerRegisteredItemUtil.getItems(new Player(playerInventory.player));
                for (Item item : items) {
                    ItemStack stack = ItemStackUtil.create(item);
                    String itemTranslationKey = stack.getTranslationKey();
                    if (I18n.hasTranslation(itemTranslationKey)) {
                        translations.putString(itemTranslationKey, I18n.translate(itemTranslationKey));
                    }
                }

                PacketByteBuf buf = PacketByteUtil.create();

                PacketByteUtil.writeString(buf, searchBox.getText());
                PacketByteUtil.writeNbt(buf, translations);
                ClientNetworking.send(_id("search"), buf);

                AlchemyTableScreenHandler screenHandler = (AlchemyTableScreenHandler) getScreenHandler();

                screenHandler.setSearchText(searchBox.getText());
                screenHandler.setTranslations(translations);
                screenHandler.index = 0;
                screenHandler.sortBySearch();
            }
        }
        return super.keyReleased(args);
    }

    public void removedOverride() {
        super.removedOverride();
        if (searchBox.isFocused())
            ScreenUtil.setRepeatEvents(false);
    }

    @Override
    public void initOverride() {
        super.initOverride();

        searchBox = new TextFieldWidget(this.textRenderer, x + 85,  y + 5, 60, 9, TextUtil.literal(""));
        searchBox.setDrawsBackground(true);
        searchBox.setFocusUnlocked(true);
        ScreenUtil.TextFieldUtil.setFocused(searchBox, false);
        searchBox.setMaxLength(2048);
        searchBox.setText("");
        addDrawableChild_compatibility(searchBox);

        addDrawableCTBW(ScreenUtil.createTexturedButtonWidget(x + 113, y + 110, 18, 18, 208, 0, 18, getTexture(), (buttonWidget) -> {
            // クライアントの反映
            if (this.getScreenHandler() instanceof AlchemyTableScreenHandler) {
                AlchemyTableScreenHandler screenHandler = (AlchemyTableScreenHandler) getScreenHandler();
                screenHandler.prevExtractSlots();
            }

            // サーバーに送信
            PacketByteBuf buf = PacketByteUtil.create();
            NbtCompound nbt = NbtUtil.create();
            NbtUtil.set(nbt, "control", 0);
            PacketByteUtil.writeNbt(buf, nbt);
            ClientNetworking.send(_id("network"), buf);
        }));

        addDrawableCTBW(ScreenUtil.createTexturedButtonWidget(x + 171, y + 110, 18, 18, 226, 0, 18, getTexture(), (buttonWidget) -> {
            // クライアントの反映
            if (this.getScreenHandler() instanceof AlchemyTableScreenHandler) {
                AlchemyTableScreenHandler screenHandler = (AlchemyTableScreenHandler) getScreenHandler();
                screenHandler.nextExtractSlots();
            }

            PacketByteBuf buf = PacketByteUtil.create();
            NbtCompound nbt = NbtUtil.create();
            NbtUtil.set(nbt, "control", 1);
            PacketByteUtil.writeNbt(buf, nbt);
            ClientNetworking.send(_id("network"), buf);
        }));
    }

    @Override
    public CompatIdentifier getCompatTexture() {
        return _id("textures/gui/alchemy_table.png");
    }

    @Override
    public void drawForegroundOverride(DrawForegroundArgs args) {
        ScreenUtil.RendererUtil.drawText(textRenderer, args.drawObjectDM, getTitle(), this.titleX, this.titleY, 4210752);
        long emc = EMCManager.getEmcFromPlayer(new Player(playerInventory.player));
        ScreenUtil.RendererUtil.drawText(textRenderer, args.drawObjectDM, TextUtil.literal("EMC: " + String.format("%,d", emc)), this.titleX, backgroundHeight / 2, 4210752);
    }

    @Override
    public void drawBackgroundOverride(DrawBackgroundArgs args) {
        super.drawBackgroundOverride(args);
        ScreenUtil.TextFieldUtil.render(searchBox, new RenderArgs(args.drawObjectDM, args.mouseX, args.mouseY, args.delta));
    }
}
