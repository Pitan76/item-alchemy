package net.pitan76.itemalchemy.item;

import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.pitan76.itemalchemy.ItemAlchemy;
import net.pitan76.mcpitanlib.api.item.ArmorEquipmentType;
import net.pitan76.mcpitanlib.api.item.CompatibleArmorMaterial;
import net.pitan76.mcpitanlib.api.sound.CompatSoundEvents;
import net.pitan76.mcpitanlib.api.util.CompatIdentifier;
import net.pitan76.mcpitanlib.api.util.IngredientUtil;

public enum AlchemicalArmorMaterials implements CompatibleArmorMaterial {

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
    public SoundEvent getEquipSound() {
        return CompatSoundEvents.ITEM_ARMOR_EQUIP_NETHERITE.get();
    }

    @Override
    public Ingredient getRepairIngredient() {
        if (this == DARK_MATTER) {
            return IngredientUtil.ofItems(Items.DARK_MATTER.getOrNull());
        }
        return IngredientUtil.ofItems(Items.RED_MATTER.getOrNull());
    }

    @Override
    public Identifier getId() {
        return CompatIdentifier.of(ItemAlchemy.MOD_ID, name + "_armor").toMinecraft();
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
