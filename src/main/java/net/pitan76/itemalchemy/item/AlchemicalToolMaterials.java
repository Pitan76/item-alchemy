package net.pitan76.itemalchemy.item;

import net.minecraft.recipe.Ingredient;
import net.pitan76.mcpitanlib.api.item.tool.CompatibleToolMaterial;

public enum AlchemicalToolMaterials implements CompatibleToolMaterial {

    DARK_MATTER(3, 16, 10.0F, 2.0F, 16),
    RED_MATTER(3, 16, 15.0F, 4.0F, 20);

    private final int miningLevel;
    private final int itemDurability;
    private final float miningSpeed;
    private final float attackDamage;
    private final int enchantability;
    private final Ingredient repairIngredient;

    AlchemicalToolMaterials(int miningLevel, int itemDurability, float miningSpeed, float attackDamage, int enchantability, Ingredient repairIngredient) {
        this.miningLevel = miningLevel;
        this.itemDurability = itemDurability;
        this.miningSpeed = miningSpeed;
        this.attackDamage = attackDamage;
        this.enchantability = enchantability;
        this.repairIngredient = repairIngredient;
    }

    AlchemicalToolMaterials(int miningLevel, int itemDurability, float miningSpeed, float attackDamage, int enchantability) {
        this(miningLevel, itemDurability, miningSpeed, attackDamage, enchantability, empty());
    }

    public static Ingredient empty() {
        try {
            return Ingredient.empty();
        } catch (NoSuchMethodError e) {
            try {
                return Ingredient.EMPTY;
            } catch (NoSuchFieldError e2) {
                return Ingredient.ofItems();
            }
        }
    }

    public int getCompatDurability() {
        return this.itemDurability;
    }

    public float getCompatMiningSpeedMultiplier() {
        return this.miningSpeed;
    }

    public float getCompatAttackDamage() {
        return this.attackDamage;
    }

    public int getCompatMiningLevel() {
        return this.miningLevel;
    }

    public int getCompatEnchantability() {
        return this.enchantability;
    }

    public Ingredient getCompatRepairIngredient() {
        return this.repairIngredient;
    }
}
