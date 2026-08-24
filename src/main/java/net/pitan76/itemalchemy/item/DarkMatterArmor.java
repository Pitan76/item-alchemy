package net.pitan76.itemalchemy.item;

import net.pitan76.itemalchemy.util.ArmorEffectUtil;
import net.pitan76.itemalchemy.util.TooltipUtil;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.entity.effect.CompatStatusEffect;
import net.pitan76.mcpitanlib.api.entity.effect.CompatStatusEffectInstance;
import net.pitan76.mcpitanlib.api.event.item.InventoryTickEvent;
import net.pitan76.mcpitanlib.api.item.ArmorEquipmentType;
import net.pitan76.mcpitanlib.api.item.CompatibleArmorItem;
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.text.TextComponent;
import net.pitan76.mcpitanlib.api.util.CompatIdentifier;
import net.pitan76.mcpitanlib.api.util.EntityUtil;
import net.pitan76.mcpitanlib.api.util.ItemStackUtil;
import net.pitan76.mcpitanlib.api.util.StatusEffectUtil;
import net.pitan76.mcpitanlib.api.util.WorldUtil;

import java.util.List;
import java.util.stream.Collectors;

public class DarkMatterArmor extends CompatibleArmorItem implements IArmorEffect {

    // 効果の残り時間をチェックする間隔 (tick)
    private static final int EFFECT_INTERVAL = 40;

    private static final CompatStatusEffect NIGHT_VISION = StatusEffectUtil.getStatusEffect(CompatIdentifier.of("minecraft", "night_vision"));
    private static final CompatStatusEffect FIRE_RESISTANCE = StatusEffectUtil.getStatusEffect(CompatIdentifier.of("minecraft", "fire_resistance"));
    private static final CompatStatusEffect SPEED = StatusEffectUtil.getStatusEffect(CompatIdentifier.of("minecraft", "speed"));
    private static final CompatStatusEffect JUMP_BOOST = StatusEffectUtil.getStatusEffect(CompatIdentifier.of("minecraft", "jump_boost"));

    public DarkMatterArmor(ArmorEquipmentType type, CompatibleItemSettings settings) {
        super(AlchemicalArmorMaterials.DARK_MATTER, type, settings);
    }

    @Override
    public void appendTooltip(net.pitan76.mcpitanlib.api.event.item.ItemAppendTooltipEvent e, Options options) {
        e.addTooltip(TooltipUtil.generateTooltipLines(e.getStackM().getItem())
                .stream().map(TextComponent::getText).collect(Collectors.toList()));
    }

    @Override
    public void inventoryTick(InventoryTickEvent e, Options options) {
        if (e.isClient()) return;
        if (!e.isPlayer()) return;
        Player player = e.getPlayer();

        if (!isWornByPlayer(player)) return;
        if (!isEffectEnabled(e.getStack())) return;

        if (type == ArmorEquipmentType.FEET)
            EntityUtil.setFallDistance(player.getEntity(), 0);

        if (WorldUtil.getTime(e.world) % EFFECT_INTERVAL != 0) return;

        List<CompatStatusEffectInstance> effects = player.getStatusEffects();

        if (type == ArmorEquipmentType.HEAD) {
            ArmorEffectUtil.refresh(player, effects, NIGHT_VISION, 0);
        } else if (type == ArmorEquipmentType.CHEST) {
            ArmorEffectUtil.refresh(player, effects, FIRE_RESISTANCE, 0);
        } else if (type == ArmorEquipmentType.LEGS) {
            ArmorEffectUtil.refresh(player, effects, SPEED, 0);
        } else if (type == ArmorEquipmentType.FEET) {
            ArmorEffectUtil.refresh(player, effects, JUMP_BOOST, 0);
        }
    }

    private boolean isWornByPlayer(Player player) {
        return ItemStackUtil.getItem(player.getEquippedStack(type)) == this;
    }
}
