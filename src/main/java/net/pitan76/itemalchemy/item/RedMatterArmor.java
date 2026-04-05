package net.pitan76.itemalchemy.item;

import net.minecraft.entity.player.PlayerEntity;
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
import net.pitan76.mcpitanlib.api.util.WorldUtil;
import net.pitan76.mcpitanlib.api.util.entity.LivingEntityUtil;

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
        if (!(e.entity instanceof PlayerEntity)) return;
        Player player = new Player((PlayerEntity) e.entity);

        if (!isWornByPlayer(player.getEntity())) return;

        if (type == ArmorEquipmentType.FEET) {
            EntityUtil.setFallDistance(player.getEntity(), 0);
        }

        if (WorldUtil.getTime(e.world) % EFFECT_INTERVAL != 0) return;

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

        if (hasFullSet(player.getEntity())) {
            player.addStatusEffect(
                    new CompatStatusEffectInstance(REGENERATION, EFFECT_DURATION, 0, true, false));
        }
    }

    // TODO: 修正
    @SuppressWarnings("deprecation")
    private boolean isWornByPlayer(PlayerEntity player) {
        return ItemStackUtil.getItem(LivingEntityUtil.getEquippedStack(player, type.getSlot())) == this;
    }

    @SuppressWarnings("deprecation")
    private boolean hasFullSet(PlayerEntity player) {
        ItemStack head = LivingEntityUtil.getEquippedStack(player, ArmorEquipmentType.HEAD.getSlot());
        ItemStack chest = LivingEntityUtil.getEquippedStack(player, ArmorEquipmentType.CHEST.getSlot());
        ItemStack legs = LivingEntityUtil.getEquippedStack(player, ArmorEquipmentType.LEGS.getSlot());
        ItemStack feet = LivingEntityUtil.getEquippedStack(player, ArmorEquipmentType.FEET.getSlot());
        return ItemStackUtil.getItem(head) instanceof RedMatterArmor
                && ItemStackUtil.getItem(chest) instanceof RedMatterArmor
                && ItemStackUtil.getItem(legs) instanceof RedMatterArmor
                && ItemStackUtil.getItem(feet) instanceof RedMatterArmor;
    }
}
