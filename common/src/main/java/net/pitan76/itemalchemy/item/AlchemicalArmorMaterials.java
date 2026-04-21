package net.pitan76.itemalchemy.item;

import net.pitan76.itemalchemy.ItemAlchemy;
import net.pitan76.mcpitanlib.api.item.ArmorEquipmentType;
import net.pitan76.mcpitanlib.api.item.v3.CompatArmorMaterial;
import net.pitan76.mcpitanlib.api.sound.CompatSoundEvent;
import net.pitan76.mcpitanlib.api.sound.CompatSoundEvents;
import net.pitan76.mcpitanlib.api.tag.item.RepairIngredientTag;
import net.pitan76.mcpitanlib.api.util.CompatIdentifier;

public enum AlchemicalArmorMaterials implements CompatArmorMaterial {

    DARK_MATTER(
            "dark_matter",
            new int[]{39, 39, 39, 39}, // durability: helmet, chest, legs, boots (high like netherite)
            new int[]{4, 9, 7, 4},     // protection: helmet, chest, legs, boots
            20,                         // enchantability
            3.5f,                       // toughness
            0.2f                        // knockback resistance
    ),
    RED_MATTER(
            "red_matter",
            new int[]{45, 45, 45, 45},
            new int[]{5, 11, 9, 5},
            25,
            4.0f,
            0.3f
    );

    private static final int[] BASE_DURABILITY = {13, 15, 16, 11};

    private final String name;
    private final int[] durabilityMultiplier;
    private final int[] protectionAmounts;
    private final int enchantability;
    private final float toughness;
    private final float knockbackResistance;

    AlchemicalArmorMaterials(String name, int[] durabilityMultiplier, int[] protectionAmounts,
                             int enchantability, float toughness, float knockbackResistance) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.protectionAmounts = protectionAmounts;
        this.enchantability = enchantability;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
    }

    @Override
    public int getDurability(ArmorEquipmentType type) {
        int index = slotIndex(type);
        return BASE_DURABILITY[index] * durabilityMultiplier[index];
    }

    @Override
    public int getProtection(ArmorEquipmentType type) {
        return protectionAmounts[slotIndex(type)];
    }

    @Override
    public int getEnchantability() {
        return enchantability;
    }

    @Override
    public CompatSoundEvent getEquipCompatSound() {
        return CompatSoundEvents.ITEM_ARMOR_EQUIP_NETHERITE;
    }

    @Override
    public RepairIngredientTag getRepairIngredientTag() {
        if (this == DARK_MATTER) {
            return new RepairIngredientTag(ItemAlchemy._id("dark_matter"));
        }
        return new RepairIngredientTag(ItemAlchemy._id("red_matter"));
    }

    @Override
    public CompatIdentifier getCompatId() {
        return ItemAlchemy._id(name + "_armor");
    }

    @Override
    public float getToughness() {
        return toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return knockbackResistance;
    }

    private int slotIndex(ArmorEquipmentType type) {
        if (type == ArmorEquipmentType.HEAD) return 0;
        if (type == ArmorEquipmentType.CHEST) return 1;
        if (type == ArmorEquipmentType.LEGS) return 2;
        return 3; // FEET
    }
}
