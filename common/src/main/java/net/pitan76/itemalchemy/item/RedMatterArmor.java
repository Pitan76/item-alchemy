package net.pitan76.itemalchemy.item;

import net.minecraft.item.ItemStack;
import net.pitan76.itemalchemy.util.TooltipUtil;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.entity.effect.CompatStatusEffect;
import net.pitan76.mcpitanlib.api.entity.effect.CompatStatusEffectInstance;
import net.pitan76.mcpitanlib.api.event.item.InventoryTickEvent;
import net.pitan76.mcpitanlib.api.item.ArmorEquipmentType;
import net.pitan76.mcpitanlib.api.item.CompatibleArmorItem;
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.util.CompatIdentifier;
import net.pitan76.mcpitanlib.api.util.EntityUtil;
import net.pitan76.mcpitanlib.api.util.ItemStackUtil;
import net.pitan76.mcpitanlib.api.util.StatusEffectUtil;
import net.pitan76.mcpitanlib.midohra.world.World;

public class RedMatterArmor extends CompatibleArmorItem {

    private static final int EFFECT_INTERVAL = 60;
    private static final int EFFECT_DURATION = 100;

    private static final CompatStatusEffect NIGHT_VISION = StatusEffectUtil.getStatusEffect(CompatIdentifier.of("minecraft", "night_vision"));
    private static final CompatStatusEffect FIRE_RESISTANCE = StatusEffectUtil.getStatusEffect(CompatIdentifier.of("minecraft", "fire_resistance"));
    private static final CompatStatusEffect SPEED = StatusEffectUtil.getStatusEffect(CompatIdentifier.of("minecraft", "speed"));
    private static final CompatStatusEffect WATER_BREATHING = StatusEffectUtil.getStatusEffect(CompatIdentifier.of("minecraft", "water_breathing"));
    private static final CompatStatusEffect JUMP_BOOST = StatusEffectUtil.getStatusEffect(CompatIdentifier.of("minecraft", "jump_boost"));
    private static final CompatStatusEffect REGENERATION = StatusEffectUtil.getStatusEffect(CompatIdentifier.of("minecraft", "regeneration"));

    public RedMatterArmor(ArmorEquipmentType type, CompatibleItemSettings settings) {
        super(AlchemicalArmorMaterials.RED_MATTER, type, settings);
    }

    @Override
    public void appendTooltip(net.pitan76.mcpitanlib.api.event.item.ItemAppendTooltipEvent e, Options options) {
        e.addTooltip(TooltipUtil.generateTooltipLines(ItemStackUtil.getItem(e.getStack())));
    }

    @Override
    public void inventoryTick(InventoryTickEvent e, Options options) {
        if (e.isClient()) return;
        if (!e.isPlayer()) return;
        Player player = e.getPlayer();
        World world = e.getMidohraWorld();

        if (!isWornByPlayer(player)) return;

        if (type == ArmorEquipmentType.FEET) {
            EntityUtil.setFallDistance(player.getEntity(), 0);
        }

        if (world.getTime() % EFFECT_INTERVAL != 0) return;

        if (type == ArmorEquipmentType.HEAD) {
            player.addStatusEffect(
                    new CompatStatusEffectInstance(NIGHT_VISION, EFFECT_DURATION, 0, true, false));
            player.addStatusEffect(
                    new CompatStatusEffectInstance(WATER_BREATHING, EFFECT_DURATION, 0, true, false));
        } else if (type == ArmorEquipmentType.CHEST) {
            player.addStatusEffect(
                    new CompatStatusEffectInstance(FIRE_RESISTANCE, EFFECT_DURATION, 0, true, false));
        } else if (type == ArmorEquipmentType.LEGS) {
            player.addStatusEffect(
                    new CompatStatusEffectInstance(SPEED, EFFECT_DURATION, 1, true, false));
            player.addStatusEffect(
                    new CompatStatusEffectInstance(JUMP_BOOST, EFFECT_DURATION, 0, true, false));
        }

        if (hasFullSet(player)) {
            player.addStatusEffect(
                    new CompatStatusEffectInstance(REGENERATION, EFFECT_DURATION, 0, true, false));
        }
    }

    private boolean isWornByPlayer(Player player) {
        return ItemStackUtil.getItem(player.getEquippedStack(type)) == this;
    }

    private boolean hasFullSet(Player player) {
        ItemStack head = player.getEquippedStack(ArmorEquipmentType.HEAD);
        ItemStack chest = player.getEquippedStack(ArmorEquipmentType.CHEST);
        ItemStack legs = player.getEquippedStack(ArmorEquipmentType.LEGS);
        ItemStack feet = player.getEquippedStack(ArmorEquipmentType.FEET);
        return ItemStackUtil.getItem(head) instanceof RedMatterArmor
                && ItemStackUtil.getItem(chest) instanceof RedMatterArmor
                && ItemStackUtil.getItem(legs) instanceof RedMatterArmor
                && ItemStackUtil.getItem(feet) instanceof RedMatterArmor;
    }
}
