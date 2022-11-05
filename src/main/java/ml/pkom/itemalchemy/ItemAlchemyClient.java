package ml.pkom.itemalchemy;

import ml.pkom.itemalchemy.screens.AlchemyTableScreen;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import ml.pkom.mcpitanlibarch.api.nbt.NbtTag;
import ml.pkom.mcpitanlibarch.api.util.TextUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ItemAlchemyClient {
    public static NbtCompound itemAlchemyNbt;

    public static void init() {
        ScreenRegistry.register(ScreenHandlers.ALCHEMY_TABLE, AlchemyTableScreen::new);

        ClientPlayNetworking.registerGlobalReceiver(ItemAlchemy.id("sync_emc"), (client, handler, buf, sender) -> {
            NbtCompound nbt = buf.readNbt();
            itemAlchemyNbt = nbt;

            Player player = new Player(client.player);

            NbtCompound playerNbt = NbtTag.create();
            player.getPlayerEntity().writeCustomDataToNbt(playerNbt);

            playerNbt.put("itemalchemy", nbt);

            player.getPlayerEntity().readCustomDataFromNbt(playerNbt);
        });

        ClientPlayNetworking.registerGlobalReceiver(ItemAlchemy.id("sync_emc_map"), (client, handler, buf, sender) -> {
            NbtCompound nbt = buf.readNbt();

            for (String key : nbt.getKeys()) {
                Map<String, Long> emcMap = new LinkedHashMap<>();
                emcMap.put(key, nbt.getLong(key));
                EMCManager.setMap(emcMap);
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
