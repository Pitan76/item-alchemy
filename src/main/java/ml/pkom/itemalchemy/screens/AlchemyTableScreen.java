package ml.pkom.itemalchemy.screens;

import io.netty.buffer.Unpooled;
import ml.pkom.itemalchemy.ItemAlchemy;
import ml.pkom.itemalchemy.ItemAlchemyClient;
import ml.pkom.mcpitanlibarch.api.client.SimpleHandledScreen;
import ml.pkom.mcpitanlibarch.api.util.TextUtil;
import ml.pkom.mcpitanlibarch.api.util.client.ScreenUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

;

public class AlchemyTableScreen extends SimpleHandledScreen {
    public PlayerInventory playerInventory;
    public AlchemyTableScreen(ScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 208;
        this.backgroundHeight = 222;
        this.passEvents = false;
        this.playerInventory = inventory;
    }

    @Override
    protected void init() {
        super.init();
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
        drawTexture(matrices, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.callRenderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.callDrawMouseoverTooltip(matrices, mouseX, mouseY);
    }
}
