package net.pitan76.itemalchemy.manager;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.pitan76.itemalchemy.item.DarkMatterArmor;
import net.pitan76.itemalchemy.item.IArmorEffect;
import net.pitan76.itemalchemy.item.RedMatterArmor;
import net.pitan76.mcpitanlib.api.entity.CompatPlayerAbilities;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.item.ArmorEquipmentType;
import net.pitan76.mcpitanlib.api.util.EntityUtil;
import net.pitan76.mcpitanlib.api.util.ItemStackUtil;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 装備している防具の常時発動能力を管理する。
 */
public class ArmorEffectManager {

    // ブーツ装備時のステップ高さ
    private static final float STEP_HEIGHT = 1.0F;

    // 自分が付与した飛行のみを解除するための記録 (他modが付与した飛行を奪わないため)
    private static final Set<UUID> flightGranted = ConcurrentHashMap.newKeySet();

    public static void tick(Player player) {
        updateFlight(player);
        updateStepHeight(player);
    }

    public static void onQuit(Player player) {
        flightGranted.remove(player.getUUID());
    }

    private static void updateFlight(Player player) {
        boolean active = isEffectActive(player, ArmorEquipmentType.CHEST, RedMatterArmor.class);
        CompatPlayerAbilities abilities = player.getCompatAbilities();
        UUID uuid = player.getUUID();

        // クリエイティブの飛行権限には触らない
        if (abilities.isCreativeMode()) {
            flightGranted.remove(uuid);
            return;
        }

        if (active) {
            flightGranted.add(uuid);

            if (abilities.allowFlying()) return;

            abilities.setAllowFlying(true);
            abilities.sync();
            return;
        }

        // 自分が付与していない飛行は解除しない
        if (!flightGranted.remove(uuid)) return;
        if (!abilities.allowFlying()) return;

        abilities.setAllowFlying(false);
        abilities.setFlying(false);
        abilities.sync();
    }

    private static void updateStepHeight(Player player) {
        boolean active = isEffectActive(player, ArmorEquipmentType.FEET, RedMatterArmor.class)
                || isEffectActive(player, ArmorEquipmentType.FEET, DarkMatterArmor.class);

        float stepHeight = active ? STEP_HEIGHT : EntityUtil.getDefaultStepHeight(player.getEntity());
        if (EntityUtil.getStepHeight(player.getEntity()) == stepHeight) return;

        EntityUtil.setStepHeight(player.getEntity(), stepHeight);
    }

    /**
     * 指定部位に該当の防具を装備していて、かつ能力が有効かどうか。
     */
    private static boolean isEffectActive(Player player, ArmorEquipmentType type, Class<? extends IArmorEffect> armorClass) {
        ItemStack stack = player.getEquippedStack(type);
        Item item = ItemStackUtil.getItem(stack);

        if (!armorClass.isInstance(item)) return false;

        return ((IArmorEffect) item).isEffectEnabled(stack);
    }
}
