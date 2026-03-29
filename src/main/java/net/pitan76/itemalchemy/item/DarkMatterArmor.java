package net.pitan76.itemalchemy.item;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import java.util.ArrayList;
import java.util.List;
import net.pitan76.itemalchemy.util.TooltipUtil;
import net.pitan76.mcpitanlib.api.entity.effect.CompatStatusEffect;
import net.pitan76.mcpitanlib.api.entity.effect.CompatStatusEffectInstance;
import net.pitan76.mcpitanlib.api.event.item.InventoryTickEvent;
import net.pitan76.mcpitanlib.api.item.ArmorEquipmentType;
import net.pitan76.mcpitanlib.api.item.CompatibleArmorItem;
import net.pitan76.mcpitanlib.api.item.ExtendItemProvider;
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;
import net.pitan76.mcpitanlib.api.util.CompatIdentifier;
import net.pitan76.mcpitanlib.api.util.EntityUtil;
import net.pitan76.mcpitanlib.api.util.ItemStackUtil;
import net.pitan76.mcpitanlib.api.util.StatusEffectUtil;
import net.pitan76.mcpitanlib.api.util.TextUtil;
import net.pitan76.mcpitanlib.api.util.WorldUtil;
import net.pitan76.mcpitanlib.api.util.item.ItemUtil;
import net.pitan76.mcpitanlib.api.util.entity.LivingEntityUtil;
import net.pitan76.mcpitanlib.api.item.ExtendItemProvider.Options;

public class DarkMatterArmor extends CompatibleArmorItem {

    private static final int EFFECT_INTERVAL = 60;
    private static final int EFFECT_DURATION = 100;

    private static final CompatStatusEffect NIGHT_VISION = StatusEffectUtil.getStatusEffect(CompatIdentifier.of("minecraft", "night_vision"));
    private static final CompatStatusEffect FIRE_RESISTANCE = StatusEffectUtil.getStatusEffect(CompatIdentifier.of("minecraft", "fire_resistance"));
    private static final CompatStatusEffect SPEED = StatusEffectUtil.getStatusEffect(CompatIdentifier.of("minecraft", "speed"));

    public DarkMatterArmor(ArmorEquipmentType type, CompatibleItemSettings settings) {
        super(AlchemicalArmorMaterials.DARK_MATTER, type, settings);
    }

    @Override
    public void appendTooltip(net.pitan76.mcpitanlib.api.event.item.ItemAppendTooltipEvent e, Options options) {
        options.cancel = true;
        net.minecraft.item.ItemStack stack = e.getStack();
        String translationKey = ItemUtil.getTranslationKey(stack.getItem());
        
        if (TooltipUtil.hasShiftDown()) {
            String shiftKey = translationKey + ".desc_shift";
            if (I18n.hasTranslation(shiftKey)) {
                String shiftText = I18n.translate(shiftKey);
                List<String> lines = splitTooltipText(shiftText);
                for (String line : lines) {
                    e.addTooltip(TextUtil.literal(line));
                }
            }
        } else {
            // Show basic description with multi-line support (only when shift is NOT held)
            String descKey = translationKey + ".desc";
            if (I18n.hasTranslation(descKey)) {
                String descText = I18n.translate(descKey);
                List<String> descLines = splitTooltipText(descText);
                for (String line : descLines) {
                    e.addTooltip(TextUtil.literal(line));
                }
            }
            e.addTooltip(TextUtil.withColor(TextUtil.translatable("text.itemalchemy.shift_info"), 0x555555));
        }
    }
    
    private List<String> splitTooltipText(String text) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return lines;
        }
        
        int start = 0;
        int delimiterIndex;
        boolean foundDelimiter = false;
        
        while ((delimiterIndex = text.indexOf("|||", start)) != -1) {
            foundDelimiter = true;
            String line = text.substring(start, delimiterIndex);
            if (!line.trim().isEmpty()) {
                lines.add(line);
            }
            start = delimiterIndex + 3;
        }
        
        if (start < text.length()) {
            String remaining = text.substring(start);
            if (!remaining.trim().isEmpty()) {
                lines.add(remaining);
            }
        }
        
        return lines;
    }

    @Override
    public void inventoryTick(InventoryTickEvent e, Options options) {
        if (e.isClient()) return;
        if (!(e.entity instanceof PlayerEntity)) return;
        PlayerEntity player = (PlayerEntity) e.entity;

        if (!isWornByPlayer(player)) return;

        if (type == ArmorEquipmentType.FEET) {
            EntityUtil.setFallDistance(player, 0);
            return;
        }

        if (WorldUtil.getTime(e.world) % EFFECT_INTERVAL != 0) return;

        if (type == ArmorEquipmentType.HEAD) {
            LivingEntityUtil.addStatusEffect(player,
                    new CompatStatusEffectInstance(NIGHT_VISION, EFFECT_DURATION, 0, true, false));
        } else if (type == ArmorEquipmentType.CHEST) {
            LivingEntityUtil.addStatusEffect(player,
                    new CompatStatusEffectInstance(FIRE_RESISTANCE, EFFECT_DURATION, 0, true, false));
        } else if (type == ArmorEquipmentType.LEGS) {
            LivingEntityUtil.addStatusEffect(player,
                    new CompatStatusEffectInstance(SPEED, EFFECT_DURATION, 0, true, false));
        }
    }

    @SuppressWarnings("deprecation")
    private boolean isWornByPlayer(PlayerEntity player) {
        return ItemStackUtil.getItem(LivingEntityUtil.getEquippedStack(player, type.getSlot())) == this;
    }
}
