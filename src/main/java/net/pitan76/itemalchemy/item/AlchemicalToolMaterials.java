package net.pitan76.itemalchemy.item;

import net.minecraft.recipe.Ingredient;
import net.pitan76.mcpitanlib.api.item.tool.CompatibleToolMaterial;

public enum AlchemicalToolMaterials implements CompatibleToolMaterial {

    DARK_MATTER(3, 0, 10.0F, 2.0F, 16, Ingredient.ofItems(Items.DARK_MATTER.getOrNull())),
    RED_MATTER(3, 0, 15.0F, 4.0F, 20, Ingredient.ofItems(Items.RED_MATTER.getOrNull()));

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
