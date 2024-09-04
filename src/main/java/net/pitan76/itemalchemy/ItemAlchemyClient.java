package net.pitan76.itemalchemy;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.pitan76.itemalchemy.client.renderer.BlockRenderer;
import net.pitan76.itemalchemy.client.screen.AlchemyChestScreen;
import net.pitan76.itemalchemy.client.screen.AlchemyTableScreen;
import net.pitan76.itemalchemy.client.screen.EMCCollectorScreen;
import net.pitan76.itemalchemy.client.screen.EMCCondenserScreen;
import net.pitan76.itemalchemy.gui.screen.EMCCollectorScreenHandler;
import net.pitan76.itemalchemy.gui.screen.EMCCondenserScreenHandler;
import net.pitan76.itemalchemy.gui.screen.ScreenHandlers;
import net.pitan76.mcpitanlib.api.client.event.ItemTooltipRegistry;
import net.pitan76.mcpitanlib.api.client.registry.CompatRegistryClient;
import net.pitan76.mcpitanlib.api.client.registry.KeybindingRegistry;
import net.pitan76.mcpitanlib.api.network.ClientNetworking;
import net.pitan76.mcpitanlib.api.network.PacketByteUtil;
import net.pitan76.mcpitanlib.api.util.TextUtil;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static net.pitan76.itemalchemy.ItemAlchemy._id;

public class ItemAlchemyClient {
    public static NbtCompound itemAlchemyNbt;

    public static void init() {
        // Mixinを使った場合、関数が1.20から変更されているために使えないのでこちらで対処しておく
        ItemTooltipRegistry.registerItemTooltip((context) -> context.addTooltip(getEmcText(context.getStack())));

        CompatRegistryClient.registerScreen(ItemAlchemy.MOD_ID, ScreenHandlers.ALCHEMY_TABLE, AlchemyTableScreen::new);
        CompatRegistryClient.registerScreen(ItemAlchemy.MOD_ID, ScreenHandlers.EMC_COLLECTOR, EMCCollectorScreen::new);
        CompatRegistryClient.registerScreen(ItemAlchemy.MOD_ID, ScreenHandlers.EMC_CONDENSER, EMCCondenserScreen::new);
        CompatRegistryClient.registerScreen(ItemAlchemy.MOD_ID, ScreenHandlers.ALCHEMY_CHEST, AlchemyChestScreen::new);

        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register(new BlockRenderer());

        ClientNetworking.registerReceiver(_id("sync_emc").toMinecraft(), (client, p, buf) -> {
            itemAlchemyNbt = PacketByteUtil.readNbt(buf);
        });

        ClientNetworking.registerReceiver(_id("sync_emc_map").toMinecraft(), (client, p, buf) -> {
            Map<String, Long> map = PacketByteUtil.readMap(buf, PacketByteUtil::readString, PacketByteBuf::readLong);
            if (map == null) return;

            EMCManager.setMap(map);
        });

        ClientNetworking.registerReceiver(_id("itemalchemy_emc_collector").toMinecraft(), (client, p, buf) -> {
            long storedEMC = PacketByteUtil.readLong(buf);
            if (Objects.requireNonNull(p).currentScreenHandler instanceof EMCCollectorScreenHandler) {
                EMCCollectorScreenHandler screenHandler = (EMCCollectorScreenHandler) p.currentScreenHandler;
                screenHandler.storedEMC = storedEMC;
            }
        });

        ClientNetworking.registerReceiver(_id("itemalchemy_emc_condenser").toMinecraft(), (client, p, buf) -> {
            long storedEMC = PacketByteUtil.readLong(buf);
            long maxEMC = PacketByteUtil.readLong(buf);

            if (Objects.requireNonNull(p).currentScreenHandler instanceof EMCCondenserScreenHandler) {
                EMCCondenserScreenHandler screenHandler = (EMCCondenserScreenHandler) p.currentScreenHandler;
                screenHandler.storedEMC = storedEMC;
                screenHandler.maxEMC = maxEMC;
            }
        });

        KeybindingRegistry.registerOnLevelWithNetwork(new KeyBinding("key.itemalchemy.charge", GLFW.GLFW_KEY_V, "category.itemalchemy.all"), _id("tool_charge").toMinecraft());
    }

    // display emc to the item's tooltip
    public static List<Text> getEmcText(ItemStack stack) {
        List<Text> list = new ArrayList<>();
        long emc;
        try {
            emc = EMCManager.getMap().get(EMCManager.itemToId(stack.getItem()));
        } catch (Exception e) {
            emc = 0;
        }
        if (emc == 0) {
            return list;
        }
        list.add(TextUtil.literal("§eEMC: §r" + String.format("%,d", emc)));

        if (stack.getCount() > 1) {
            list.add(TextUtil.literal("§eStack EMC: §r" + String.format("%,d", emc * stack.getCount())));
        }
        return list;
    }
}
