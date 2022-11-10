package ml.pkom.itemalchemy.screens;

import io.netty.buffer.Unpooled;
import ml.pkom.itemalchemy.ItemAlchemy;
import ml.pkom.itemalchemy.ItemAlchemyClient;
import ml.pkom.mcpitanlibarch.api.client.SimpleHandledScreen;
import ml.pkom.mcpitanlibarch.api.nbt.NbtTag;
import ml.pkom.mcpitanlibarch.api.util.ItemUtil;
import ml.pkom.mcpitanlibarch.api.util.TextUtil;
import ml.pkom.mcpitanlibarch.api.util.client.ScreenUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

;

public class AlchemyTableScreen extends SimpleHandledScreen {
    public PlayerInventory playerInventory;
    public TextFieldWidget searchBox;
    public AlchemyTableScreen(ScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.passEvents = false;
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
                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                buf.writeString(searchBox.getText());
                ClientPlayNetworking.send(ItemAlchemy.id("search"), buf);

                //
                AlchemyTableScreenHandler screenHandler = (AlchemyTableScreenHandler) getScreenHandler();

                // Sort
                NbtTag nbtTag = NbtTag.create();
                client.player.writeCustomDataToNbt(nbtTag);

                if (nbtTag.contains("itemalchemy")) {

                    NbtCompound copy = nbtTag.copy();
                    NbtCompound items = NbtTag.create();

                    NbtCompound itemAlchemyTag = nbtTag.getCompound("itemalchemy");
                    if (itemAlchemyTag.contains("registered_items")) {
                        items = itemAlchemyTag.getCompound("registered_items");
                    }

                    List<String> ids = new ArrayList<>(items.getKeys());
                    for (String id : ids) {
                        if (!id.contains(searchBox.getText()) && !new ItemStack(ItemUtil.fromId(new Identifier(id))).getName().getString().contains(searchBox.getText())) {
                            items.remove(id);
                        }
                    }

                    itemAlchemyTag.put("registered_items", items);
                    nbtTag.put("itemalchemy", itemAlchemyTag);

                    client.player.readCustomDataFromNbt(nbtTag);

                    screenHandler.extractInventory.placeExtractSlots();

                    client.player.readCustomDataFromNbt(copy);
                }
            }
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    public void removed() {
        super.removed();
        if (searchBox.isFocused())
            client.keyboard.setRepeatEvents(false);
    }

    @Override
    public void initOverride() {
        super.initOverride();

        searchBox = new TextFieldWidget(this.textRenderer, x + 85,  y + 5, 60, 9, TextUtil.literal(""));
        searchBox.setDrawsBackground(true);
        searchBox.setFocusUnlocked(true);
        searchBox.setTextFieldFocused(false);
        searchBox.setMaxLength(2048);
        addDrawableChild_compatibility(searchBox);
        
        addDrawableChild_compatibility(new TexturedButtonWidget(x + 113, y + 110, 18, 18, 208, 0, 18, getTexture(), (buttonWidget) -> {
            // クライアントの反映
            if (this.getScreenHandler() instanceof AlchemyTableScreenHandler) {
                AlchemyTableScreenHandler screenHandler = (AlchemyTableScreenHandler) getScreenHandler();
                screenHandler.prevExtractSlots();
            }

            // サーバーに送信
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            NbtCompound nbt = new NbtCompound();
            nbt.putInt("control", 0);
            buf.writeNbt(nbt);
            ClientPlayNetworking.send(ItemAlchemy.id("network"), buf);
        }));

        addDrawableChild_compatibility(new TexturedButtonWidget(x + 171, y + 110, 18, 18, 226, 0, 18, getTexture(), (buttonWidget) -> {
            // クライアントの反映
            if (this.getScreenHandler() instanceof AlchemyTableScreenHandler) {
                AlchemyTableScreenHandler screenHandler = (AlchemyTableScreenHandler) getScreenHandler();
                screenHandler.nextExtractSlots();
            }

            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            NbtCompound nbt = new NbtCompound();
            nbt.putInt("control", 1);
            buf.writeNbt(nbt);
            ClientPlayNetworking.send(ItemAlchemy.id("network"), buf);
        }));
    }

    public Identifier getTexture() {
        return ItemAlchemy.id("textures/guis/alchemy_table.png");
    }

    @Override
    public void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        //super.drawForeground(matrices, mouseX, mouseY);
        this.textRenderer.draw(matrices, getTitle(), (float) this.titleX, (float) this.titleY, 4210752);
        long emc;
        emc = ItemAlchemyClient.getClientPlayerEMC();
        this.textRenderer.draw(matrices, TextUtil.literal("EMC: " + String.format("%,d", emc)), (float) this.titleX, (float) backgroundHeight / 2, 4210752);
    }

    @Override
    public void drawBackgroundOverride(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        ScreenUtil.setBackground(getTexture());
        callDrawTexture(matrices, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        searchBox.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void renderOverride(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.callRenderBackground(matrices);
        super.renderOverride(matrices, mouseX, mouseY, delta);
        this.callDrawMouseoverTooltip(matrices, mouseX, mouseY);
    }
}
