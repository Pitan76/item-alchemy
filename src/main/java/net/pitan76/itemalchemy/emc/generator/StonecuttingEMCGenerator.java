package net.pitan76.itemalchemy.emc.generator;

import net.pitan76.mcpitanlib.api.util.CompatIdentifier;
import net.pitan76.mcpitanlib.midohra.recipe.Recipe;
import net.pitan76.mcpitanlib.midohra.recipe.RecipeType;
import net.pitan76.mcpitanlib.midohra.recipe.entry.RecipeEntry;
import net.pitan76.mcpitanlib.midohra.recipe.input.RecipeInputOrInventory;
import net.pitan76.mcpitanlib.midohra.world.ServerWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class StonecuttingEMCGenerator implements IEMCGenerator {
    @Override
    public void generate(ServerWorld world) {
        List<Recipe> unsetRecipes = new ArrayList<>();

        Stream<RecipeEntry> recipeEntryStream = world.getRecipeManager().getRecipeEntries().stream()
                .filter(entry -> entry.getRecipeType().equals(RecipeType.STONECUTTING));

        recipeEntryStream.forEach(entry -> {
            try {
                CraftingEMCGenerator.addEmcFromRecipe(entry.getRecipe().craft(RecipeInputOrInventory.NONE, world), entry.getRecipe(), unsetRecipes, false);
            } catch (NoClassDefFoundError | Exception ignore) {}
        });
    }

    @Override
    public CompatIdentifier getId() {
        return CompatIdentifier.of("stonecutting");
    }
}
