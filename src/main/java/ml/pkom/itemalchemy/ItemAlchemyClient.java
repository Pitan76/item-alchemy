package ml.pkom.itemalchemy;

import ml.pkom.itemalchemy.client.screens.AlchemyChestScreen;
import ml.pkom.itemalchemy.client.screens.AlchemyTableScreen;
import ml.pkom.itemalchemy.client.screens.EMCCollectorScreen;
import ml.pkom.itemalchemy.client.screens.EMCCondenserScreen;
import ml.pkom.itemalchemy.gui.screens.EMCCollectorScreenHandler;
import ml.pkom.itemalchemy.gui.screens.EMCCondenserScreenHandler;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import ml.pkom.mcpitanlibarch.api.util.TextUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import java.util.*;

public class ItemAlchemyClient {
    public static NbtCompound itemAlchemyNbt;

    public static void init() {
        ScreenRegistry.register(ScreenHandlers.ALCHEMY_TABLE, AlchemyTableScreen::new);
        ScreenRegistry.register(ScreenHandlers.EMC_COLLECTOR, EMCCollectorScreen::new);
        ScreenRegistry.register(ScreenHandlers.EMC_CONDENSER, EMCCondenserScreen::new);
        ScreenRegistry.register(ScreenHandlers.ALCHEMY_CHEST, AlchemyChestScreen::new);

        ClientPlayNetworking.registerGlobalReceiver(ItemAlchemy.id("sync_emc"), (client, handler, buf, sender) -> {
            NbtCompound nbt = buf.readNbt();
            itemAlchemyNbt = nbt;

            Player player = new Player(client.player);

            NbtCompound playerNbt = EMCManager.writePlayerNbt(player);

            playerNbt.put("itemalchemy", nbt);

            EMCManager.readPlayerNbt(player, playerNbt);
        });

        ClientPlayNetworking.registerGlobalReceiver(ItemAlchemy.id("sync_emc_map"), (client, handler, buf, sender) -> {
            NbtCompound nbt = buf.readNbt();
            if (nbt == null) return;

            Map<String, Long> emcMap = new LinkedHashMap<>();
            for (String key : nbt.getKeys()) {
                emcMap.put(key, nbt.getLong(key));
                //System.out.println(key + "=" + nbt.getLong(key));
            }
            EMCManager.setMap(emcMap);
        });

        ClientPlayNetworking.registerGlobalReceiver(ItemAlchemy.id("itemalchemy_emc_collector"), (client, handler, buf, sender) -> {
            long storedEMC = buf.readLong();
            if (Objects.requireNonNull(client.player).currentScreenHandler instanceof EMCCollectorScreenHandler) {
                EMCCollectorScreenHandler screenHandler = (EMCCollectorScreenHandler) client.player.currentScreenHandler;
                screenHandler.storedEMC = storedEMC;
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(ItemAlchemy.id("itemalchemy_emc_condenser"), (client, handler, buf, sender) -> {
            long storedEMC = buf.readLong();
            long maxEMC = buf.readLong();
            if (Objects.requireNonNull(client.player).currentScreenHandler instanceof EMCCondenserScreenHandler) {
                EMCCondenserScreenHandler screenHandler = (EMCCondenserScreenHandler) client.player.currentScreenHandler;
                screenHandler.storedEMC = storedEMC;
                screenHandler.maxEMC = maxEMC;
            }
        });
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
            //System.out.println(e.getMessage());
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
