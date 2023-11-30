package ml.pkom.itemalchemy.client.screen;

import io.netty.buffer.Unpooled;
import ml.pkom.itemalchemy.EMCManager;
import ml.pkom.itemalchemy.ItemAlchemy;
import ml.pkom.itemalchemy.api.PlayerRegisteredItemUtil;
import ml.pkom.itemalchemy.gui.screen.AlchemyTableScreenHandler;
import ml.pkom.mcpitanlibarch.api.client.SimpleHandledScreen;
import ml.pkom.mcpitanlibarch.api.client.render.handledscreen.DrawBackgroundArgs;
import ml.pkom.mcpitanlibarch.api.client.render.handledscreen.DrawForegroundArgs;
import ml.pkom.mcpitanlibarch.api.client.render.handledscreen.DrawMouseoverTooltipArgs;
import ml.pkom.mcpitanlibarch.api.client.render.handledscreen.RenderArgs;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import ml.pkom.mcpitanlibarch.api.network.ClientNetworking;
import ml.pkom.mcpitanlibarch.api.network.PacketByteUtil;
import ml.pkom.mcpitanlibarch.api.util.TextUtil;
import ml.pkom.mcpitanlibarch.api.util.client.RenderUtil;
import ml.pkom.mcpitanlibarch.api.util.client.ScreenUtil;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class AlchemyTableScreen extends SimpleHandledScreen {
    public PlayerInventory playerInventory;
    public TextFieldWidget searchBox;
    public AlchemyTableScreen(ScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        ScreenUtil.setPassEvents(this, false);

        this.playerInventory = inventory;
        setBackgroundWidth(208);
        setBackgroundHeight(222);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (searchBox.isFocused()) {
            if (keyCode != 256) {
                return searchBox.keyPressed(keyCode, scanCode, modifiers);
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (searchBox.isFocused()) {
            if (keyCode != 256) {
                NbtCompound translations = new NbtCompound();

                List<Item> items = PlayerRegisteredItemUtil.getItems(new Player(playerInventory.player));
                for (Item item : items) {
                    ItemStack stack = new ItemStack(item);
                    String itemTranslationKey = stack.getTranslationKey();
                    if (I18n.hasTranslation(itemTranslationKey)) {
                        translations.putString(itemTranslationKey, I18n.translate(itemTranslationKey));
                    }
                }


                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

                PacketByteUtil.writeString(buf, searchBox.getText());
                PacketByteUtil.writeNbt(buf, translations);
                ClientNetworking.send(ItemAlchemy.id("search"), buf);

                AlchemyTableScreenHandler screenHandler = (AlchemyTableScreenHandler) getScreenHandler();

                screenHandler.setSearchText(searchBox.getText());
                screenHandler.setTranslations(translations);
                screenHandler.index = 0;
                screenHandler.sortBySearch();
            }
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    public void removed() {
        super.removed();
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
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            NbtCompound nbt = new NbtCompound();
            nbt.putInt("control", 0);
            PacketByteUtil.writeNbt(buf, nbt);
            ClientNetworking.send(ItemAlchemy.id("network"), buf);
        }));

        addDrawableCTBW(ScreenUtil.createTexturedButtonWidget(x + 171, y + 110, 18, 18, 226, 0, 18, getTexture(), (buttonWidget) -> {
            // クライアントの反映
            if (this.getScreenHandler() instanceof AlchemyTableScreenHandler) {
                AlchemyTableScreenHandler screenHandler = (AlchemyTableScreenHandler) getScreenHandler();
                screenHandler.nextExtractSlots();
            }

            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            NbtCompound nbt = new NbtCompound();
            nbt.putInt("control", 1);
            PacketByteUtil.writeNbt(buf, nbt);
            ClientNetworking.send(ItemAlchemy.id("network"), buf);
        }));
    }

    public Identifier getTexture() {
        return ItemAlchemy.id("textures/gui/alchemy_table.png");
    }

    @Override
    public void drawForegroundOverride(DrawForegroundArgs args) {
        ScreenUtil.RendererUtil.drawText(textRenderer, args.drawObjectDM, getTitle(), this.titleX, this.titleY, 4210752);
        long emc = EMCManager.getEmcFromPlayer(new Player(playerInventory.player));
        ScreenUtil.RendererUtil.drawText(textRenderer, args.drawObjectDM, TextUtil.literal("EMC: " + String.format("%,d", emc)), this.titleX, backgroundHeight / 2, 4210752);
    }

    @Override
    public void drawBackgroundOverride(DrawBackgroundArgs args) {
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        RenderUtil.setShaderToPositionTexProgram();
        RenderUtil.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        callDrawTexture(args.drawObjectDM, getTexture(), x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        ScreenUtil.TextFieldUtil.render(searchBox, new RenderArgs(args.drawObjectDM, args.mouseX, args.mouseY, args.delta));
    }

    @Override
    public void renderOverride(RenderArgs args) {
        callRenderBackground(args);
        super.renderOverride(args);
        callDrawMouseoverTooltip(new DrawMouseoverTooltipArgs(args.drawObjectDM, args.mouseX, args.mouseY));
    }
}
