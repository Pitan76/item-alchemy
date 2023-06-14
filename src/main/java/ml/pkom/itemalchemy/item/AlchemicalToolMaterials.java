package ml.pkom.itemalchemy.item;

import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public enum AlchemicalToolMaterials implements ToolMaterial {

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

    public int getDurability() {
        return this.itemDurability;
    }

    public float getMiningSpeedMultiplier() {
        return this.miningSpeed;
    }

    public float getAttackDamage() {
        return this.attackDamage;
    }

    public int getMiningLevel() {
        return this.miningLevel;
    }

    public int getEnchantability() {
        return this.enchantability;
    }

    public Ingredient getRepairIngredient() {
        return this.repairIngredient;
    }
}
