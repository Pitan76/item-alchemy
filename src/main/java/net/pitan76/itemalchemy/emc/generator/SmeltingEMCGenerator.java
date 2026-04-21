package net.pitan76.itemalchemy.emc.generator;

import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.mcpitanlib.api.util.CompatIdentifier;
import net.pitan76.mcpitanlib.api.util.inventory.CompatInventory;
import net.pitan76.mcpitanlib.midohra.item.ItemStack;
import net.pitan76.mcpitanlib.midohra.recipe.Ingredient;
import net.pitan76.mcpitanlib.midohra.recipe.Recipe;
import net.pitan76.mcpitanlib.midohra.recipe.RecipeType;
import net.pitan76.mcpitanlib.midohra.recipe.entry.RecipeEntry;
import net.pitan76.mcpitanlib.midohra.recipe.input.RecipeInputOrInventory;
import net.pitan76.mcpitanlib.midohra.world.ServerWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SmeltingEMCGenerator implements IEMCGenerator {
    @Override
    public void generate(ServerWorld world) {
        List<Recipe> unsetRecipes = new ArrayList<>();
        List<RecipeEntry> entries = world.getRecipeManager().getRecipeEntries().stream()
                .filter(entry -> entry.getRecipeType().equals(RecipeType.SMELTING))
                .collect(Collectors.toList());

        for (RecipeEntry entry : entries) {
            try {
                List<Ingredient> ingredients = entry.getRecipe().getInputs().stream()
                        .map(Ingredient::of)
                        .collect(Collectors.toList());

                CompatInventory inventory = new CompatInventory(2);
                for (int i = 0; i < ingredients.size(); i++) {
                    ItemStack stack = ingredients.get(i).getMatchingStacksAsMidohra()[0];
                    inventory.callSetStack(i, stack);
                }

                ItemStack output = entry.getRecipe().craftMidohra(RecipeInputOrInventory.of(inventory), world);
                EMCManager.addEmcFromRecipe(output.toMinecraft(), entry.getRecipe(), unsetRecipes, true);
            } catch (NoClassDefFoundError | Exception ignore) {}
        }
    }

    @Override
    public CompatIdentifier getId() {
        return CompatIdentifier.of("smelting");
    }
}
