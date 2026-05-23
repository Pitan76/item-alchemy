package net.pitan76.itemalchemy.emc.generator;

import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.mcpitanlib.api.util.CompatIdentifier;
import net.pitan76.mcpitanlib.midohra.item.ItemStack;
import net.pitan76.mcpitanlib.midohra.recipe.Recipe;
import net.pitan76.mcpitanlib.midohra.recipe.RecipeType;
import net.pitan76.mcpitanlib.midohra.recipe.entry.RecipeEntry;
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
                ItemStack output = entry.getRecipe().getOutput(world);
                EMCManager.addEmcFromRecipe(output.toMinecraft(), entry.getRecipe(), unsetRecipes, true);
            } catch (NoClassDefFoundError | Exception ignore) {}
        }
    }

    @Override
    public CompatIdentifier getId() {
        return CompatIdentifier.of("smelting");
    }
}
