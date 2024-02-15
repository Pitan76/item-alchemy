package net.pitan76.itemalchemy.api;

import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.itemalchemy.emcs.EMCDef;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.tag.TagKey;
import net.pitan76.mcpitanlib.api.util.ItemUtil;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class EMCUtil {

    public static class ITEM {
        public static void setEMC(Item item, long amount) {
            EMCManager.add(item, amount);
        }

        public static void setEMC(String id, long amount) {
            EMCManager.add(id, amount);
        }

        public static void setEMC(TagKey<Item> tagKey, long amount) {
            EMCManager.add2(tagKey, amount);
        }


        public static long getEMC(Item item) {
            return EMCManager.get(item);
        }

        public static long getEMC(String id) {
            return EMCManager.get(ItemUtil.fromId(new Identifier(id)));
        }
    }

    public static class PLAYER {
        public static void incrementEMC(Player player, long amount) {
            EMCManager.incrementEmc(player, amount);
        }

        public static void decrementEMC(Player player, long amount) {
            EMCManager.decrementEmc(player, amount);
        }

        public static long getEMC(Player player) {
            return EMCManager.getEmcFromPlayer(player);
        }

        public static void setEMC(Player player, long amount) {
            EMCManager.setEMCtoPlayer(player, amount);
        }
    }

    public static void addDef(EMCDef def) {
        EMCManager.addDef(def);
    }

    public static void addDef(EMCDef... defs) {
        for (EMCDef def : defs) {
            EMCManager.addDef(def);
        }
    }
}
