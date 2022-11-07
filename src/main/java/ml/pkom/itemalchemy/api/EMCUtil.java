package ml.pkom.itemalchemy.api;

import ml.pkom.itemalchemy.EMCManager;
import ml.pkom.mcpitanlibarch.api.entity.Player;
import ml.pkom.mcpitanlibarch.api.util.ItemUtil;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

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

}
