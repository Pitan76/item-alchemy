package net.pitan76.itemalchemy;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.pitan76.itemalchemy.block.Blocks;
import net.pitan76.itemalchemy.client.renderer.BlockRenderer;
import net.pitan76.itemalchemy.client.screen.*;
import net.pitan76.itemalchemy.config.ItemAlchemyConfig;
import net.pitan76.itemalchemy.gui.screen.EMCBatteryScreenHandler;
import net.pitan76.itemalchemy.gui.screen.EMCCollectorScreenHandler;
import net.pitan76.itemalchemy.gui.screen.EMCCondenserScreenHandler;
import net.pitan76.itemalchemy.gui.screen.ScreenHandlers;
import net.pitan76.mcpitanlib.api.client.event.ItemTooltipRegistry;
import net.pitan76.mcpitanlib.api.client.event.WorldRenderRegistry;
import net.pitan76.mcpitanlib.api.client.option.KeyCodes;
import net.pitan76.mcpitanlib.api.client.registry.CompatRegistryClient;
import net.pitan76.mcpitanlib.api.client.registry.v2.KeybindingRegistry;
import net.pitan76.mcpitanlib.api.network.PacketByteUtil;
import net.pitan76.mcpitanlib.api.network.v2.ClientNetworking;
import net.pitan76.mcpitanlib.api.util.ItemStackUtil;
import net.pitan76.mcpitanlib.api.util.TextUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.pitan76.itemalchemy.ItemAlchemy._id;

public class ItemAlchemyClient {
    public static NbtCompound itemAlchemyNbt;

    public static void init() {
        ItemAlchemyConfig.initOnce();

        // Mixinを使った場合、関数が1.20から変更されているために使えないのでこちらで対処しておく
        if (ItemAlchemyConfig.isShowEmcInTooltip())
            ItemTooltipRegistry.registerItemTooltip((context) -> context.addTooltip(getEmcText(context.getStack())));

        CompatRegistryClient.registerScreen(ItemAlchemy.MOD_ID, ScreenHandlers.ALCHEMY_TABLE, AlchemyTableScreen::new);
        CompatRegistryClient.registerScreen(ItemAlchemy.MOD_ID, ScreenHandlers.EMC_COLLECTOR, EMCCollectorScreen::new);
        CompatRegistryClient.registerScreen(ItemAlchemy.MOD_ID, ScreenHandlers.EMC_CONDENSER, EMCCondenserScreen::new);
        CompatRegistryClient.registerScreen(ItemAlchemy.MOD_ID, ScreenHandlers.EMC_CONDENSER_MK2, EMCCondenserMK2Screen::new);
        CompatRegistryClient.registerScreen(ItemAlchemy.MOD_ID, ScreenHandlers.ALCHEMY_CHEST, AlchemyChestScreen::new);
        CompatRegistryClient.registerScreen(ItemAlchemy.MOD_ID, ScreenHandlers.EMC_IMPORTER, EMCImporterScreen::new);
        CompatRegistryClient.registerScreen(ItemAlchemy.MOD_ID, ScreenHandlers.EMC_EXPORTER, EMCExporterScreen::new);
        CompatRegistryClient.registerScreen(ItemAlchemy.MOD_ID, ScreenHandlers.EMC_BATTERY, EMCBatteryScreen::new);

        CompatRegistryClient.registerCutoutBlock(Blocks.EMC_CABLE.getOrNull());

        WorldRenderRegistry.registerWorldRenderBeforeBlockOutline(new BlockRenderer());

        ClientNetworking.registerReceiver(_id("sync_emc"), (e) -> itemAlchemyNbt = PacketByteUtil.readNbt(e.buf));

        ClientNetworking.registerReceiver(_id("sync_emc_map"), (e) -> {
            Map<String, Long> map = PacketByteUtil.readMap(e.buf, PacketByteUtil::readString, PacketByteBuf::readLong);
            if (map == null) return;

            EMCManager.setMap(map);
        });

        ClientNetworking.registerReceiver(_id("itemalchemy_emc_collector"), (e) -> {
            long storedEMC = PacketByteUtil.readLong(e.buf);
            if (e.player.getCurrentScreenHandler() instanceof EMCCollectorScreenHandler) {
                EMCCollectorScreenHandler screenHandler = (EMCCollectorScreenHandler) e.player.getCurrentScreenHandler();
                screenHandler.storedEMC = storedEMC;
            }
        });

        ClientNetworking.registerReceiver(_id("itemalchemy_emc_battery"), (e) -> {
            long storedEMC = PacketByteUtil.readLong(e.buf);
            if (e.player.getCurrentScreenHandler() instanceof EMCBatteryScreenHandler) {
                EMCBatteryScreenHandler screenHandler = (EMCBatteryScreenHandler) e.player.getCurrentScreenHandler();
                screenHandler.storedEMC = storedEMC;
            }
        });

        ClientNetworking.registerReceiver(_id("itemalchemy_emc_condenser"), (e) -> {
            long storedEMC = PacketByteUtil.readLong(e.buf);
            long maxEMC = PacketByteUtil.readLong(e.buf);

            if (e.player.getCurrentScreenHandler() instanceof EMCCondenserScreenHandler) {
                EMCCondenserScreenHandler screenHandler = (EMCCondenserScreenHandler) e.player.getCurrentScreenHandler();
                screenHandler.storedEMC = storedEMC;
                screenHandler.maxEMC = maxEMC;
            }
        });

        KeybindingRegistry.registerOnLevelWithNetwork("key.itemalchemy.charge", KeyCodes.KEY_V, "category.itemalchemy.all", _id("tool_charge"));
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

        if (emc == 0) return list;

        list.add(TextUtil.literal("§eEMC: §r" + String.format("%,d", emc)));

        if (ItemStackUtil.getCount(stack) > 1)
            list.add(TextUtil.literal("§eStack EMC: §r" + String.format("%,d", emc * ItemStackUtil.getCount(stack))));

        return list;
    }
}
