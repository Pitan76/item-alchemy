package ml.pkom.itemalchemy;

import dev.architectury.event.events.client.ClientTickEvent;
import ml.pkom.itemalchemy.client.renderer.BlockRenderer;
import ml.pkom.itemalchemy.client.screen.AlchemyChestScreen;
import ml.pkom.itemalchemy.client.screen.AlchemyTableScreen;
import ml.pkom.itemalchemy.client.screen.EMCCollectorScreen;
import ml.pkom.itemalchemy.client.screen.EMCCondenserScreen;
import ml.pkom.itemalchemy.gui.screen.EMCCollectorScreenHandler;
import ml.pkom.itemalchemy.gui.screen.EMCCondenserScreenHandler;
import ml.pkom.itemalchemy.gui.screen.ScreenHandlers;
import ml.pkom.mcpitanlibarch.api.client.registry.ArchRegistryClient;
import ml.pkom.mcpitanlibarch.api.client.registry.KeybindingRegistry;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import ml.pkom.mcpitanlibarch.api.network.ClientNetworking;
import ml.pkom.mcpitanlibarch.api.util.TextUtil;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.*;

public class ItemAlchemyClient {
    public static NbtCompound itemAlchemyNbt;

    public static void init() {
        // Mixinを使った場合、関数が1.20から変更されているために使えないのでこちらで対処しておく
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> lines.addAll(getEmcText(stack)));

        ArchRegistryClient.registerScreen(ScreenHandlers.ALCHEMY_TABLE, AlchemyTableScreen::new);
        ArchRegistryClient.registerScreen(ScreenHandlers.EMC_COLLECTOR, EMCCollectorScreen::new);
        ArchRegistryClient.registerScreen(ScreenHandlers.EMC_CONDENSER, EMCCondenserScreen::new);
        ArchRegistryClient.registerScreen(ScreenHandlers.ALCHEMY_CHEST, AlchemyChestScreen::new);

        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register(new BlockRenderer());

        ClientNetworking.registerReceiver(ItemAlchemy.id("sync_emc"), (client, p, buf) -> {
            NbtCompound nbt = buf.readNbt();
            itemAlchemyNbt = nbt;

            Player player = new Player(p);

            NbtCompound playerNbt = EMCManager.writePlayerNbt(player);

            playerNbt.put("itemalchemy", nbt);

            EMCManager.readPlayerNbt(player, playerNbt);
        });

        ClientNetworking.registerReceiver(ItemAlchemy.id("sync_emc_map"), (client, p, buf) -> {
            NbtCompound nbt = buf.readNbt();
            if (nbt == null) return;

            Map<String, Long> emcMap = new LinkedHashMap<>();
            for (String key : nbt.getKeys()) {
                emcMap.put(key, nbt.getLong(key));
                //System.out.println(key + "=" + nbt.getLong(key));
            }
            EMCManager.setMap(emcMap);
        });

        ClientNetworking.registerReceiver(ItemAlchemy.id("itemalchemy_emc_collector"), (client, p, buf) -> {
            long storedEMC = buf.readLong();
            if (Objects.requireNonNull(p).currentScreenHandler instanceof EMCCollectorScreenHandler) {
                EMCCollectorScreenHandler screenHandler = (EMCCollectorScreenHandler) p.currentScreenHandler;
                screenHandler.storedEMC = storedEMC;
            }
        });

        ClientNetworking.registerReceiver(ItemAlchemy.id("itemalchemy_emc_condenser"), (client, p, buf) -> {
            long storedEMC = buf.readLong();
            long maxEMC = buf.readLong();
            if (Objects.requireNonNull(p).currentScreenHandler instanceof EMCCondenserScreenHandler) {
                EMCCondenserScreenHandler screenHandler = (EMCCondenserScreenHandler) p.currentScreenHandler;
                screenHandler.storedEMC = storedEMC;
                screenHandler.maxEMC = maxEMC;
            }
        });

        KeybindingRegistry.registerOnLevelWithNetwork(new KeyBinding("key.itemalchemy.charge", GLFW.GLFW_KEY_V, "category.itemalchemy.tool"), ItemAlchemy.id("tool_charge"));
    }

    public static long getClientPlayerEMC() {
        long emc = 0;
        if (ItemAlchemyClient.itemAlchemyNbt != null) {
            if (ItemAlchemyClient.itemAlchemyNbt.contains("emc")) {
                emc = ItemAlchemyClient.itemAlchemyNbt.getLong("emc");
            }
        }
        return emc;
    }

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
