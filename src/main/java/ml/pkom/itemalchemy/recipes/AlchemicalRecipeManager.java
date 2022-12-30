package ml.pkom.itemalchemy.recipes;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ml.pkom.itemalchemy.EMCManager;
import ml.pkom.itemalchemy.ItemAlchemy;
import ml.pkom.mcpitanlibarch.api.util.ItemUtil;
import ml.pkom.mcpitanlibarch.api.util.RecipeUtil;
import ml.pkom.mcpitanlibarch.api.util.ResourceUtil;
import net.minecraft.datafixer.fix.BlockEntitySignTextStrictJsonFix;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

// Referred to "TransmutationRecipeManager (RetroExchange)"
// https://github.com/modmuss50/Retro-Exchange/blob/main/common/src/main/java/me/modmuss50/retroexchange/TransmutationRecipeManager.java (modmuss50)
public class AlchemicalRecipeManager {

    private int count = 0;
    private final Map<RecipeType<?>, ImmutableMap.Builder<Identifier, Recipe<?>>> map;

    public AlchemicalRecipeManager(Map<RecipeType<?>, ImmutableMap.Builder<Identifier, Recipe<?>>> map) {
        this.map = map;
    }

    public void apply(ResourceManager resourceManager) {
        count = 0;

        Map<Identifier, Resource> resourceIds;
        try {
            resourceIds = ResourceUtil.findResources(resourceManager, "alchemical_craft", ".json");
        } catch (IOException e) {
            ItemAlchemy.LOGGER.error("Failed to read alchemy.json", e);
            return;
        }

        resourceIds.forEach((resourceId, resource) -> {
            try {
                String json = IOUtils.toString(ResourceUtil.getInputStream(resource), StandardCharsets.UTF_8);
                ResourceUtil.close(resource);
                JsonArray jsonArray = BlockEntitySignTextStrictJsonFix.GSON.fromJson(json, JsonArray.class);
                jsonArray.forEach((jsonElement) -> handle(jsonElement.getAsJsonObject()));
            } catch (Exception e) {
                e.printStackTrace();
                ItemAlchemy.LOGGER.error("Failed to read {}", resourceId.toString(), e);
            }
        });

        // load EMC
        EMCManager.loadDefaultEMCs(resourceManager);
    }

    private void handle(JsonObject jsonObject) {
        JsonArray itemsArray = jsonObject.get("items").getAsJsonArray();

        Identifier inputId = new Identifier(itemsArray.get(0).getAsString());
        Identifier outputId = new Identifier(itemsArray.get(1).getAsString());

        Item input = ItemUtil.fromId(inputId);
        Item output = ItemUtil.fromId(outputId);

        int amount = jsonObject.get("amount").getAsInt();

        addAlchemicalRecipe(output, input, amount);
    }

    public void addAlchemicalRecipe(Item output, Object input, int size) {
        Object[] inputs = new Object[size + 1];
        for (int i = 1; i < size + 1; i++) {
            inputs[i] = input;
        }
        inputs[0] = ml.pkom.itemalchemy.Items.PHILOSOPHER_STONE.get();

        ShapelessRecipe recipe = RecipeUtil.createShapelessRecipe(ItemAlchemy.id("alchemical_craft/n" + count++), "", new ItemStack(output), buildInput(inputs));
        map.get(recipe.getType()).put(recipe.getId(), recipe);
    }

    private DefaultedList<Ingredient> buildInput(Object[] input) {
        DefaultedList<Ingredient> list = DefaultedList.of();
        for (Object obj : input) {
            Ingredient ingredient = null;
            if (obj instanceof Ingredient)
                ingredient = (Ingredient) obj;

            if (obj instanceof ItemConvertible)
                ingredient = Ingredient.ofItems((ItemConvertible) obj);

            list.add(ingredient);
        }
        return list;
    }
}
