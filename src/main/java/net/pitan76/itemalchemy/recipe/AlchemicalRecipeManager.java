package net.pitan76.itemalchemy.recipe;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.Ingredient;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.pitan76.itemalchemy.EMCManager;
import net.pitan76.itemalchemy.ItemAlchemy;
import net.pitan76.itemalchemy.item.Items;
import net.pitan76.mcpitanlib.api.event.v0.event.RecipeManagerEvent;
import net.pitan76.mcpitanlib.api.recipe.CompatibleRecipeEntry;
import net.pitan76.mcpitanlib.api.util.*;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static net.pitan76.itemalchemy.ItemAlchemy._id;

// Referred to "TransmutationRecipeManager (RetroExchange)"
// https://github.com/modmuss50/Retro-Exchange/blob/main/common/src/main/java/me/modmuss50/retroexchange/TransmutationRecipeManager.java (modmuss50)
public class AlchemicalRecipeManager {

    private int count = 0;

    public static AlchemicalRecipeManager INSTANCE;

    public RecipeManagerEvent event;

    public AlchemicalRecipeManager(RecipeManagerEvent e) {
        INSTANCE = this;
        this.event = e;
        apply();
    }

    private final Gson gson = new Gson();

    public void apply() {

        // load EMC
        EMCManager.loadDefaultEMCs(event.getResourceManager());

        count = 0;

        Map<Identifier, Resource> resourceIds;
        try {
            resourceIds = ResourceUtil.findResources(event.getResourceManager(), "alchemical_craft", ".json");
        } catch (IOException e) {
            ItemAlchemy.INSTANCE.error("Failed to read alchemy.json: " + e.getMessage());
            return;
        }

        resourceIds.forEach((resourceId, resource) -> {
            try {
                String json = IOUtils.toString(ResourceUtil.getInputStream(resource), StandardCharsets.UTF_8);
                ResourceUtil.close(resource);
                JsonArray jsonArray = gson.fromJson(json, JsonArray.class);
                jsonArray.forEach((jsonElement) -> handle(jsonElement.getAsJsonObject()));
            } catch (Exception e) {
                e.printStackTrace();
                ItemAlchemy.INSTANCE.error("Failed to read {}: " + resourceId.toString() + " " + e.getMessage());
            }
        });
    }

    private void handle(JsonObject jsonObject) {
        JsonArray itemsArray = jsonObject.get("items").getAsJsonArray();

        CompatIdentifier inputId = CompatIdentifier.of(itemsArray.get(0).getAsString());
        CompatIdentifier outputId = CompatIdentifier.of(itemsArray.get(1).getAsString());

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
        inputs[0] = Items.PHILOSOPHER_STONE.get();

        CompatIdentifier id = _id("alchemical_craft/n" + count++);

        CompatibleRecipeEntry recipe = CompatibleRecipeEntryUtil.createShapelessRecipe(id, "", RecipeUtil.CompatibilityCraftingRecipeCategory.MISC, ItemStackUtil.create(output), buildInput(inputs));
        event.putCompatibleRecipeEntry(recipe);
    }

    private DefaultedList<Ingredient> buildInput(Object[] input) {
        DefaultedList<Ingredient> list = DefaultedList.of();
        for (Object obj : input) {
            Ingredient ingredient = null;
            if (obj instanceof Ingredient)
                ingredient = (Ingredient) obj;

            if (obj instanceof ItemConvertible)
                ingredient = Ingredient.ofItems((ItemConvertible) obj);

            if (ingredient != null)
                list.add(ingredient);
        }
        return list;
    }
}
