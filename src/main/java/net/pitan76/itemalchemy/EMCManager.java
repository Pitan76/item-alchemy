package net.pitan76.itemalchemy;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.pitan76.easyapi.config.Config;
import net.pitan76.easyapi.config.JsonConfig;
import net.pitan76.itemalchemy.data.ModState;
import net.pitan76.itemalchemy.data.PlayerState;
import net.pitan76.itemalchemy.data.ServerState;
import net.pitan76.itemalchemy.data.TeamState;
import net.pitan76.itemalchemy.emc.EMCDef;
import net.pitan76.mcpitanlib.api.entity.Player;
import net.pitan76.mcpitanlib.api.network.PacketByteUtil;
import net.pitan76.mcpitanlib.api.network.v2.ServerNetworking;
import net.pitan76.mcpitanlib.api.tag.TagKey;
import net.pitan76.mcpitanlib.api.util.*;
import net.pitan76.mcpitanlib.api.util.item.ItemUtil;
import net.pitan76.mcpitanlib.midohra.recipe.*;
import net.pitan76.mcpitanlib.midohra.recipe.entry.RecipeEntry;
import net.pitan76.mcpitanlib.midohra.recipe.entry.ShapedRecipeEntry;
import net.pitan76.mcpitanlib.midohra.recipe.entry.ShapelessRecipeEntry;
import net.pitan76.mcpitanlib.midohra.resource.Resource;
import net.pitan76.mcpitanlib.midohra.resource.ResourceManager;
import net.pitan76.mcpitanlib.midohra.server.MCServer;
import net.pitan76.mcpitanlib.midohra.world.ServerWorld;

import java.io.File;
import java.lang.reflect.Type;
import java.util.*;

import static net.pitan76.itemalchemy.ItemAlchemy._id;

public class EMCManager {

    private static final List<EMCDef> defs = new ArrayList<>();

    private static Map<String, Long> map = new LinkedHashMap<>();

    public static String itemToId(Item item) {
        return ItemUtil.toId(item).toString();
    }

    public static void add(Item item, long emc) {
        add(itemToId(item), emc);
    }

    public static void add(String item, long emc) {
        if (!item.contains(":"))
            item = "minecraft:" + item;

        if (contains(item)) return;
        map.put(item, emc);
    }

    public static void addExist(String itemId, long emc) {
        if (ItemUtil.isExist(itemId))
            add(itemId, emc);
    }

    public static void set(Item item, long emc) {
        set(itemToId(item), emc);
    }

    public static void set(String item, long emc) {
        if (contains(item))
            map.replace(item, emc);
        else
            map.put(item, emc);
    }

    public static void remove(Item item) {
        map.remove(itemToId(item));
    }

    public static long get(Item item) {
        if (!contains(item)) return 0;
        return map.get(itemToId(item));
    }

    public static long get(ItemStack stack) {
        if (!contains(stack.getItem())) return 0;
        return get(stack.getItem()) * ItemStackUtil.getCount(stack);
    }

    public static Map<String, Long> getMap() {
        return map;
    }

    public static void setMap(Map<String, Long> map) {
        EMCManager.map = map;
    }

    public static boolean contains(Item item) {
        return contains(itemToId(item));
    }
    public static boolean contains(String item) {
        return map.containsKey(item);
    }

    public static void add2(TagKey<Item> tagKey, long emc) {
        for (Item item : ItemUtil.getItems()) {
            if (ItemUtil.isInTag(item, tagKey)) {
                add(item, emc);
            }
        }
    }

    public static File getConfigFile() {
        File dir = new File(PlatformUtil.getConfigFolderAsFile(), ItemAlchemy.MOD_ID);
        if (!dir.exists()) dir.mkdirs();

        return new File(dir, "emc_config.json");
    }

    public static Config config;

    public static void init(MinecraftServer server) {
        init(MCServer.of(server));
    }

    public static void init(MCServer server) {
        ItemAlchemy.INSTANCE.info("init emc manager");
        if (!map.isEmpty()) map = new LinkedHashMap<>();
        config = new JsonConfig();

        File file = getConfigFile();

        if (file.exists() && config.load(file)) {
            for (Map.Entry<String, Object> entry : config.configMap.entrySet()) {
                if (entry.getValue() instanceof Long) {
                    add(entry.getKey(), (Long) entry.getValue());
                }
                if (entry.getValue() instanceof Integer) {
                    add(entry.getKey(), Long.valueOf((Integer) entry.getValue()));
                }
                if (entry.getValue() instanceof Double) {
                    add(entry.getKey(), (Math.round((Double) entry.getValue())));
                }
                if (entry.getValue() instanceof String) {
                    add(entry.getKey(), Long.parseLong((String) entry.getValue()));
                }
            }
        } else {
            defaultMap();
        }

        ServerWorld world = server.getOverworld();
        setEmcFromRecipes(world);
        for (Map.Entry<String, Long> entry : getMap().entrySet()) {
            config.set(entry.getKey(), entry.getValue());
        }
        config.save(file);

        if (!world.isClient()) {
            for (Player player : world.getPlayers()) {
                syncS2C_emc_map(player);
            }
        }
    }

    public static void defaultMap() {
        if (!defaultEMCMap.isEmpty()) {
            map.putAll(defaultEMCMap);
            return;
        }

        defs.forEach(EMCDef::addAll);
    }

    public static void setEmcFromRecipes(ServerWorld world) {
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

    public static boolean addEmcFromRecipe(ItemStack outStack, Recipe recipe, List<Recipe> unsetRecipes, boolean last) {
        if (outStack == null) return false;
        if (!contains(outStack.getItem())) {
            long totalEmc = 0;
            for (net.minecraft.recipe.Ingredient rawIngredient : recipe.getInputs()) {
                Ingredient ingredient = Ingredient.of(rawIngredient);
                ItemStack[] stacks = ingredient.getMatchingStacks();
                if (stacks.length > 0) {
                    ItemStack stack = stacks[0];
                    if (contains(stack.getItem())) {
                        if (ItemStackUtil.getCount(outStack) == 0) {
                            totalEmc += get(stack.getItem());
                        } else {
                            totalEmc += get(stack.getItem()) / ItemStackUtil.getCount(outStack);
                        }
                    } else if (!unsetRecipes.contains(recipe)) {
                        if (!last) {
                            unsetRecipes.add(recipe);
                            return false;
                        }
                        //totalEmc += 1 / outStack.getCount();
                    }
                }
            }
            if (totalEmc <= 0) return false;
            add(outStack.getItem(), totalEmc);
        }
        return true;
    }

    public static void writeEmcToPlayer(Player player, ItemStack stack) {
        incrementEmc(player, EMCManager.get(stack));
    }

    public static void decrementEmc(Player player, long amount) {
        ServerState state = ServerState.of(player);

        if (!state.getPlayer(player.getUUID()).isPresent()) return;
        PlayerState playerState = state.getPlayer(player.getUUID()).get();

        if (!state.getTeam(playerState.teamID).isPresent()) return;
        TeamState teamState = state.getTeam(playerState.teamID).get();

        teamState.storedEMC -= amount;

        state.callMarkDirty();
    }

    public static void setEMCtoPlayer(Player player, long emc) {
        ServerState state = ServerState.of(player);

        if (!state.getPlayer(player.getUUID()).isPresent()) return;
        PlayerState playerState = state.getPlayer(player.getUUID()).get();

        if (!state.getTeam(playerState.teamID).isPresent()) return;

        TeamState teamState = state.getTeam(playerState.teamID).get();

        teamState.storedEMC = emc;

        state.callMarkDirty();
    }

    public static void incrementEmc(Player player, long amount) {
        ServerState state = ServerState.of(player);

        if (!state.getPlayer(player.getUUID()).isPresent()) return;
        PlayerState playerState = state.getPlayer(player.getUUID()).get();

        if (!state.getTeam(playerState.teamID).isPresent()) return;
        TeamState teamState = state.getTeam(playerState.teamID).get();

        teamState.storedEMC += amount;

        state.callMarkDirty();
    }

    public static long getEmcFromPlayer(Player player) {
        Optional<TeamState> teamState = ModState.getModState(ServerUtil.getServer(player.getWorld())).getTeamByPlayer(player.getUUID());

        return teamState.map(state -> state.storedEMC).orElse(0L);
    }

    public static void syncS2C(Player player) {
        if (!player.hasNetworkHandler()) return;
        if (!player.isServerPlayerEntity()) return;

        ServerState serverState = ServerState.of(player);
        PacketByteBuf buf = PacketByteUtil.create();

        if (!serverState.getTeamByPlayer(player.getUUID()).isPresent()) return;
        TeamState teamState = serverState.getTeamByPlayer(player.getUUID()).get();

        NbtCompound nbt = NbtUtil.create();
        NbtCompound teamNBT = NbtUtil.create();

        teamState.writeNbt(teamNBT);

        NbtUtil.put(nbt, "team", teamNBT);

        PacketByteUtil.writeNbt(buf, nbt);

        ServerNetworking.send(player, _id("sync_emc"), buf);
    }

    public static void syncS2C_emc_map(Player player) {
        if (!player.hasNetworkHandler()) return;
        if (map.isEmpty()) return;

        PacketByteBuf buf = PacketByteUtil.create();
        PacketByteUtil.writeMap(buf, map);
        //System.out.println("send emc map to " + player.getName().getString());
        ServerNetworking.send(player, _id("sync_emc_map"), buf);
    }

    public static Map<String, Long> defaultEMCMap = new LinkedHashMap<>();

    public static void loadDefaultEMCs(ResourceManager resourceManager) {
        Map<CompatIdentifier, Resource> resourceIds;
        resourceIds = resourceManager.findResources("default_emcs", ".json");
        if (resourceIds == null || resourceIds.isEmpty())
            return;

        Gson gson = new Gson();
        Type listType = new TypeToken<HashMap<String, Long>>(){}.getType();

        resourceIds.forEach((resourceId, resource) -> {
            try {
                String json = resource.getContent();
                HashMap<String, Long> map = gson.fromJson(json, listType);

                if (resourceId.toString().endsWith("/tags.json")) {
                    HashMap<String, Long> tempMap = gson.fromJson(json, listType);
                    for (Map.Entry<String, Long> entry : map.entrySet()) {
                        CompatIdentifier tagId = CompatIdentifier.of(entry.getKey());
                        for (Item item : ItemUtil.getInTag(tagId)) {
                            if (tempMap.containsKey(ItemUtil.toId(item).toString())) continue;
                            tempMap.put(ItemUtil.toId(item).toString(), entry.getValue());
                        }
                    }
                    map = tempMap;
                }

                for (Map.Entry<String, Long> entry : map.entrySet()) {
                    if (ItemUtil.isExist(CompatIdentifier.of(entry.getKey()))) {
                        defaultEMCMap.put(entry.getKey(), entry.getValue());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ItemAlchemy.INSTANCE.error("Failed to read {}: " + resourceId.toString() + " " + e.getMessage());
            }
        });
    }

    public static List<EMCDef> getDefs() {
        return defs;
    }

    public static void addDef(EMCDef def) {
        defs.add(def);
    }
}
