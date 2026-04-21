package net.pitan76.itemalchemy.item;

import net.pitan76.mcpitanlib.api.item.v3.CompatToolMaterial;
import net.pitan76.mcpitanlib.api.tag.item.RepairIngredientTag;

public enum AlchemicalToolMaterials implements CompatToolMaterial {

    DARK_MATTER(3, 16, 10.0F, 2.0F, 16),
    RED_MATTER(3, 16, 15.0F, 4.0F, 20);

    private final int miningLevel;
    private final int itemDurability;
    private final float miningSpeed;
    private final float attackDamage;
    private final int enchantability;
    private final RepairIngredientTag repairIngredientTag;

    AlchemicalToolMaterials(int miningLevel, int itemDurability, float miningSpeed, float attackDamage, int enchantability, RepairIngredientTag repairIngredientTag) {
        this.miningLevel = miningLevel;
        this.itemDurability = itemDurability;
        this.miningSpeed = miningSpeed;
        this.attackDamage = attackDamage;
        this.enchantability = enchantability;
        this.repairIngredientTag = repairIngredientTag;
    }

    AlchemicalToolMaterials(int miningLevel, int itemDurability, float miningSpeed, float attackDamage, int enchantability) {
        this(miningLevel, itemDurability, miningSpeed, attackDamage, enchantability, RepairIngredientTag.STONE_TOOL_MATERIALS);
    }

    @Override
    public int getCompatDurability() {
        return this.itemDurability;
    }

    @Override
    public float getCompatMiningSpeedMultiplier() {
        return this.miningSpeed;
    }

    @Override
    public float getCompatAttackDamage() {
        return this.attackDamage;
    }

    @Override
    public int getCompatMiningLevel() {
        return this.miningLevel;
    }

    @Override
    public int getCompatEnchantability() {
        return this.enchantability;
    }

    @Override
    public RepairIngredientTag getRepairIngredientTag() {
        return repairIngredientTag;
    }
}
