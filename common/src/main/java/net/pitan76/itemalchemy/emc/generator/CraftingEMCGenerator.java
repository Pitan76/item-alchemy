package net.pitan76.itemalchemy.emc.generator;

import net.minecraft.item.ItemStack;
import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.mcpitanlib.api.util.CompatIdentifier;
import net.pitan76.mcpitanlib.midohra.recipe.*;
import net.pitan76.mcpitanlib.midohra.recipe.entry.RecipeEntry;
import net.pitan76.mcpitanlib.midohra.recipe.entry.ShapedRecipeEntry;
import net.pitan76.mcpitanlib.midohra.recipe.entry.ShapelessRecipeEntry;
import net.pitan76.mcpitanlib.midohra.world.ServerWorld;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CraftingEMCGenerator implements IEMCGenerator {

    @Override
    public void generate(ServerWorld world) {
        List<Recipe> unsetRecipes = new ArrayList<>();

        ServerRecipeManager recipeManager = world.getRecipeManager();
        Collection<RecipeEntry> recipes = recipeManager.getNormalRecipeEntries();

        for (RecipeEntry recipeEntry : recipes) {
            try {
                ItemStack outStack;
                if (recipeEntry instanceof ShapedRecipeEntry) {
                    outStack = ((ShapedRecipeEntry) recipeEntry).getRecipe().craft(world);
                } else if (recipeEntry instanceof ShapelessRecipeEntry) {
                    outStack = ((ShapelessRecipeEntry) recipeEntry).getRecipe().craft(world);
                } else {
                    continue;
                }

                addEmcFromRecipe(outStack, recipeEntry.getRecipe(), unsetRecipes, false);
            } catch (NoClassDefFoundError | Exception ignore) {}
        }

        List<Recipe> dummy = new ArrayList<>();
        for (Recipe recipe : unsetRecipes) {
            try {
                ItemStack outStack;
                if (recipe instanceof ShapedRecipe) {
                    outStack = ((ShapedRecipe) recipe).craft(world);
                } else if (recipe instanceof ShapelessRecipe) {
                    outStack = ((ShapelessRecipe) recipe).craft(world);
                } else {
                    continue;
                }
                addEmcFromRecipe(outStack, recipe, dummy, true);

            } catch (NoClassDefFoundError | Exception ignore) {}
        }
    }

    @Override
    public CompatIdentifier getId() {
        return CompatIdentifier.of("crafting");
    }

    public static void addEmcFromRecipe(ItemStack outStack, Recipe recipe, List<Recipe> unsetRecipes, boolean last) {
        EMCManager.addEmcFromRecipe(outStack, recipe, unsetRecipes, last);
    }
}