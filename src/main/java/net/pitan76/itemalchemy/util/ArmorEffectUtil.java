package net.pitan76.itemalchemy.util;

import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.entity.effect.CompatStatusEffect;
import net.pitan76.mcpitanlib.api.entity.effect.CompatStatusEffectInstance;

import java.util.List;

/**
 * 防具の常時効果を付与するためのユーティリティ。
 */
public class ArmorEffectUtil {

    /**
     * 効果を付け直す残り時間の閾値 (tick)。
     * これより長く残っている場合は何もしない。
     */
    public static final int REFRESH_THRESHOLD = 150;

    /**
     * 付与する効果の長さ (tick)。
     */
    public static final int EFFECT_DURATION = 1000;

    /**
     * 残り時間が短い場合のみ効果を付け直す。
     * 毎回 addStatusEffect すると同期パケットが飛び続けて重くなるため。
     */
    public static void refresh(Player player, List<CompatStatusEffectInstance> current, CompatStatusEffect effect, int amplifier) {
        for (CompatStatusEffectInstance instance : current) {
            if (!instance.getCompatStatusEffect().map(effect::equals).orElse(false)) continue;
            if (instance.getAmplifier() < amplifier) break;
            if (instance.isInfinite() || instance.getDuration() > REFRESH_THRESHOLD) return;
            break;
        }

        player.addStatusEffect(new CompatStatusEffectInstance(effect, EFFECT_DURATION, amplifier, true, false));
    }
}
